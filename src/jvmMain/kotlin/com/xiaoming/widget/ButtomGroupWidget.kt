package com.xiaoming.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.utils.AdbUtil

@Composable
fun ButtomGroupWidget(data: ButtomGroupData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(data.title)
        StaggeredGrid {
            data.map.forEach {
                Button(onClick = {
                    AdbUtil.shell(it.value)
                }, modifier = Modifier.padding(end = 10.dp, top = 5.dp)) {
                    Text(text = it.key, modifier = Modifier.padding(5.dp), maxLines = 1)
                }
            }
        }
    }
}


