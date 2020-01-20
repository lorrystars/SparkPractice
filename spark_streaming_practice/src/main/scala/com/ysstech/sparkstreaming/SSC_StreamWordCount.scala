package com.ysstech.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SSC_StreamWordCount {
  def main(args: Array[String]): Unit = {
    //1.初始化spark配置信息
    val sc =new SparkConf().setMaster("local[*]").setAppName("streamwordcount")

    //2.初始化sparkStreamingcontext
    val ssc = new StreamingContext(sc,Seconds(5))

    //设置日志级别
    ssc.sparkContext.setLogLevel("warn")
    //3.监控端口创建dstream,读进来的数据为一行行
    val lineStream: ReceiverInputDStream[String] = ssc.socketTextStream("node01",9999)

    //4.将每行数据进行切分(此时获取的是字符串数组),给单词添加词频对单词进行统计
    val wordCount = lineStream.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    //5.出发action算子执行executor
    wordCount.print()

   //6.启动stream程序
    ssc.start()
    //6.阻塞主线程
    ssc.awaitTermination()



  }
}
