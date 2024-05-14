package com.xiaoming.utils

import java.text.DecimalFormat

object FileUtil {

    /**
     * 字节转gb单位
     * @param size 字节数大小
     */
    fun byte2Gb(size: Int): String {
        //获取到的size为：1705230
        val GB = 1024 * 1024 * 1024 //定义GB的计算常量
        val MB = 1024 * 1024 //定义MB的计算常量
        val KB = 1024 //定义KB的计算常量
        val df = DecimalFormat("0.00") //格式化小数
        var resultSize = ""
        resultSize = if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            df.format((size / GB.toFloat()).toDouble()) + "GB   "
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            df.format((size / MB.toFloat()).toDouble()) + "MB   "
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            df.format((size / KB.toFloat()).toDouble()) + "KB   "
        } else {
            size.toString() + "B   "
        }
        return resultSize
    }
}