package com.ysstech.sparkstreaming

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

/**
  * 自定义receiver数据源
  * @param host
  * @param port
  */
class SSC_CustomerReceiver(val host:String,val port:Int)  extends Receiver[String](StorageLevel.MEMORY_AND_DISK){
  /**
    * 接收socket数据
    * @return
    */
  def receive(): Socket = {
    //创建Socket
    var socket: Socket = new Socket(host, port)
    //获取输入流
    val is = socket.getInputStream
    //创建一个BufferedReader用于读取端口传来的数据
    val br = new BufferedReader(new InputStreamReader(is))
    //val reader = new BufferedReader(new InputStreamReader(socket.getInputStream, StandardCharsets.UTF_8))
    var line:String = null
    //判断receiver没有关闭并且socket中有数据就进行接收
    while ((line = br.readLine()) != null && !isStopped()){
      //将数据保存下来,累计到一个批次以后进行处理
      store(line)
    }
    //跳出循环则关闭资源
      br.close()
      is.close()
      socket
 /*   //重启任务
    restart("restart")*/
  }


  /**
    * receiver启动的时候调用
    */
  override def onStart(): Unit = {
    new Thread() {
      override def run(): Unit = {
        //接收socket数据
        receive()
      }
    }.start()
  }

  /**
    * receiver停止的时候调用
    */
  override def onStop(): Unit = {}
}
