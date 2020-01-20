package com.ysstech.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

/**
  * RDD作为数据源
  */
object SSC_RDDStream {
  def main(args: Array[String]): Unit = {
    //1.初始化spark配置信息
    val sc = new SparkConf().setMaster("local[*]").setAppName("RDDStream")
    //2.初始化sparkstreueamingContext
    val ssc = new StreamingContext(sc,Seconds(5))
    //3.创建RDD队列
    val rddQueue = new mutable.Queue[RDD[Int]]()
    //4.创建QueueInputDstream
    val inputstream = ssc.queueStream(rddQueue,oneAtATime = false)
    //5.处理队列中的RDD数据并打印
    val result = inputstream.map((_,1)).reduceByKey(_+_).print()
    //6.开启sparkstreaming
    ssc.start()
    //7.循环创建并向RDD队列中放入RDD
    for( i <- 1 to 5){
      rddQueue += ssc.sparkContext.makeRDD( 1 to 300,10)
      Thread.sleep(2000)
    }
    //8.阻塞线程
    ssc.awaitTermination()
  }

}
