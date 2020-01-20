package com.ysstech.sparkstreaming.Utils

import java.util.{Calendar, Date}

import org.apache.commons.lang3.time.FastDateFormat

class DateUtils {

  def getDate(num:Int,format:String) ={
    if(num == null || format.equals("")){
     val  num = 0;
    }
      //1.获取当前日期
      val date = new Date()
      //2.在当前日期基础上-num 天
      val calender = Calendar.getInstance()
      calender.setTime(date)
      calender.add(Calendar.DAY_OF_YEAR,-num)
      //3,对日期进行格式化
    val formatter = FastDateFormat.getInstance(format)
    formatter.format(calender)


  }
}
