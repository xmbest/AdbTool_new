package com.xiaoming.config

import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.entity.InputSendData
import com.xiaoming.entity.ShellSendData

class FastBroadConfig {
    val map = mapOf(
        FastBroadType.INPUT_SEND to InputSendData::class,
        FastBroadType.BOTTOM_GROUP to ButtomGroupData::class,
        FastBroadType.SHELL_SEND to ShellSendData::class,
    )
}

class FastBroadType{
    companion object{
        const val INPUT_SEND = 0
        const val BOTTOM_GROUP = 1
        const val SHELL_SEND = 2
    }
}