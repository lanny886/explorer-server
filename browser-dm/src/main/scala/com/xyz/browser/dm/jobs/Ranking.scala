package com.xyz.browser.dm.jobs

import java.math.BigDecimal

import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.db.{DbUtil, Entity}
import cn.hutool.setting.Setting
import com.alibaba.fastjson.JSON
import com.xyz.browser.common.constant.Reward
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

object Ranking {
  val phoenix_setting = new Setting("phoenix.setting")
  val sdf = FastDateFormat.getInstance("yyyy-MM-dd")

  def main(args: Array[String]) {

    val zkUrl = phoenix_setting.getStr("zkUrl")
    val phoenixSchem = phoenix_setting.getStr("phoenixSchem")


    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("Ranking")

    val sc = new SparkContext(sparkConf)

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    import ss.implicits._

    val blockdf = ss.sqlContext.read
      .format("org.apache.phoenix.spark")
      .option("table", "vns.block")
      .option("zkUrl", zkUrl)
      .load()

    val bdfs = blockdf.select("hash","miner","uncles").flatMap(f=>{
      val hash = f.getAs[String]("hash")
      val miner = f.getAs[String]("miner")
      val arr = ArrayBuffer[(String,String,String)]()
      val uncs = f.getAs[String]("uncles")

      val arrUnc = JSON.parseArray(uncs)
      for(i<- 0 until arrUnc.size()){
        arr += (("uncle",arrUnc.getString(i),""))
      }
      arr +=(("",hash,miner))
      arr
    }).toDF("flag","hash","miner")

    bdfs.persist(StorageLevel.MEMORY_AND_DISK)

    val unclehashs = bdfs.filter(_.getAs[String]("flag") == "uncle")
        .select("hash").distinct()

    unclehashs.persist(StorageLevel.MEMORY_AND_DISK)

    val blockhashs = bdfs.select("hash").except(unclehashs)

    val uncleblocks = bdfs.select("hash","miner").join(unclehashs,"hash")
      .drop("hash").map(f=>{(f.getAs[String]("miner"),new java.math.BigDecimal(Reward.UNCLES_REWARD))})
      .toDF("address","ur")
      .groupBy("address").agg(sum("ur").as("ur"))

    uncleblocks.persist(StorageLevel.MEMORY_AND_DISK)

    val blocks = bdfs.select("hash","miner").join(blockhashs,"hash")
        .drop("hash").map(f=>{(f.getAs[String]("miner"),new java.math.BigDecimal(Reward.BLOCK_REWARD))})
      .toDF("address","br")
      .groupBy("address").agg(sum("br").as("br"))

    blocks.persist(StorageLevel.MEMORY_AND_DISK)

    val btxndf = ss.sqlContext.read
    .format("org.apache.phoenix.spark")
    .option("table", "vns.btransaction")
    .option("zkUrl", zkUrl)
    .load()

    val txndf = ss.sqlContext.read
      .format("org.apache.phoenix.spark")
      .option("table", "vns.transaction")
      .option("zkUrl", zkUrl)
      .load()

    val btxns = btxndf.select("hash","tfrom","tto","gas","gasPrice","tvalue")
        .map(f=>{
          val hash = f.getAs[String]("hash")
          val from = f.getAs[String]("tfrom")
          val to = f.getAs[String]("tto")
          val gas = new java.math.BigInteger(f.getAs[String]("gas").substring(2), 16)
          val gasPrice = new java.math.BigInteger(f.getAs[String]("gasPrice").substring(2), 16)
          val txnFee = new java.math.BigDecimal(gas.multiply(gasPrice)).divide(new BigDecimal("1000000000000000000"))
          val value = new java.math.BigDecimal(new java.math.BigInteger(f.getAs[String]("tvalue").substring(2), 16).toString).divide(new BigDecimal("1000000000000000000"))
          (hash,from,to,value,txnFee)
        }).toDF("hash","from","to","value","txnFee")

    val txns = txndf.select("transactionHash","status").map(f=>{
      val hash = f.getAs[String]("transactionHash")
      val status = Integer.valueOf(f.getAs[String]("status").substring(2),16)
      (hash,status)
    }).toDF("hash","status").filter(_.getAs[Integer]("status") == 1)
    val tts = btxns.join(txns,Seq("hash"))
      .select("hash","from","to","value","txnFee")

    tts.persist(StorageLevel.MEMORY_AND_DISK)
    val out = tts.select("from","value","txnFee").map(f=>{
      val address = f.getAs[String]("from")
      val value = f.getAs[java.math.BigDecimal]("value")
      val txnFee = f.getAs[java.math.BigDecimal]("txnFee")
      val v = value.add(txnFee)
      (address,v)
    }).toDF("address","ov")
      .groupBy("address").agg(sum("ov").as("ov"))

    val in = tts.select("to","value")
      .withColumnRenamed("to","address")
      .withColumnRenamed("value","iv")
      .groupBy("address").agg(sum("iv").as("iv"))

    val assetRanking = in.join(out,Seq("address"),"outer").join(blocks,Seq("address"),"outer").join(uncleblocks,Seq("address"),"outer")
      .map(f=>{
        val address = f.getAs[String]("address")
        var ov = f.getAs[java.math.BigDecimal]("ov")
        if(ov ==null)
          ov = java.math.BigDecimal.ZERO;
        var iv = f.getAs[java.math.BigDecimal]("iv")
        if(iv ==null)
          iv = java.math.BigDecimal.ZERO;
        var br = f.getAs[java.math.BigDecimal]("br")
        if(br ==null)
          br = java.math.BigDecimal.ZERO;
        var ur = f.getAs[java.math.BigDecimal]("ur")
        if(ur ==null)
          ur = java.math.BigDecimal.ZERO;
        val asset = iv.subtract(ov).add(br).add(ur)
        (address,asset)
      }).toDF("address","asset").filter(f=>StringUtils.isNotBlank(f.getAs[String]("address"))).orderBy(desc("asset")).head(100)

    val ranking_asset = assetRanking.map(f=>{
      val address = f.getAs[String]("address")
      val asset = f.getAs[java.math.BigDecimal]("asset").toString
      Entity.create("s_ranking_asset").set("address",address).set("asset",asset);
    })

    DbUtil.use().execute("truncate table s_ranking_asset")
    DbUtil.use().insert(ranking_asset.toList.asJava)

    val minerRanking = uncleblocks.join(blocks,Seq("address"),"outer")
      .map(f=>{
        val address = f.getAs[String]("address")
        var br = f.getAs[java.math.BigDecimal]("br")
        if(br ==null)
          br = java.math.BigDecimal.ZERO;
        var ur = f.getAs[java.math.BigDecimal]("ur")
        if(ur ==null)
          ur = java.math.BigDecimal.ZERO;
        val totalReward = br.add(ur)
        (address,br,ur,totalReward)
      }).toDF("address","blockReward","uncleReward","totalReward").filter(f=>StringUtils.isNotBlank(f.getAs[String]("address"))).orderBy(desc("totalReward")).head(100)

    val ranking_miner = minerRanking.map(f=>{
      val address = f.getAs[String]("address")
      val blockReward = f.getAs[java.math.BigDecimal]("blockReward").toString
      val uncleReward = f.getAs[java.math.BigDecimal]("uncleReward").toString
      val totalReward = f.getAs[java.math.BigDecimal]("totalReward").toString
      Entity.create("s_ranking_miner")
        .set("address",address)
        .set("block_reward",blockReward)
        .set("uncle_reward",uncleReward)
        .set("total_reward",totalReward);
    })
    DbUtil.use().execute("truncate table s_ranking_miner")
    DbUtil.use().insert(ranking_miner.toList.asJava)

    ss.stop()
    sc.stop()

  }

}



