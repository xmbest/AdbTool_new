package com.xiaoming.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.GsonUtil
import com.xiaoming.utils.LogUtil

@Composable
fun ButtomGroupWidget(data: ButtomGroupData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(data.title)
        StaggeredGrid {
            data.list.forEach {
                Button(
                    onClick = {
                        AdbUtil.shell(it.cmd)
                        LogUtil.d(GsonUtil.gson.toJson(data))
                    }, modifier = Modifier.padding(end = 10.dp, top = 10.dp),
                    colors = if (it.btnBgColor.isNotBlank()) ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            data.getColorLong(
                                it.btnBgColor
                            )
                        )
                    ) else ButtonDefaults.buttonColors()
                ) {
                    Text(
                        text = it.btnText, modifier = Modifier.padding(5.dp), maxLines = 1,
                        color = if (it.btnTextColor.isNotBlank()) Color(data.getColorLong(it.btnTextColor)) else Color.Unspecified
                    )
                }
            }
        }
    }
}


