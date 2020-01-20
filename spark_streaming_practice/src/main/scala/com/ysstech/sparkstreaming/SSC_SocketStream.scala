package com.ysstech.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用自定义的receiver:SSC_CustomerReceiver采集数据
  */
object SSC_SocketStream {
  def main(args: Array[String]): Unit = {
    //1.初始化spark配置信息
    val sc = new SparkConf().setMaster("local[*]").setAppName("Streamwordcount")
    //2.初始化sparkstreamingcontext
    val ssc = new  StreamingContext(sc,Seconds(5))
    //3.创建自定义receiver的streaming
    val lineStream = ssc.receiverStream(new SSC_CustomerReceiver("node01",9999))
    //4.将每一行数据切分,形成一个个单词,添加词频,然后打印
    val wordCount = lineStream.flatMap(_.split("")).map((_,1)).reduceByKey(_+_).print()
    //5.启动程序
    ssc.start()
    ssc.awaitTermination()

  }

}
