package com.xyz.browser.dm.jobs

import java.util.Properties

import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.setting.Setting
import com.alibaba.fastjson.JSON
import com.xyz.browser.common.constant.Reward
import com.xyz.browser.dm.entity._
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.ArrayBuffer

object DataInit {
  val sdf = FastDateFormat.getInstance("yyyy-MM-dd")
  val phoenix_setting = new Setting("phoenix.setting")
  val db_setting = new Setting("db.setting")
  def main(args: Array[String]) {
    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")


    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("DataInit")
    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()
    val blocks = ss.sqlContext.read
    .format("org.apache.phoenix.spark")
    .option("table", "vns.block")
    .option("zkUrl", zkUrl)
    .load()

    val mydata = blocks.select("number","miner","transactions","timestamp","uncles","gasUsed","gasLimit","hash").rdd.flatMap(r=>{

      val arr  = new ArrayBuffer[Record]()
      val n = r.getAs[String]("number")
      val number = java.lang.Long.valueOf(n.substring(2),16);

      val hash = r.getAs[String]("hash")

      val miner = r.getAs[String]("miner")
      val transactions = r.getAs[String]("transactions")
      val txnArr = JSON.parseArray(transactions)

      //        val txns = JSON.parseArray[RtTxnDto](transactions,classOf[RtTxnDto])
      val t = r.getAs[String]("timestamp")//"0x5c8928ff"
      val timestamp = java.lang.Long.valueOf(t.substring(2),16).toString;
      var totalGasPrice =new java.math.BigDecimal("0.0");
      val txns = scala.collection.JavaConversions.seqAsJavaList(
        for(i <- 0 until txnArr.size()) yield {
          val obj = txnArr.getJSONObject(i)
          val v = obj.getString("value")
          val gas = new java.math.BigInteger(obj.getString("gas").substring(2),16)
          val gasPrice = new java.math.BigInteger(obj.getString("gasPrice").substring(2),16)
          val txnFee = new java.math.BigDecimal(gas.multiply(gasPrice))
            .divide(new java.math.BigDecimal("1000000000000000000"))
            .toString

          totalGasPrice = totalGasPrice.add(new java.math.BigDecimal(gasPrice))
          //1000000000000000000
          val value = new java.math.BigDecimal(new java.math.BigInteger(v.substring(2),16))
            .divide(new java.math.BigDecimal("1000000000000000000")).toString
          arr+=RtTxn(obj.getString("hash"),
            java.lang.Long.valueOf(obj.getString("blockNumber").substring(2),16),
            obj.getString("from"),
            obj.getString("to"),
            value,
            timestamp,
            txnFee,
            null,
            obj.getString("blockHash")
          )
        })

      val uncs = r.getAs[String]("uncles")
      val arrUnc = JSON.parseArray(uncs)
      for(i<- 0 until arrUnc.size()){
        arr += RtUncle(null,number.toString,null,Reward.UNCLES_REWARD,null,arrUnc.getString(i));
      }
      val uncles = arrUnc.size().toString;
      val gu = r.getAs[String]("gasUsed")
      val gasUsed = new java.math.BigInteger(gu.substring(2),16).toString
      val gl = r.getAs[String]("gasLimit")
      val gasLimit = new java.math.BigInteger(gl.substring(2),16).toString
      val avgGasPrice = if(txnArr.size() == 0) "0.0" else totalGasPrice.divide(new java.math.BigDecimal(txnArr.size().toString),java.math.BigDecimal.ROUND_HALF_UP).toString()
      val rtBlock = RtBlock(number.toString,
        miner,
        Reward.BLOCK_REWARD,
        timestamp,
        txns.size().toString,
        uncles,
        gasUsed,
        gasLimit,
        avgGasPrice,
        hash
      )
      arr+=rtBlock;
      arr
    });
    mydata.persist(StorageLevel.MEMORY_AND_DISK)


    import ss.implicits._
    val myblocks = mydata.filter(_.isInstanceOf[RtBlock]).map(_.asInstanceOf[RtBlock]).toDF()
    myblocks.persist(StorageLevel.MEMORY_AND_DISK)
//    println("myblocks size:"+myblocks.count())

    val pros = new Properties()
    pros.setProperty("user",db_setting.getStr("user"))
    pros.setProperty("password",db_setting.getStr("pass"))
    pros.setProperty("driver",db_setting.getStr("driver"))


    val uncles = mydata.filter(_.isInstanceOf[RtUncle]).map(_.asInstanceOf[RtUncle]).toDF().select("hash","block_number","reward")
          .rdd
      .map(f=>{
            val blockNumber = f.getAs[String]("block_number").toLong
            val hash = f.getAs[String]("hash")
            val reward = f.getAs[String]("reward")
            (hash,(blockNumber,reward))
          }).reduceByKey((a,b)=>{
        if(a._1 > b._1)
          a
        else
          b
    }).map(f=>{
      (f._1,f._2._1.toString,f._2._2)
    }).toDF("hash","block_number","reward")

    uncles.persist(StorageLevel.MEMORY_AND_DISK)

    val unclehashs = uncles.select("hash")

    unclehashs.persist(StorageLevel.MEMORY_AND_DISK)

    val blockhashs = myblocks.select("hash").except(unclehashs)

//    number:Long,
//    miner:String,
//    block_number:Long,
//    reward:String,
//    t:String,
//    hash:String
    val uncleblocks = myblocks.select("number","miner","t","hash").join(unclehashs,"hash")

    val dbUnces = uncles.join(uncleblocks,Seq("hash"),"left").rdd.map(f=>{
      RtUncle(f.getAs[String]("number"),
        f.getAs[String]("block_number"),
        f.getAs[String]("miner"),
        f.getAs[String]("reward"),
        f.getAs[String]("t"),
        f.getAs[String]("hash")
      )
    }).toDF()

    myblocks.join(blockhashs,"hash").write.mode(SaveMode.Append)
      .jdbc(db_setting.getStr("url"),"b_rt_block",pros)

    dbUnces.write.mode(SaveMode.Append)
      .jdbc(db_setting.getStr("url"),"b_rt_uncle",pros)

    val mybtxns = mydata.filter(_.isInstanceOf[RtTxn]).map(_.asInstanceOf[RtTxn]).map(f=>{
      ((f.hash,f.block_hash),f)
    })

    val d1 = ss.sqlContext.read
      .format("org.apache.phoenix.spark")
      .option("table", "vns.transaction")
      .option("zkUrl", zkUrl)
      .load()
    val d2 = d1.select("transactionHash","blockHash","status").rdd.map(r=>{
      val hash = r.getAs[String]("transactionHash")
      val blockHash = r.getAs[String]("blockHash")
      val s = r.getAs[String]("status")
      val status = Integer.valueOf(s.substring(2),16).toString
      ((hash,blockHash),status)
    })
    val transactions = mybtxns.leftOuterJoin(d2).map(f=>{
      val hash = f._1._1
      val txn = f._2._1
      val status = f._2._2.getOrElse(null)
      txn.status = status
      (hash,txn)
    })
        .reduceByKey((a,b)=>{
          if(a.block_number > b.block_number)
            a
          else
            b
        })
        .map(_._2)
      .toDF()
    transactions.write.mode(SaveMode.Append)
      .jdbc(db_setting.getStr("url"),"t_rt_txn",pros)

    ss.stop()
  }

}



