package com.xyz.browser.dm.jobs


import cn.hutool.db.{DbUtil, Entity}
import cn.hutool.setting.Setting
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
      val total = new java.math.BigDecimal(f.getAs[String]("total"))
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
      val total = new java.math.BigDecimal(f.getAs[String]("total"))
      val contract = f.getAs[String]("contract")
      if (tokenAction.equals("create")) {
        (contract, from, total)
      } else{
        null
      }

    }).filter(f=>f!=null).toDF("contract","from","total").withColumnRenamed("from","createAddress")


    createMap.persist(StorageLevel.MEMORY_AND_DISK)

    val from = transferMap.select("contract","from","total").withColumnRenamed("from","address").groupBy("address","contract").agg(sum("total").as("fromTotal"))

    val to = transferMap.select("contract","to","total").withColumnRenamed("to","address").groupBy("address","contract").agg(sum("total").as("toTotal"))


    val addressAsset = from.join(to,Seq("address","contract"),"outer").map(f=>{
      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      var fromTotal = f.getAs[java.math.BigDecimal]("fromTotal")
      var toTotal = f.getAs[java.math.BigDecimal]("toTotal")

      if(fromTotal == null)
        fromTotal = java.math.BigDecimal.ZERO;
      if(toTotal == null)
        toTotal = java.math.BigDecimal.ZERO;
      val asset = fromTotal.subtract(toTotal).abs()
      (contract,address,asset)
    }).toDF("contract", "address", "asset")

    addressAsset.persist(StorageLevel.MEMORY_AND_DISK)

    val statistics = createMap.join(addressAsset,Seq("contract"),"outer").map(f=>{

      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      var asset = f.getAs[java.math.BigDecimal]("asset")
      var total = f.getAs[java.math.BigDecimal]("total")

      var percentage = new java.math.BigDecimal("0.0")

      if(asset == null)
        asset = java.math.BigDecimal.ZERO;
      if(total != null) {
        percentage = asset.divide(total).multiply(new java.math.BigDecimal("100"))
      }

      (contract,address,asset,percentage)

    }).toDF("contract","address","asset","percentage").collect()



    val holders_asset = statistics.map(f=>{
      val contract = f.getAs[String]("contract")
      val address = f.getAs[String]("address")
      val asset = f.getAs[java.math.BigDecimal]("asset").toString
      val percentage = f.getAs[java.math.BigDecimal]("percentage").toString
      Entity.create("s_holders_asset").set("contract",contract).set("address",address).set("asset",asset).set("percentage",percentage)
    })


    DbUtil.use().execute("truncate table s_holders_asset")
    DbUtil.use().insert(holders_asset.toList.asJava)

    ss.stop()
    sc.stop()

  }


}
