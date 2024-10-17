package com.xiaoming.entity

import com.xiaoming.config.FastBroadType

data class ButtomGroupData(val title: String, val map: Map<String, String>) :
    BaseFastBroadData(FastBroadType.BOTTOM_GROUP)
