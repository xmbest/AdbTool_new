package com.xiaoming.state

import androidx.compose.runtime.MutableState

object StateKeyValue {
    val sTaskSearchKeyWords = Pair("sTaskSearchKeyWords","system")
    val sList = listOf(sTaskSearchKeyWords)

    fun getMap(): Map<String, MutableState<String>> {
        return mapOf(sTaskSearchKeyWords.first to GlobalState.sTaskKeyWords)
    }
}