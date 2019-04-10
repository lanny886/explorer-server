package com.xyz.browser.dm.jobs

import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.setting.Setting
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object CheckNumber {
  val sdf = FastDateFormat.getInstance("yyyy-MM-dd")
  val phoenix_setting = new Setting("phoenix.setting")
  def main(args: Array[String]) {

    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("CheckNumber")

    val sc = new SparkContext(sparkConf)

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    val df = ss.sqlContext.read
    .format("org.apache.phoenix.spark")
    .option("table", "vns.block")
    .option("zkUrl", zkUrl)
    .load()

    val numbers = df.select("number").rdd.map(f=>{
      val number = new java.math.BigInteger(f.getAs[String]("number").substring(2),16).intValue()
      number
    })
    numbers.persist(StorageLevel.MEMORY_AND_DISK)

    val maxNumber = numbers.max()
    val ranges = sc.parallelize(0 to maxNumber).subtract(numbers).map(f=>{
//      Integer.inf
      "0x"+Integer.toHexString(f)
    })

    ranges.saveAsTextFile("/changmi/test/lostNumber")

    ss.stop()
    sc.stop()

  }

}



