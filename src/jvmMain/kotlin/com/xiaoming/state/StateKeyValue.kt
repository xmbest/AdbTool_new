package com.xiaoming.state

import androidx.compose.runtime.MutableState

object StateKeyValue {
    val DEFAULT = Pair("DEFAULT","程序携带")
    val ENV = Pair("ENV","环境变量")
    val CUSTOME = Pair("CUSTOME","自定义")
    val sTaskSearchKeyWords = Pair("sTaskSearchKeyWords","system")
    val sAdbCustomPath = Pair("sAdbCustomPath","")
    val sAdbSelect = Pair("sAdbSelect",DEFAULT.first)
    val sList = listOf(sTaskSearchKeyWords,sAdbCustomPath,sAdbSelect)

    val sAdbEnvList  = listOf(DEFAULT,ENV,CUSTOME)

    fun getMap(): Map<String, MutableState<String>> {
        return mapOf(
            sTaskSearchKeyWords.first to GlobalState.sTaskKeyWords,
            sAdbCustomPath.first to GlobalState.adbCustomPath,
            sAdbSelect.first to GlobalState.adbSelect,
        )
    }
}