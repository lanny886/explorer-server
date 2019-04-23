package com.xyz.browser.dm.jobs


import java.math.RoundingMode

import cn.hutool.db.{DbUtil, Entity}
import cn.hutool.setting.Setting
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._


object Holders {

  val phoenix_setting = new Setting("phoenix.setting")
  val db_addr_setting = new Setting("db_addr.setting")
  val restful_setting = new Setting("restful.setting")

  def main(args: Array[String]){

    val zkUrl = phoenix_setting.getStr("zkUrl")

    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("Holders")

    val sc = new SparkContext(sparkConf)

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    import ss.implicits._

    val contract = ss.sqlContext.read
      .format("org.apache.phoenix.spark")
      .option("table", "vns.contract")
      .option("zkUrl", zkUrl)
      .load()

    val ct = contract.select("tokenAction","tfrom", "tto", "total", "contract").rdd.map(f=>{

      val tokenAction = f.getAs[String]("tokenAction")
      val from = f.getAs[String]("tfrom")
      val to = f.getAs[String]("tto")
      val total = f.getAs[String]("total")
      val contract = f.getAs[String]("contract")
      (contract, from, to, total, tokenAction)
    }).toDF("contract","from","to","total","tokenAction")

    ct.persist(StorageLevel.MEMORY_AND_DISK)

    val transferMap = ct.select("tokenAction","from", "to", "total", "contract").rdd.map(f=>{

      val tokenAction = f.getAs[String]("tokenAction")
      val from = f.getAs[String]("from")
      val to = f.getAs[String]("to")
      val total = f.getAs[String]("total")
      val contract = f.getAs[String]("contract")
      if (tokenAction.equals("transfer")) {
        (contract, from, to, total, tokenAction)
      } else{
        null
      }

    }).filter(f=>f!=null).toDF("contract","from","to","total","tokenAction")

    transferMap.persist(StorageLevel.MEMORY_AND_DISK)

    val createMap = ct.select("tokenAction","from", "total", "contract").rdd.map(f=>{

      val tokenAction = f.getAs[String]("tokenAction")
      val from = f.getAs[String]("from")
      val total = f.getAs[String]("total")
      val contract = f.getAs[String]("contract")
      if (tokenAction.equals("create")) {
        (contract, from, total)
      } else{
        null
      }

    }).filter(f=>f!=null).toDF("contract","from","total").withColumnRenamed("from","createAddress")


    createMap.persist(StorageLevel.MEMORY_AND_DISK)

    val from = transferMap.select("contract","from","total").withColumnRenamed("from","address")
          .rdd.map(f=>{
        val contract = f.getAs[String]("contract")
        val address = f.getAs[String]("address")
        val total = new java.math.BigDecimal(f.getAs[String]("total"))
        println("fs:"+address+","+contract+","+total)
        ((address,contract),total)

      })
        .reduceByKey((a,b)=>{
          a.add(b)
        }).map(f=>{
        val fromTotal = f._2
        if(fromTotal.precision>38){
          println("from:"+f._1._1+","+f._1._2+","+f._2)
        }
      (f._1._1,f._1._2,f._2.toString)
      }).toDF("address","contract","fromTotal")//.collect()
//      .groupBy("address","contract").agg(sum("total").as("fromTotal"))

    val to = transferMap.select("contract","to","total").withColumnRenamed("to","address")
      .rdd.map(f=>{
      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      val total = new java.math.BigDecimal(f.getAs[String]("total"))
      println("ts:"+address+","+contract+","+total)
      ((address,contract),total)

    })
      .reduceByKey((a,b)=>{
        a.add(b)
      }).map(f=>{
      val toTotal = f._2
      if(toTotal.precision>38){
        println("to:"+f._1._1+","+f._1._2+","+f._2)
      }
      (f._1._1,f._1._2,f._2.toString)
    }).toDF("address","contract","toTotal")




//      .groupBy("address","contract").agg(sum("total").as("toTotal"))


    val addressAsset = from.join(to,Seq("address","contract"),"outer").map(f=>{
      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")

      var fromTotalStr = f.getAs[String]("fromTotal")
      var toTotalStr = f.getAs[String]("toTotal")
      val fromTotal =
      if(StringUtils.isBlank(fromTotalStr))
        java.math.BigDecimal.ZERO
      else
        new java.math.BigDecimal(fromTotalStr)
      val toTotal =
      if(StringUtils.isBlank(toTotalStr))
        java.math.BigDecimal.ZERO;
      else
        new java.math.BigDecimal(toTotalStr)
      val asset = fromTotal.subtract(toTotal).abs()
      (contract,address,asset.toString)
    }).toDF("contract", "address", "asset")

    addressAsset.persist(StorageLevel.MEMORY_AND_DISK)

    val statistics = createMap.join(addressAsset,Seq("contract"),"outer").map(f=>{

      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      var assetStr = f.getAs[String]("asset")
      var totalStr = f.getAs[String]("total")

      var percentage = java.math.BigDecimal.ZERO
      val asset =
      if(StringUtils.isBlank(assetStr))
        java.math.BigDecimal.ZERO
      else
        new java.math.BigDecimal(assetStr)
      if(StringUtils.isNotBlank(totalStr) ) {
        var total = new java.math.BigDecimal(totalStr)
        if(total.compareTo(java.math.BigDecimal.ZERO)!=0)
          percentage = asset.divide(total, 6, RoundingMode.HALF_UP).multiply(new java.math.BigDecimal("100"))
      }

      (contract,address,asset.toString,percentage.toString)

    }).toDF("contract","address","asset","percentage").collect()



    val holders_asset = statistics.map(f=>{
      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      val asset = f.getAs[String]("asset").toString
      val percentage = f.getAs[String]("percentage").toString
      Entity.create("s_holders_asset").set("contract",contract).set("address",address).set("asset",asset).set("percentage",percentage)
    })


    DbUtil.use().execute("truncate table s_holders_asset")
    DbUtil.use().insert(holders_asset.toList.asJava)

    ss.stop()
    sc.stop()

  }


}
