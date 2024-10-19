package com.xiaoming.entity

import com.xiaoming.config.FastBroadType

data class ButtomGroupData(val title: String, val list: List<ButtomData>) :
    BaseFastBroadData(FastBroadType.BOTTOM_GROUP)
