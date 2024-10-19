package com.xiaoming.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoming.theme.GOOGLE_GREEN
import com.xiaoming.theme.GOOGLE_RED
import com.xiaoming.theme.GOOGLE_YELLOW


/**
 * 对话框属性
 */
val showingSimpleDialog = mutableStateOf(false)
val simpleContentText = mutableStateOf("")
val simpleCallback = mutableStateOf({})
val simpleNeedCancel = mutableStateOf(false)
val simpleTitle = mutableStateOf("警告")
val simpleTitleColor = mutableStateOf(Color.Blue)

@Composable
fun SimpleDialog(
    title: String = "警告",
    titleColor: Color = GOOGLE_RED,
    contentText: String = "测试",
    needCancel: Boolean = false,
    callback: (() -> Unit)? = null,
    dialogWidth: Int = 320,
    dialogHeight: Int = 160,
    content: @Composable (() -> Unit) = {
        Row(
            modifier = Modifier.width(dialogWidth.dp).height(dialogHeight.dp).padding(5.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SelectionContainer {
                Text(color = Color.Gray, text = contentText, fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
) {
    Dialog(showingSimpleDialog,title = title, titleColor = titleColor, callback = callback, needCancel = needCancel,content =  content)
}

object SimpleDialog {
    /**
     * 错误弹窗
     * @param text 错误信息
     */
    fun error(text: String) {
        simpleTitle.value = "⚠️异常"
        simpleTitleColor.value = GOOGLE_RED
        simpleNeedCancel.value = false
        simpleContentText.value = text
        simpleCallback.value = {
            showingSimpleDialog.value = false
        }
        showingSimpleDialog.value = true
    }


    /**
     * 提示弹窗
     * @param text 错误信息
     */
    fun info(text: String,titleText: String = "提示",titleTextColor: Color = GOOGLE_GREEN) {
        simpleTitle.value = titleText
        simpleTitleColor.value = titleTextColor
        simpleNeedCancel.value = false
        simpleContentText.value = text
        simpleCallback.value = {
            showingSimpleDialog.value = false
        }
        showingSimpleDialog.value = true
    }

    /**
     * 确认弹窗
     * @param text 确认内容
     * @param block 点击确认后的回调
     */
    fun confirm(text: String, block: (() -> Unit)) {
        simpleTitle.value = "⚠️警告"
        simpleTitleColor.value = GOOGLE_YELLOW
        simpleNeedCancel.value = true
        simpleContentText.value = text
        simpleCallback.value = {
            block.invoke()
            showingSimpleDialog.value = false
            simpleCallback.value = {}
        }
        showingSimpleDialog.value = true
    }
}