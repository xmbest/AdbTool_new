package com.xiaoming.entity

import com.xiaoming.config.FastBroadType

data class ShellSendData(val title: String,
                         val btnText: String = "Send",
                         val hintText: String = "",
                         val minHeight: Int = 100) :
    BaseFastBroadData(FastBroadType.SHELL_SEND) {
}