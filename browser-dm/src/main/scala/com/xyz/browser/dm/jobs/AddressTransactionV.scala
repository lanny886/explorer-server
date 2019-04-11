package com.xyz.browser.dm.jobs

import java.math.BigDecimal

import cn.hutool.setting.Setting
import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._




object AddressTransactionV {

  val phoenix_setting = new Setting("phoenix_setting")

  def main(args: Array[String]) {

    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("AddressTransactionV")

    val sc = new SparkContext(sparkConf)

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    val df = ss.sqlContext.read
      .format("org.apache.phoenix.spark")
      .option("table", "vns.btransaction")
      .option("zkUrl", zkUrl)
      .load()


    val btxns = df.select("tfrom","tto", "tvalue")
      .map(f=>{
        val from = f.getAs[String]("tfrom")
        val to = f.getAs[String]("tto")
        val value = new java.math.BigDecimal(new java.math.BigInteger(f.getAs[String]("tvalue").substring(2), 16).toString).divide(new BigDecimal("1000000000000000000"))
        (from,to,value)
      }).toDF("from","to","value")

    val out = btxns.select("from","value").map(f=>{
      val address = f.getAs[String]("from")
      val value = f.getAs[java.math.BigDecimal]("value")
      (address,value)
    }).toDF("address","ov")
      .groupBy("address").agg(sum("ov").as("ov"))

    val in = btxns.select("to","value")
      .withColumnRenamed("to","address")
      .withColumnRenamed("value","iv")
      .groupBy("address").agg(sum("iv").as("iv"))

    val transactionVolume = in.join(out,Seq("address"),"outer").map(f=>{
        val address = f.getAs[String]("address")
        var ov = f.getAs[java.math.BigDecimal]("ov")
        if(ov ==null)
          ov = java.math.BigDecimal.ZERO;
        var iv = f.getAs[java.math.BigDecimal]("iv")
        if(iv ==null)
          iv = java.math.BigDecimal.ZERO;
        val volume = iv.add(ov)
        (address,volume)
      }).toDF("address","volume").filter(f=>StringUtils.isNotBlank(f.getAs[String]("address")))

    transactionVolume .write.format("org.apache.phoenix.spark").mode(SaveMode.Overwrite).
      options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+ ".address_transaction"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
      .save()


    ss.stop()
    sc.stop()

  }

}
