package com.xiaoming.config

import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.entity.InputSendData
import com.xiaoming.entity.ShellSendData

object FastBroadConfig {
    val map = mapOf(
        FastBroadType.INPUT_SEND to InputSendData::class.java,
        FastBroadType.BOTTOM_GROUP to ButtomGroupData::class.java,
        FastBroadType.SHELL_SEND to ShellSendData::class.java,
    )

    val list = ArrayList<Any>()
}

class FastBroadType{
    companion object{
        const val INPUT_SEND = "0"
        const val BOTTOM_GROUP = "1"
        const val SHELL_SEND = "2"
    }
}