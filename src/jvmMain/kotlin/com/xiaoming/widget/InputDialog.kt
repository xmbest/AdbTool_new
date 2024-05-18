package com.xiaoming.widget

import CustomDialogProvider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import theme.GOOGLE_BLUE
import theme.GOOGLE_RED
import theme.GOOGLE_YELLOW

/**
 * 输入框属性
 */
val showingInputDialog = mutableStateOf(false)
val inputText = mutableStateOf("")
val inputTitleText = mutableStateOf("")
val inputHintText = mutableStateOf("请输入内容")
val inputTitleColor = mutableStateOf(GOOGLE_YELLOW)
val inputCallback = mutableStateOf({})

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InputDialog(
    title: String = "提示",
    titleColor: Color = GOOGLE_YELLOW,
    hint: String = "请输入内容",
    runnable: (() -> Unit)? = null,
    width: Int = 320,
    height: Int = 150,
    content: @Composable (() -> Unit) = {
        Column(
            modifier = Modifier.height((height - (height - 100)).dp).width(width.dp).clip(RoundedCornerShape(5.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    inputText.value,
                    onValueChange = { inputText.value = it },
                    placeholder = { Text(hint) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
) {
    AlertDialog(
        dialogProvider = CustomDialogProvider,
        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        onDismissRequest = {
            showingInputDialog.value = false
        },
        buttons = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp, end = 20.dp)
            ) {
                Button(
                    onClick = {
                        runnable?.invoke()
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_BLUE),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(text = "确定", color = Color.White)
                }
                Button(
                    onClick = {
                        showingInputDialog.value = false
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_RED)
                ) {
                    Text(text = "取消", color = Color.White)
                }
            }
        },
        title = { Text(color = titleColor, text = title) },
        text = { content() })
}

object InputDialog {
    /**
     * 确认弹窗
     * @param text 确认内容
     * @param block 点击确认后的回调
     */
    fun confirm(
        title: String = "⚠️警告",
        titleColor: Color = GOOGLE_YELLOW,
        hint: String = "请输入内容",
        block: (() -> Unit) = {}
    ) {
        inputTitleText.value = title
        inputTitleColor.value = titleColor
        inputHintText.value = hint
        inputCallback.value = block
        showingInputDialog.value = true
    }
}