package com.xiaoming.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaoming.module.AdbModule
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import com.xiaoming.utils.ImgUtil
import com.xiaoming.utils.LogUtil
import com.xiaoming.utils.PathSelectorUtil
import com.xiaoming.utils.PropertiesUtil
import com.xiaoming.config.route_left_background
import com.xiaoming.config.route_left_item_clicked_color
import com.xiaoming.config.route_left_item_color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.xiaoming.theme.GOOGLE_BLUE


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(start = 14.dp, top = 10.dp)
    ) {
        TooltipArea(tooltip = {
            Text("save in ${GlobalState.workDir}")
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    GlobalState.saveLog.value == "1",
                    onCheckedChange = {
                        GlobalState.saveLog.value = if (it) "1" else "0"
                        PropertiesUtil.setValue("saveLog", GlobalState.saveLog.value)
                        LogUtil.d("saveLog value change ==> " + GlobalState.saveLog.value)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = GOOGLE_BLUE)
                )

                Text(text = "保存日志",
                    color = route_left_item_color,
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        GlobalState.saveLog.value = if (GlobalState.saveLog.value == "1") "0" else "1"
                        PropertiesUtil.setValue("saveLog", GlobalState.saveLog.value)
                        LogUtil.d("saveLog value change ==> " + GlobalState.saveLog.value)
                    })
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 14.dp)
        ) {
            Text("adb执行环境: ", color = route_left_item_color)
            Row {
                LocalDataKey.sAdbEnvList.forEachIndexed { index, pair ->
                    SelectButton(
                        pair.second,
                        if (pair.first != GlobalState.adbSelect.value) route_left_background else GOOGLE_BLUE,
                        if (pair.first != GlobalState.adbSelect.value) route_left_item_color else route_left_item_clicked_color,
                        RoundedCornerShape(
                            if (index != 0) 0 else 15,
                            if (index != LocalDataKey.sAdbEnvList.size - 1) 0 else 15,
                            if (index != LocalDataKey.sAdbEnvList.size - 1) 0 else 15,
                            if (index != 0) 0 else 15
                        )
                    ) {
                        GlobalState.adbSelect.value = pair.first
                        CoroutineScope(Dispatchers.Default).launch {
                            PropertiesUtil.setValue(LocalDataKey.sAdbSelect.first, pair.first)
                            AdbModule.changeAdb(pair.first)
                        }
                    }
                }
            }
            if (GlobalState.adbSelect.value == LocalDataKey.CUSTOM.first) {
                TextField(
                    GlobalState.adbCustomPath.value,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(1f).height(54.dp).padding(end = 10.dp, top = 4.dp),
                    enabled = false,
                    trailingIcon = {
                        TooltipArea(tooltip = {
                            Text("切换")
                        }) {
                            Icon(
                                painterResource(ImgUtil.getRealLocation("file")),
                                null,
                                modifier = Modifier.size(30.dp).clickable {
                                    val path = PathSelectorUtil.selectFile(StringBuilder("请选择adb执行程序"))
                                    if (path.isNotBlank()) {
                                        GlobalState.adbCustomPath.value = path
                                        CoroutineScope(Dispatchers.Default).launch {
                                            PropertiesUtil.setValue(LocalDataKey.sAdbCustomPath.first, path)
                                        }
                                    }
                                },
                                tint = route_left_item_color
                            )
                        }

                    }
                )
            }

            Text("默认启动页: ", color = route_left_item_color, modifier = Modifier.padding(top = 4.dp))
            Row {
                GlobalState.pages.forEachIndexed { index, page ->
                    SelectButton(
                        page.name,
                        if (index.toString() != GlobalState.sDefaultStartIndex.value) route_left_background else GOOGLE_BLUE,
                        if (index.toString() != GlobalState.sDefaultStartIndex.value) route_left_item_color else route_left_item_clicked_color,
                        RoundedCornerShape(
                            if (index != 0) 0 else 15,
                            if (index != LocalDataKey.sAdbEnvList.size - 1) 0 else 15,
                            if (index != LocalDataKey.sAdbEnvList.size - 1) 0 else 15,
                            if (index != 0) 0 else 15
                        )
                    ) {
                        GlobalState.sDefaultStartIndex.value = index.toString()
                        PropertiesUtil.setValue(
                            LocalDataKey.sDefaultPageIndex.first,
                            GlobalState.sDefaultStartIndex.value,
                            ""
                        )
                        LogUtil.d("sDefaultStartIndex value change ==> ${GlobalState.sDefaultStartIndex.value}")
                    }
                }
            }
            Text("文件保存路径: ", color = route_left_item_color, modifier = Modifier.padding(top = 4.dp))
            Row {
                TextField(
                    GlobalState.sFileSavePath.value,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(1f).height(54.dp).padding(end = 10.dp, top = 4.dp),
                    enabled = false,
                    trailingIcon = {
                        TooltipArea(tooltip = {
                            Text("切换")
                        }) {
                            Icon(
                                painterResource(ImgUtil.getRealLocation("folder")),
                                null,
                                modifier = Modifier.size(30.dp).clickable {
                                    val path = PathSelectorUtil.selectDir(title = "请选择文件保存路径")
                                    if (path.isNotBlank()) {
                                        GlobalState.sFileSavePath.value = path
                                        CoroutineScope(Dispatchers.Default).launch {
                                            PropertiesUtil.setValue(LocalDataKey.sFileSavePath.first, path)
                                        }
                                    }
                                },
                                tint = route_left_item_color
                            )
                        }

                    }
                )
            }

        }
    }
}

@Composable
fun SelectButton(
    str: String,
    backgroundColor: Color,
    textColor: Color,
    shape: Shape = MaterialTheme.shapes.small,
    click: () -> Unit
) {
    Button(
        onClick = click,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = shape
    ) {
        Text(str, color = textColor)
    }
}