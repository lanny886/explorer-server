package com.xyz.browser.dm.jobs

import cn.hutool.http.HttpUtil
import cn.hutool.setting.Setting
import com.alibaba.fastjson.{JSON, JSONObject}
import com.xyz.browser.common.constant.Reward
import com.xyz.browser.common.enums.WsMsgTypeEnum
import com.xyz.browser.common.model.websocket.InMessage
import com.xyz.browser.common.model.{RtBlockDto, RtTxnDto}
import com.xyz.browser.dm.entity._
import com.xyz.browser.dm.util.WebSocketUtil
import org.apache.commons.lang3.StringUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
//import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object RawStreaming {
  val ws_setting = new Setting("ws.setting")
  val restful_setting = new Setting("restful.setting")
  val db_setting = new Setting("db.setting")
  val kafka_setting = new Setting("kafka.setting")
  val phoenix_setting = new Setting("phoenix.setting")
  def main(args: Array[String]) {
    var webSocketClient = WebSocketUtil.webSocketClient(ws_setting.getStr("url"))

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkConf = new SparkConf().setAppName("RawStreaming")
      .set("spark.streaming.kafka.consumer.poll.ms", "10000")
//      .set("spark.streaming.kafka.maxRatePerPartition","1")  //v/s

    val ssc = new StreamingContext(sparkConf, Seconds(30))

    val topicVns = kafka_setting.getStr("topicVns")
    val topicContract = kafka_setting.getStr("topicContract")
    val topicBancor = kafka_setting.getStr("topicBancor")

    val topics = Array(topicVns,topicContract,topicBancor)
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> kafka_setting.getStr("bootstrapServers"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> kafka_setting.getStr("groupId"),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //init start offset
//    val newOffset = Map(new TopicPartition(topics(0), 0) -> 2627882L)//2380800L

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))
//      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams,newOffset))

    val ss = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()
    import ss.implicits._

    kafkaStream.foreachRDD(rdd => {

      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//      rdd.foreachPartition { iter =>
//        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
//        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
//      }

//      val osrs = Array[OffsetRange](OffsetRange(topics(0),0,2380779L,2380779L))

      val records = rdd.flatMap(r => {
        val topic = r.topic()
        val partition = r.partition().toString
        val key = r.key()
        val log = r.value()
        val offset = r.offset();

        var result:List[Record]=null
        try {
          val obj = JSON.parseObject(log);
          if(topicVns.equals(topic)){
            val resultObj = obj.getJSONObject("result")
            if(resultObj!=null){
              if (resultObj.containsKey("hash")) {//block
                result =  parseBlock(resultObj).toList
              } else if (resultObj.containsKey("transactionHash")) {//transaction
                result = parseTransaction(resultObj)::Nil
              }
            }
          }else if(topicContract.equals(topic)){
            if(obj.containsKey("contract"))//contract
              result = parseContract(obj)::Nil
          }else if(topicBancor.equals(topic)){
              result = parseBancor(obj)::Nil
          }

        }catch{
          case e: Exception =>
            println("err occur:"+e.getMessage)
            e.printStackTrace()
            result = Err(topic,partition,offset.toString,key,log, e.getMessage,System.currentTimeMillis().toString) :: Nil
        }
        if(result == null){
          result = Err(topic,partition,offset.toString,key,log, "unknow type",System.currentTimeMillis().toString) :: Nil
        }
        result
      })

      records.persist(StorageLevel.MEMORY_AND_DISK)

      val blocks = records.filter(_.isInstanceOf[Block]).map(_.asInstanceOf[Block]).toDF()

      blocks.persist(StorageLevel.MEMORY_AND_DISK)

      blocks.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".block"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()

      val btrans = records.filter(_.isInstanceOf[Btransaction]).map(_.asInstanceOf[Btransaction]).toDF()
      btrans.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".btransaction"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()

      val trans = records.filter(_.isInstanceOf[Transaction]).map(_.asInstanceOf[Transaction]).toDF()

      trans.persist(StorageLevel.MEMORY_AND_DISK)

      trans.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".transaction"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()


      val contracts = records.filter(_.isInstanceOf[Contract]).map(_.asInstanceOf[Contract]).toDF()
      contracts.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".contract"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()

      val bancors = records.filter(_.isInstanceOf[Bancor]).map(_.asInstanceOf[Bancor]).toDF()
      bancors.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".bancor"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()



      val errors = records.filter(_.isInstanceOf[Err]).map(_.asInstanceOf[Err]).toDF
      errors.write
        .format("org.apache.phoenix.spark")
        .mode(SaveMode.Overwrite)
        .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".err2"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
        .save()


      val rtBlocks = blocks.select("number","miner","transactions","timestamp","uncles","gasUsed","gasLimit","hash").rdd.map(r=>{
        val n = r.getAs[String]("number")
        val number = java.lang.Long.valueOf(n.substring(2),16);

        val hash = r.getAs[String]("hash")

        val miner = r.getAs[String]("miner")
        val transactions = r.getAs[String]("transactions")
        val txnArr = JSON.parseArray(transactions)

        val t = r.getAs[String]("timestamp")//"0x5c8928ff"
        val timestamp = java.lang.Long.valueOf(t.substring(2),16).toString;
        var totalGasPrice =new java.math.BigDecimal("0.0");
        val txns = scala.collection.JavaConversions.seqAsJavaList(
        for(i <- 0 until txnArr.size()) yield {
          val obj = txnArr.getJSONObject(i)
          val rtTxnDto = new RtTxnDto()
          rtTxnDto.setHash(obj.getString("hash"))
          rtTxnDto.setBlockNumber(java.lang.Long.valueOf(obj.getString("blockNumber").substring(2),16))
          rtTxnDto.setFrom(obj.getString("from"))
          rtTxnDto.setTo(obj.getString("to"))
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

          rtTxnDto.setValue(value)
          rtTxnDto.setT(timestamp)
          rtTxnDto.setTxnFee(txnFee)
          rtTxnDto.setBlockHash(obj.getString("blockHash"))
          rtTxnDto
        })

        val uncs = r.getAs[String]("uncles")
        val arrUnc = JSON.parseArray(uncs,classOf[String])
        val gu = r.getAs[String]("gasUsed")
        val gasUsed = new java.math.BigInteger(gu.substring(2),16).toString
        val gl = r.getAs[String]("gasLimit")
        val gasLimit = new java.math.BigInteger(gl.substring(2),16).toString
        val avgGasPrice = if(txnArr.size() == 0) "0.0" else totalGasPrice.divide(new java.math.BigDecimal(txnArr.size().toString),java.math.BigDecimal.ROUND_HALF_UP).toString()



        val rtBlockDto = new RtBlockDto(number,miner,txns,Reward.BLOCK_REWARD,timestamp,arrUnc,gasUsed,gasLimit,avgGasPrice,hash);
        rtBlockDto
      }).sortBy(_.getNumber).collect();

      val rtTxnStatus = trans.select("transactionHash","status").rdd.map(r=>{
        val hash = r.getAs[String]("transactionHash").toString
        val s = r.getAs[String]("status")
        val status = Integer.valueOf(s.substring(2),16).toString
        val rtTxnDto = new RtTxnDto()
        rtTxnDto.setHash(hash)
        rtTxnDto.setStatus(status)
        rtTxnDto
      }).collect()

      records.unpersist()
      blocks.unpersist()
      trans.unpersist()
      /*val contract = contracts.select("total","decimal","name","symbol","asset","hash","blockNumber","contract","tokenStandard","tokenAction","tfrom","tto").rdd.map(r=>{
        val total = r.getAs[String]("total")
        val decimal = r.getAs[String]("decimal")
        val name = r.getAs[String]("name")
        val symbol = r.getAs[String]("symbol")
        val asset = r.getAs[String]("asset")
        val hash = r.getAs[String]("hash")
        val blockNumber = r.getAs[String]("blockNumber")
        val contract = r.getAs[String]("contract")
        val tokenStandard = r.getAs[String]("tokenStandard")
        val tokenAction = r.getAs[String]("tokenAction")
        val tfrom = r.getAs[String]("tfrom")
        val tto = r.getAs[String]("tto")
        val contractDto = new ContractDto()
        contractDto.setTotal(total)
        contractDto.setDecimal(decimal)
        contractDto.setName(name)
        contractDto.setSymbol(symbol)
        contractDto.setAsset(asset)
        contractDto.setHash(hash)
        contractDto.setBlockNumber(blockNumber)
        contractDto.setContract(contract)
        contractDto.setTokenStandard(tokenStandard)
        contractDto.setTokenAction(tokenAction)
        contractDto.setTfrom(tfrom)
        contractDto.setTto(tto)
        contractDto
      }).collect()*/

      println("websocketclient status:"+webSocketClient.getReadyState)



      val blockSync = ArrayBuffer[String]();
      val size=200
      var num = if (rtBlocks.length % size == 0) rtBlocks.length / size else rtBlocks.length / size + 1
      for(i<-1 to num) {
        val start = (i - 1) * size;
        val end = if (i * size > rtBlocks.length) rtBlocks.length else i * size;
        val blockPatch = rtBlocks.slice(start, end)
        val hashs = blockPatch.map(_.getHash)
        try {
          val post = HttpUtil.createPost(restful_setting.getStr("blockUrl"))
          post.contentType("application/json")
          post.body(JSON.toJSON(blockPatch.toList.asJava).toString)
          val response = post.execute
          if (response.getStatus == 200) {
            val obj = JSON.parseObject(response.body)
            val ret = obj.getInteger("ret")
            if(ret == 0){
              val resultObj = obj.getJSONObject("result")
              val failed = resultObj.getJSONArray("failed")
              for (i <- 0 until failed.size()) {
                blockSync += failed.getString(i)
              }
            }else{
              blockSync ++= hashs;
            }
          } else {
            blockSync ++= hashs;
          }
        } catch {
          case e: Exception =>
            blockSync ++= hashs;
        }
        val failedSets = blockSync.toSet
        for (b <- blockPatch) {
          if (!failedSets.contains(b.getHash) && webSocketClient.isOpen) {
            val im = new InMessage(WsMsgTypeEnum.LATEST_BLOCK.getName, ws_setting.getStr("h5ClientToken"))
              .addData("block", b);
            webSocketClient.send(im.toJson)
          }
        }
      }
      if(blockSync.length>0){
        ss.createDataset(blockSync).toDF("hash").write
          .format("org.apache.phoenix.spark")
          .mode(SaveMode.Overwrite)
          .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+".block_sync"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
          .save()
      }

      val txnStatusSync = ArrayBuffer[String]();
//      val size=500
      num = if (rtTxnStatus.length % size == 0) rtTxnStatus.length / size else rtTxnStatus.length / size + 1
      for(i<-1 to num) {
        val start = (i - 1) * size;
        val end = if (i * size > rtTxnStatus.length) rtTxnStatus.length else i * size;
        val txnStatusPatch = rtTxnStatus.slice(start, end)
        val hashs = txnStatusPatch.map(_.getHash)
        try {
          val post = HttpUtil.createPost(restful_setting.getStr("txnUrl"))
          post.contentType("application/json")
          post.body(JSON.toJSON(txnStatusPatch.toList.asJava).toString)
          val response = post.execute
          if (response.getStatus == 200) {
            val obj = JSON.parseObject(response.body)
            val ret = obj.getInteger("ret")
            if(ret == 0){
              val resultObj = obj.getJSONObject("result")
              val failed = resultObj.getJSONArray("failed")
              for (i <- 0 until failed.size()) {
                txnStatusSync += failed.getString(i)
              }
            }else{
              txnStatusSync ++= hashs;
            }
          } else {
            txnStatusSync ++= hashs;
          }
        } catch {
          case e: Exception =>
            txnStatusSync ++= hashs;
        }
      }
      if(txnStatusSync.length>0) {
        ss.createDataset(txnStatusSync).toDF("transactionHash").write
          .format("org.apache.phoenix.spark")
          .mode(SaveMode.Overwrite)
          .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+ ".transaction_sync"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
          .save()
      }
      /*val contractSync =
        (for(s <-contract)yield{
          var hash:String = null
          val im = new InMessage(WsMsgTypeEnum.LATEST_CONTRACT.getName,ws_setting.getStr("h5ClientToken"))
            .addData("contract",s);
          if(webSocketClient.isOpen) {
            webSocketClient.send(im.toJson)
          }else
            hash = s.getHash
          hash
        }).filter(f=>f!=null)

      if(contractSync.length>0) {
        ss.createDataset(contractSync).toDF("hash").write
          .format("org.apache.phoenix.spark")
          .mode(SaveMode.Overwrite)
          .options(Map("table" -> (phoenix_setting.getStr("phoenixSchem")+ ".contract_sync"), "zkUrl" -> phoenix_setting.getStr("zkUrl")))
          .save()
      }*/

      // some time later, after outputs have completed
      kafkaStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    })

    ssc.start()
    print("wait termination")

    try{
      ssc.awaitTermination()
    }finally{
      println("closing websocketClient");
      webSocketClient.setForcedClose(true);
      webSocketClient.close();
    }
    ss.stop()



  }

  def parseBlock(resultObj:JSONObject):ArrayBuffer[Record]={
    val arr  = new ArrayBuffer[Record]()
    val difficulty = resultObj.getString("difficulty")
    val extraData = resultObj.getString("extraData")
    val gasLimit = resultObj.getString("gasLimit")
    val gasUsed = resultObj.getString("gasUsed")
    val hash = resultObj.getString("hash")
    val logsBloom = resultObj.getString("logsBloom")
    val miner = resultObj.getString("miner")
    val mixHash = resultObj.getString("mixHash")
    val nonce = resultObj.getString("nonce")
    val number = resultObj.getString("number")
    val parentHash = resultObj.getString("parentHash")
    val receiptsRoot = resultObj.getString("receiptsRoot")
    val sha3Uncles = resultObj.getString("sha3Uncles")
    val size = resultObj.getString("size")
    val stateRoot = resultObj.getString("stateRoot")
    val timestamp = resultObj.getString("timestamp")
    val totalDifficulty = resultObj.getString("totalDifficulty")
    val transactions = resultObj.getJSONArray("transactions");
    val transactionsJsonStr = transactions.toJSONString
    val transactionsRoot = resultObj.getString("transactionsRoot")
    val unclesJsonStr = resultObj.getJSONArray("uncles").toJSONString
    arr+=Block(
      difficulty ,
      extraData ,
      gasLimit ,
      gasUsed ,
      hash ,
      logsBloom ,
      miner ,
      mixHash ,
      nonce ,
      number ,
      parentHash ,
      receiptsRoot ,
      sha3Uncles ,
      size ,
      stateRoot ,
      timestamp ,
      totalDifficulty ,
      transactionsJsonStr ,
      transactionsRoot ,
      unclesJsonStr
    )
    for(i<-0 until transactions.size()){
      val transObj = transactions.getJSONObject(i)
      val blockHash = transObj.getString("blockHash")
      val blockNumber = transObj.getString("blockNumber")
      val tfrom = transObj.getString("from")
      val gas = transObj.getString("gas")
      val gasPrice = transObj.getString("gasPrice")
      val hash = transObj.getString("hash")
      val input = transObj.getString("input")
      val nonce = transObj.getString("nonce")
      val tto = transObj.getString("to")
      val transactionIndex = transObj.getString("transactionIndex")
      val tvalue = transObj.getString("value")
      val v = transObj.getString("v")
      val r = transObj.getString("r")
      val s = transObj.getString("s")
      arr +=Btransaction(
        blockHash,
        blockNumber,
        tfrom,
        gas,
        gasPrice,
        hash,
        input,
        nonce,
        tto,
        transactionIndex,
        tvalue,
        v,
        r,
        s,
        timestamp
      )
    }
    arr
  }
  def parseTransaction(resultObj:JSONObject):Record={
    val blockHash = resultObj.getString("blockHash")
    val blockNumber = resultObj.getString("blockNumber")
    val contractAddress = resultObj.getString("contractAddress")
    val cumulativeGasUsed = resultObj.getString("cumulativeGasUsed")
    val from = resultObj.getString("from")
    val gasUsed = resultObj.getString("gasUsed")
    val logs = resultObj.getJSONArray("logs").toJSONString
    val logsBloom = resultObj.getString("logsBloom")
    val status = resultObj.getString("status")
    val to = resultObj.getString("to")
    val transactionHash = resultObj.getString("transactionHash")
    val transactionIndex = resultObj.getString("transactionIndex")
    Transaction(
      blockHash ,
      blockNumber ,
      contractAddress ,
      cumulativeGasUsed ,
      from ,
      gasUsed ,
      logs ,
      logsBloom ,
      status ,
      to ,
      transactionHash ,
      transactionIndex
    )

  }

  def parseContract(obj:JSONObject):Record={
    val total = obj.getString("total")
    val decimal = obj.getInteger("decimal").toString
    val name = obj.getString("name")
    val symbol = obj.getString("symbol")
    val asset = obj.getString("asset")
    val hash = obj.getString("hash")
    val blockNumber = obj.getString("blockNumber")
    val contract = obj.getString("contract")
    val tokenStandard = obj.getString("tokenStandard")
    val tokenAction = obj.getString("tokenAction")
    val tfrom = obj.getString("from")
    val tto = obj.getString("to")
    Contract(
      total ,
      decimal ,
      name ,
      symbol ,
      asset ,
      hash ,
      blockNumber ,
      contract ,
      tokenStandard ,
      tokenAction ,
      tfrom ,
      tto
    )

  }
  def parseBancor(obj:JSONObject):Record={
    val hash = obj.getString("hash")
    val tfrom = obj.getString("from")
    val contract = obj.getString("contract")
    val action = obj.getString("action")
    val ttype = obj.getInteger("type").toString
    var name:String = null
    var tfunction:String = null
    var param:String = null
    var input:String = null
    if("create".equals(action)){
      name = obj.getString("name")
    }else if("run".equals(action)){
      name = obj.getString("name")
      tfunction = obj.getString("function")
      val pa = obj.getJSONArray("param")
      param = if(pa!=null) pa.toJSONString else null
      input = obj.getString("input")
    }

    Bancor(
      hash ,
      tfrom ,
      contract ,
      action ,
      ttype ,
      name ,
      tfunction ,
      param ,
      input
    )

  }
}



