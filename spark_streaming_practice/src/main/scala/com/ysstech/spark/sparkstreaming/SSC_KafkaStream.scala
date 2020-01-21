package com.ysstech.spark.sparkstreaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * sparkstreaming使用kafka作为数据源
  */
object SSC_KafkaStream {
  def main(args: Array[String]): Unit = {
    //1.创建sparkconf并初始化SSC
    val sc = new SparkConf().setMaster("local[*]").setAppName("SSC_KafkaStream")
    val ssc = new StreamingContext(sc,Seconds(5))
    ssc.sparkContext.setLogLevel("warn")
    //2.定义kafka参数
    val topics = Array("source")
    //3.将kafka参映射为map
    val kafkaParams =Map[String,Object](
     //指定kafka broker的节点
      "bootstrap.servers" -> "node01:9092,node02:9092,node03:9092",
      //kafka消息key的反序列化方式
      "key.deserializer" -> classOf[StringDeserializer],
      //kafka消息value的反序列化方式
      "value.deserializer" -> classOf[StringDeserializer],
      //消费组
      "group.id" -> "spark",
      //从哪个offset开始消费,earliest:最小的offset
      "auto.offset.reset" -> "latest",
      //是否自动提交offset
      "enable.auto.commit" ->(false: java.lang.Boolean
    ))

    //4.调用kafkautils获取kafka直流
    val kafkaSource = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](topics,kafkaParams))

  //ssc: StreamingContext,
    //      locationStrategy: LocationStrategy,
    //      consumerStrategy: ConsumerStrategy[K, V],
    //      perPartitionConfig: PerPartitionConfig

    //5.数据处理
    kafkaSource.foreachRDD( rdd =>{
      //5.1数据处理
      rdd.flatMap(_.value().split(" ")).map((_,1)).reduceByKey(_+_).foreach(println(_))
      //5.2提交本次offset
      val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      //5.3提交offset
      kafkaSource.asInstanceOf[CanCommitOffsets].commitAsync(offsets)
    })

    //6.启动ssc阻塞主线程
    ssc.start()
    ssc.awaitTermination()
  }
}
