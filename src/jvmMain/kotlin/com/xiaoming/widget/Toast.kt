package com.xiaoming.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoming.config.route_left_background
import com.xiaoming.config.route_left_item_color
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.xiaoming.theme.GOOGLE_BLUE

/**
 * Toast属性
 */
val toastText = mutableStateOf("")
val showToast = mutableStateOf(false)
val toastBgColor = mutableStateOf(route_left_background)
val toastTextColor = mutableStateOf(route_left_item_color)
val toastTime = mutableStateOf(Toast.TOAST_NORMOL)
val toastClose = mutableStateOf(false)

/*
* 自定义Toast
* */
@Composable
fun Toast() {
    if (showToast.value) {
        Box(modifier = Modifier.fillMaxSize().padding(bottom = 30.dp)) {
            Card(modifier = Modifier.height(40.dp).align(Alignment.BottomCenter)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(
                        toastBgColor.value
                    ).padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(text = toastText.value, color = toastTextColor.value)
                    if (toastClose.value) {
                        Text(text = "关闭", color = GOOGLE_BLUE, modifier = Modifier.clickable {
                            showToast.value = false
                        }.align(Alignment.CenterVertically))
                    }
                }
                autoClose(showToast, toastTime.value)
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun autoClose(showToast: MutableState<Boolean>, showTime: Long) {
    GlobalScope.launch {
        delay(showTime)
        if (showToast.value) {
            showToast.value = false
        }
    }
}

object Toast {
    const val TOAST_SHORT = 500L
    const val TOAST_NORMOL = 1000L
    const val TOAST_LONG = 1500L
    fun show(
        text: String,
        time: Long = TOAST_NORMOL,
        bgColor: Color = route_left_background,
        textColor: Color = route_left_item_color,
        close:Boolean = false
    ) {
        toastText.value = text
        toastTime.value = time
        toastBgColor.value = bgColor
        toastTextColor.value = textColor
        toastClose.value = close
        showToast.value = true
    }
}