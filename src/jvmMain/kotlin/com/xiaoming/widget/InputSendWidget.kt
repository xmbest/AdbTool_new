package com.xiaoming.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.InputSendData
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.LogUtil
import com.xiaoming.utils.PropertiesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputSendWidget(data: InputSendData) {
    val text = remember {
        mutableStateOf(PropertiesUtil.getValue(data.uuid) ?: "")
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(data.title)
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            TextField(
                text.value,
                trailingIcon = {
                    if (text.value.isNotBlank()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.width(20.dp).height(20.dp).clickable {
                                text.value = ""
                            }
                        )
                    }
                },
                singleLine = true,
                placeholder = { Text(data.hint) },
                onValueChange = { text.value = it },
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 5.dp).onKeyEvent {
                    if (it.key.keyCode == Key.Enter.keyCode && it.type == KeyEventType.KeyUp) {
                        LogUtil.d("Enter")
                        onClick(text.value, data)
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                }
            )
            Button(
                onClick = {
                    onClick(text.value, data)
                },
                modifier = Modifier.width(80.dp).fillMaxHeight().padding(start = 0.dp, end = 5.dp)
            ) {
                Text(text = data.btnText)
            }
        }
    }
}


/**
 * 控件 click
 * @param text 输入框文本
 * @param data 数据
 */
fun onClick(text: String, data: InputSendData) {
    CoroutineScope(Dispatchers.Default).launch {
        if (text.isBlank()) {
            Toast.show("内容不可空")
            return@launch
        }
        PropertiesUtil.setValue(data.uuid, text)
        AdbUtil.shell(data.cmd.replace(data.template, text).trim())
    }
}