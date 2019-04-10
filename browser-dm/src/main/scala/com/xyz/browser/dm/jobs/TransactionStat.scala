package com.xyz.browser.dm.jobs

import java.util.Calendar

import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.setting.Setting
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object TransactionStat {
  val sdf = FastDateFormat.getInstance("yyyy-MM-dd")

  val phoenix_setting = new Setting("phoenix.setting")
  def main(args: Array[String]) {
    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH,-1)
    cal.set(Calendar.HOUR_OF_DAY,0)
    cal.set(Calendar.MINUTE,0);
    cal.set(Calendar.SECOND,0);
    cal.set(Calendar.MILLISECOND,0);
    val flag = sdf.format(cal.getTime)
    val hexStart = Integer.toHexString((cal.getTimeInMillis/1000).toInt)
    cal.set(Calendar.HOUR_OF_DAY,23)
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    cal.set(Calendar.MILLISECOND,999);
    val hexEnd = Integer.toHexString((cal.getTimeInMillis/1000).toInt)



    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("TransactionStat")
    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()
    val df = ss.sqlContext.read
    .format("org.apache.phoenix.spark")
    .option("table", "vns.btransaction")
    .option("zkUrl", zkUrl)
    .load()

    val trans = df.select("timestamp").filter(f=>{
      val timestamp = f.getAs[String]("timestamp")//"0x5c8928ff"
      val t = timestamp.substring(2)
      if(t>=hexStart && t<=hexEnd)
        true
      else
        false
    })

    val size = trans.count()

    ss.stop()


  }

}



