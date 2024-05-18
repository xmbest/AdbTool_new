package com.xiaoming.utils

import java.text.DecimalFormat

object FileUtil {

    /**
     * 字节转gb单位
     * @param size 字节数大小
     */
    fun byte2Gb(size: String): String {
        var sizeInt = 0

        if (size.contains(",")) {
            val split = size.split(",")
            if (split.size > 1) {
                val sizeInner = split[1].trim()
                try {
                    sizeInt = sizeInner.toInt()
                } catch (e: Exception) {
                    return "0B"
                }
            }
        }

        try {
            sizeInt = size.toInt()
        } catch (e: Exception) {
            return "0B"
        }
        //获取到的size为：1705230
        val GB = 1024 * 1024 * 1024 //定义GB的计算常量
        val MB = 1024 * 1024 //定义MB的计算常量
        val KB = 1024 //定义KB的计算常量
        val df = DecimalFormat("0.00") //格式化小数
        var resultSize = ""
        resultSize = if (sizeInt / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            df.format((sizeInt / GB.toFloat()).toDouble()) + "GB   "
        } else if (sizeInt / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            df.format((sizeInt / MB.toFloat()).toDouble()) + "MB   "
        } else if (sizeInt / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            df.format((sizeInt / KB.toFloat()).toDouble()) + "KB   "
        } else {
            size + "B   "
        }
        return resultSize
    }
}