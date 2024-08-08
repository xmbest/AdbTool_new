package com.xiaoming.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

@Composable
fun InputDialog(
    title: String = "提示",
    titleColor: Color = GOOGLE_YELLOW,
    hint: String = "请输入内容",
    callback: (() -> Unit)? = null,
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
    Dialog(showingInputDialog, title = title, titleColor = titleColor, callback = callback, content =  content)
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