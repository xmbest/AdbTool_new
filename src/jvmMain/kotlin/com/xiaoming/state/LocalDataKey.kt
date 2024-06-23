package com.xiaoming.state

import androidx.compose.runtime.MutableState

object LocalDataKey {
    val DEFAULT = Pair("DEFAULT","程序携带")
    val ENV = Pair("ENV","环境变量")
    val CUSTOM = Pair("CUSTOM","自定义")
    val sTaskSearchKeyWords = Pair("sTaskSearchKeyWords","system")
    val sAdbCustomPath = Pair("sAdbCustomPath","")
    val sAdbSelect = Pair("sAdbSelect",DEFAULT.first)
    val sSaveLog = Pair("sSaveLog","0")
    val sDefaultPageIndex = Pair("sDefaultPageIndex","0")
    val sFileSavePath = Pair("sFileSavePath",System.getProperty("user.home"))
    val sList = listOf(sTaskSearchKeyWords,sAdbCustomPath,sAdbSelect,sSaveLog,sDefaultPageIndex,sFileSavePath)
    val sAdbEnvList  = listOf(DEFAULT,ENV,CUSTOM)

    fun getMap(): Map<String, MutableState<String>> {
        return mapOf(
            sTaskSearchKeyWords.first to GlobalState.sTaskKeyWords,
            sAdbCustomPath.first to GlobalState.adbCustomPath,
            sAdbSelect.first to GlobalState.adbSelect,
            sSaveLog.first to GlobalState.saveLog,
            sDefaultPageIndex.first to GlobalState.sDefaultStartIndex,
            sFileSavePath.first to GlobalState.sFileSavePath
        )
    }
}