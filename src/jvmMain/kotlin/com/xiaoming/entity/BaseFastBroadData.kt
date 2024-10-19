package com.xiaoming.entity

import androidx.compose.ui.graphics.Color
import com.xiaoming.theme.GOOGLE_BLUE
import java.util.*

open class BaseFastBroadData(val type: String, val uuid: String = "uuid-" + UUID.randomUUID().toString()) {
    fun getColorLong(color: String, defaultColor: Color = GOOGLE_BLUE): Long {
        return try {
            java.lang.Long.decode(color)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultColor.value.toLong()
        }
    }
}