package com.ysstech.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * HDFS上文件作为数据源
  */
object SSC_FileStream {
  def main(args: Array[String]): Unit = {
    //1.初始化Spark配置信息
    val sc = new SparkConf().setMaster("local[*]").setAppName("filestreamwordcount")
    //2.初始化sparkstreaming
    val ssc = new  StreamingContext(sc,Seconds(5))
    //3.监控文件夹创建Dstream
    val dirStream = ssc.textFileStream("hdfs://node01:9000/input/spark_test")
    //4.将每一行数据进行分割压平,形成一个个单词,添加词频,然后进行词频统计
    val wordCountFileSystem = dirStream.flatMap(_.split("\t")).map((_,1)).reduceByKey(_+_)
    //5.打印统计结果(触发action算子)
    wordCountFileSystem.print()
    //6.启动sparkstreamingContext并且阻塞线程
    ssc.start()
    ssc.awaitTermination()
  }
}
