package com.xiaming.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.xiaoming.utils.AdbUtil

@Composable
fun FileScreen() {
    Text("File")
    AdbUtil.findFileList("/"){entry, children -> {

    }}
}