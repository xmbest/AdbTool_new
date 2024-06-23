package com.xiaoming.utils

import com.android.ddmlib.Log.LogLevel
import com.xiaoming.state.GlobalState
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object LogUtil {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    /**
     * 控制台输出日志
     * @param msg 日志
     * @param level 等级
     */
    private fun out(msg: String,level: LogLevel){
            val date = sdf.format(Date())
            val str = String.format("%s ${level.priorityLetter} %s", date, msg)
            println(str)
            if (GlobalState.saveLog.value){
                flush(str)
            }
        }

        fun d(msg:String){
            out(msg,LogLevel.DEBUG)
        }

        fun e(msg:String){
            out(msg,LogLevel.ERROR)
        }

        private fun flush(msg: String){
            val sdf1 = SimpleDateFormat("yyyy_MM_dd_HH")
            val date = sdf1.format(Date())
            val parent = File(GlobalState.workDir,"log")
            if (!parent.exists()){
                parent.mkdirs()
            }
            val file = File(parent,"${date}.log")
            if (!file.exists()){
                file.createNewFile()
            }
            val output = BufferedOutputStream(FileOutputStream(file,true))
            output.write((msg + "\n").toByteArray())
            output.flush()
            output.close()
        }
}