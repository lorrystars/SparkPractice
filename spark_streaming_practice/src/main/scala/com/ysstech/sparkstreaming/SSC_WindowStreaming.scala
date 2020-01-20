package com.ysstech.sparkstreaming

import com.ysstech.sparkstreaming.Utils.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SSC_WindowStreaming {
  def main(args: Array[String]): Unit = {
    //1.定义更新状态的方法
    val updateFunc = (values: Seq[Int],state:Option[Int]) => {
      val currentCount = values.foldLeft(0)(_+_)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }

    //2.创建ssc对象,并且初始化
    val sc = new  SparkConf().setMaster("local[*]").setAppName("SSC_WindowStreaming")
    val ssc = new StreamingContext(sc,Seconds(5))
    ssc.checkpoint("hdfs://node01:8020//input/spark-test/streamcheck/")
    ssc.sparkContext.setLogLevel("warn")

    //3.获取数据(使用自定义数据源)
    //val source = ssc.receiverStream(new SSC_CustomerReceiver("node01",9999))
    val source = ssc.socketTextStream("node01",9999)
    //3.1数据处理(每隔五秒计算一次窗口持续10秒的计算计算)
    source.flatMap(_.split(" ")).map((_,1))
      .reduceByKeyAndWindow(
        reduceFunc = (agg,curr) => agg+curr,
        windowDuration =Seconds(10),
        slideDuration = Seconds(5)
    ).print()

    val utils = new DateUtils()
    println(utils.getDate(0, "yyyy-MM-dd HH:mm:ss"))

    //4.启动streaming程序
    ssc.start()
    ssc.awaitTermination()


  }

}
