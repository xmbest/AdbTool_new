package com.xiaoming.widget

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.ShellSendData
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.PropertiesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ShellSendWidget(data: ShellSendData) {
    val text = remember {
        mutableStateOf(PropertiesUtil.getValue(data.uuid) ?: "")
    }
    Column(modifier = Modifier.fillMaxWidth().height(data.minHeight.dp)) {
        Text(data.title)
        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp)) {
            val scroll = rememberScrollState()
            TextField(
                text.value,
                modifier = Modifier.fillMaxSize().scrollable(scroll, Orientation.Vertical).fillMaxHeight(),
                trailingIcon = {
                    if (text.value.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Button(
                                onClick = {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        if (text.value.isBlank()) {
                                            Toast.show(data.hintText)
                                            return@launch
                                        }
                                        PropertiesUtil.setValue(data.uuid, text.value)
                                        AdbUtil.shell(text.value)
                                    }
                                }, modifier = Modifier.padding(end = 5.dp),
                                colors = if (data.btnBgColor.isNotBlank()) ButtonDefaults.buttonColors(
                                    backgroundColor = Color(
                                        data.getColorLong(
                                            data.btnBgColor
                                        )
                                    )
                                ) else ButtonDefaults.buttonColors()
                            ) {
                                Text(
                                    text = data.btnText,
                                    color = if (data.btnTextColor.isNotBlank()) Color(data.getColorLong(data.btnTextColor)) else Color.Unspecified
                                )
                            }
                        }
                    }
                },
                onValueChange = {
                    text.value = it
                }, placeholder = {
                    Text(data.hintText)
                })
        }

    }
}