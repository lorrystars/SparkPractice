package com.ysstech.spark.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 有状态转换操作UpdateBykey
  */
object SSC_UpdateStateByKey {
  def main(args: Array[String]): Unit = {
    //1.定义一个更新状态方法,参数values为当前批次单词频度,state为以往单词频度
    val updateFunc = (values:Seq[Int],state:Option[Int]) =>{
      val currentCount = values.foldLeft(0)(_+_)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }
  //2.创建ssc对象并初始化
    val sc = new SparkConf().setMaster("local[*]").setAppName("updateByKeyCount")
    val ssc = new StreamingContext(sc,Seconds(10))
    ssc.checkpoint("hdfs://node01:8020//input/spark-test/streamcheck")

    //3.创建一个socketStream
    val lines = ssc.socketTextStream("node01",9999)
    val updateCount = lines.flatMap(_.split(" ")).map((_,1))
          .updateStateByKey[Int](updateFunc).print()
    //4.启动ssc程序并阻塞线程
    ssc.start()
    ssc.awaitTermination()

  }

}
