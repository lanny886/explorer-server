package com.xyz.browser.dm.util;

import cn.hutool.setting.Setting;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.math.BigInteger;
import java.util.*;

import static java.lang.System.exit;
import static sun.misc.Version.print;

/**
 * 检查spark streaming job获取失败的kafka记录
 */
public class CheckLost {

    public static void main(String[] args) {

        Setting kafka_setting = new Setting("kafka.setting");
        //modify this by found in spark err log
        // like java.lang.AssertionError: assertion failed: Failed to get records for spark-executor-kafka_consumer_browser VNS 0 5293938 after polling for 10000
        //
//        1686041
        long offsetStart=41143;//(include)
        long offsetEnd=41162;//(exclude)

        //Kafka consumer configuration settings
        String topicName = kafka_setting.getStr("topic");
        String bootstrap_servers=kafka_setting.getStr("bootstrapServers");
        Properties props = new Properties();

        props.put("bootstrap.servers", bootstrap_servers);
        props.put("group.id", "test1");
        props.put("enable.auto.commit", "false");
//        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");

        //要发送自定义对象，需要指定对象的反序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, Object> consumer = new KafkaConsumer<String, Object>(props);
        Map<TopicPartition, OffsetAndMetadata> hashMaps = new HashMap<TopicPartition, OffsetAndMetadata>();
        hashMaps.put(new TopicPartition(topicName, 0), new OffsetAndMetadata(0));
//        consumer.commitSync(hashMaps);
        consumer.assign(Arrays.asList(new TopicPartition(topicName, 0)));
//        consumer.seekToBeginning(Arrays.asList(new TopicPartition(topicName, 0)));//不改变当前offset
       consumer.seek(new TopicPartition(topicName, 0), offsetStart);//不改变当前offset
//        consumer.subscribe(Arrays.asList(topicName));
        List<String> losts = Lists.newArrayList();
        b1: while (true) {
            ConsumerRecords<String, Object> records = consumer.poll(100);
//            System.out.println("dd");
            for (ConsumerRecord<String, Object> record : records){
                if(record.offset()>=offsetEnd)
                     break b1;
                String value = record.value().toString();
                System.out.println(value);
                losts.add(value);

                JSONObject obj = JSON.parseObject(value);
                JSONObject resultObj = obj.getJSONObject("result");
                if(resultObj!=null){
                    if (resultObj.containsKey("hash")) {//block
                        String number = resultObj.getString("number");
                        String hash = resultObj.getString("hash");
                        System.out.println(record.offset()+",block:"+new BigInteger(number.substring(2),16).toString()+","+hash);
                    } else if (resultObj.containsKey("transactionHash")) {//transaction
                        String number= resultObj.getString("blockNumber");
                        String hash= resultObj.getString("transactionHash");
                        System.out.println(record.offset()+",txn block:"+new BigInteger(number.substring(2),16).toString()+","+hash);
                    }
                }else{
                    if(obj.containsKey("contract"))//contract
                        System.out.println(record.offset()+",contract");
                }

            }
        }
//        exit(0);
        System.out.println("sending");

        Properties props2 = new Properties();
        props2.put("bootstrap.servers", bootstrap_servers);
        props2.put("acks", "all");
        props2.put("retries", 0);
        props2.put("batch.size", 16384);
        props2.put("linger.ms", 1);
        props2.put("buffer.memory", 33554432);
        props2.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props2.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props2);
        for(String lost:losts)
            producer.send(new ProducerRecord<String, String>(topicName, lost));

        producer.close();

    }
}
