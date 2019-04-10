package com.xyz.browser.dm.jobs

import java.util.Properties

import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.setting.Setting
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object CheckData {
  val sdf = FastDateFormat.getInstance("yyyy-MM-dd")
  val phoenix_setting = new Setting("phoenix.setting")
  val db_setting = new Setting("db.setting")
  def main(args: Array[String]) {
    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("CheckData")

    val sc = new SparkContext(sparkConf)

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    val properties = new Properties()
    properties.put("user",db_setting.getStr("user"))
    properties.put("password",db_setting.getStr("pass"))
    properties.setProperty("driver",db_setting.getStr("driver"))
    val mysqlDf = ss.read.jdbc(db_setting.getStr("url"),"b_rt_block",properties)
    val mhashs = mysqlDf.select("hash")
    val df = ss.sqlContext.read
    .format("org.apache.phoenix.spark")
    .option("table", "vns.block")
    .option("zkUrl", zkUrl)
    .load()

    val hashs = df.select("hash")

    val losthashs = hashs.except(mhashs)
    losthashs.persist(StorageLevel.MEMORY_AND_DISK)
    val count = losthashs.count();
    println("losthashs count:"+count)
    val list = losthashs.collect()
    println("list start")
    for(s<-list){
      println(s)
    }
    println("list end")

    ss.stop()

    sc.stop()


  }

}



