package com.xiaoming.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoming.utils.ClipboardUtils
import com.xiaoming.utils.GenerexUtils
import com.xiaoming.widget.Toast
import com.xiaoming.config.route_left_item_color
import com.xiaoming.theme.GOOGLE_BLUE
import com.xiaoming.theme.GOOGLE_GREEN
import com.xiaoming.theme.GOOGLE_RED
import com.xiaoming.theme.GOOGLE_YELLOW


/**
 * 命令字生成页面
 */

val scrText = mutableStateOf("")
val destText = mutableStateOf("")
val toCn = mutableStateOf(false)

@Preview
@Composable
fun CommandScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            CommandText(
                str = scrText, hint = "TXZ语料规则\n" +
                        "1.多种语料请换行\n2.已将1./2./数字././;/；等替换成空白\n" +
                        "3.【】/（）已替换成英文符号\n4.支持数字范围，例1-36：调高([1-9]|[1-2][1-9]|3[0-6])度"
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CommandButton("生成") {
                try {
                    if (scrText.value.isBlank()) {
                        Toast.show("内容不可空")
                        return@CommandButton
                    }
                    destText.value = GenerexUtils.generateAll(scrText.value, toCn.value)

                } catch (_: Exception) {
                    Toast.show("请检查格式")
                }
            }
            CommandButton("粘贴", backgroundColor = GOOGLE_GREEN) {
                scrText.value = ClipboardUtils.getSysClipboardText() ?: ""
            }
            CommandButton("复制", backgroundColor = GOOGLE_YELLOW) {
                if (destText.value.isBlank())
                    return@CommandButton
                ClipboardUtils.setSysClipboardText(destText.value)
                Toast.show("结果已复制")
            }
            CommandButton("清空", backgroundColor = GOOGLE_RED) {
                if (destText.value.isBlank() && scrText.value.isBlank())
                    return@CommandButton
                scrText.value = ""
                destText.value = ""
            }
            Checkbox(
                toCn.value,
                onCheckedChange = {
                    toCn.value = it
                },
                colors = CheckboxDefaults.colors(checkedColor = GOOGLE_BLUE),
            )
            Text(text = "数转中",
                color = route_left_item_color,
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    toCn.value = !toCn.value
                }
            )

        }
        Row(modifier = Modifier.fillMaxWidth().weight(2f)) {
            CommandText(str = destText, hint = "结果")
        }
    }
}


@Composable
fun CommandText(str: MutableState<String>, hint: String) {
    val scroll = rememberScrollState()
    TextField(
        str.value,
        modifier = Modifier.fillMaxSize().scrollable(scroll, Orientation.Vertical).fillMaxHeight(),
        placeholder = { Text(hint) },
        trailingIcon = {
            if (str.value.isNotEmpty()) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.clickable {
                        str.value = ""
                    }.hoverable(interactionSource = remember { MutableInteractionSource() }))
                }
            }
        },
        onValueChange = {
            str.value = it
        })
}

@Composable
fun CommandButton(
    str: String,
    backgroundColor: Color = GOOGLE_BLUE,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier.padding(end = 5.dp, top = 5.dp, bottom = 5.dp)
    ) {
        Text(str, color = textColor)
    }
}


