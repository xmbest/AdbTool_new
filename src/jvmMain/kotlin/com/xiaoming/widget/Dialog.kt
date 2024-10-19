package com.xiaoming.widget

import CustomDialogProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoming.theme.GOOGLE_BLUE
import com.xiaoming.theme.GOOGLE_RED
import com.xiaoming.theme.GOOGLE_YELLOW

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dialog(
    showDialog: MutableState<Boolean>,
    title: String,
    titleColor: Color = GOOGLE_YELLOW,
    callback: (() -> Unit)? = null,
    needCancel:Boolean = false,
    content: @Composable (() -> Unit)
) {
    AlertDialog(
        dialogProvider = CustomDialogProvider,
        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        onDismissRequest = {
            showDialog.value = false
        },
        buttons = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp, end = 20.dp)
            ) {
                Button(
                    onClick = {
                        callback?.invoke()
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_BLUE),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(text = "确定", color = Color.White)
                }
                if (needCancel){
                    Button(
                        onClick = {
                            showDialog.value = false
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_RED)
                    ) {
                        Text(text = "取消", color = Color.White)
                    }
                }
            }
        },
        title = { Text(color = titleColor, text = title) },
        text = { content() })
}