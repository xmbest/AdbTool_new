package com.xiaoming.componts

import CustomDialogProvider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.GOOGLE_BLUE
import theme.GOOGLE_GREEN
import theme.GOOGLE_RED
import theme.GOOGLE_YELLOW


/**
 * 对话框属性
 */
val showingSimpleDialog = mutableStateOf(false)
val contentText = mutableStateOf("")
val callback = mutableStateOf({})
val needCancel = mutableStateOf(false)
val title = mutableStateOf("警告")
val titleColor = mutableStateOf(Color.Blue)

@OptIn(ExperimentalMaterialApi::class)
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

    AlertDialog(
        dialogProvider = CustomDialogProvider, modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        onDismissRequest = {
        }, buttons = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, end = 8.dp, start = 8.dp)
            ) {
                Button(
                    onClick = {
                        showingSimpleDialog.value = false
                        callback!!.invoke()
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_BLUE)
                ) {
                    Text(text = "确定", color = Color.White)
                }
                if (needCancel) {
                    Button(
                        onClick = {
                            showingSimpleDialog.value = false
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_RED),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "取消", color = Color.White)
                    }
                }
            }
        }, text = {
            content()
        }, title = {
            Text(color = titleColor, text = title)
        })
}

object SimpleDialog {
    /**
     * 错误弹窗
     * @param text 错误信息
     */
    fun error(text: String) {
        title.value = "异常"
        titleColor.value = GOOGLE_RED
        needCancel.value = false
        contentText.value = text
        callback.value = {}
        showingSimpleDialog.value = true
    }


    /**
     * 提示弹窗
     * @param text 错误信息
     */
    fun info(text: String,titleText: String = "提示") {
        title.value = titleText
        titleColor.value = GOOGLE_GREEN
        needCancel.value = false
        contentText.value = text
        callback.value = {}
        showingSimpleDialog.value = true
    }

    /**
     * 确认弹窗
     * @param text 确认内容
     * @param block 点击确认后的回调
     */
    fun confirm(text: String, block: (() -> Unit)) {
        title.value = "警告"
        titleColor.value = GOOGLE_YELLOW
        needCancel.value = true
        contentText.value = text
        callback.value = {
            block.invoke()
            callback.value = {}
        }
        showingSimpleDialog.value = true
    }
}