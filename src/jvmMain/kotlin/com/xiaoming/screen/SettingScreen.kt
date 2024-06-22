package com.xiaming.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaoming.db.DAOImpl
import com.xiaoming.module.AdbModule
import com.xiaoming.state.GlobalState
import com.xiaoming.state.StateKeyValue
import com.xiaoming.utils.ImgUtil
import com.xiaoming.utils.PathSelectorUtil
import config.route_left_background
import config.route_left_item_clicked_color
import config.route_left_item_color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.GOOGLE_BLUE


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(start = 14.dp, top = 10.dp)
    ) {
        SettingLable("adb执行环境: ")
        Row {
            StateKeyValue.sAdbEnvList.forEach {
                SelectButton(
                    it.second,
                    if (it.first != GlobalState.adbSelect.value) route_left_background else GOOGLE_BLUE,
                    if (it.first != GlobalState.adbSelect.value) route_left_item_color else route_left_item_clicked_color
                ) {
                    GlobalState.adbSelect.value = it.first
                    CoroutineScope(Dispatchers.Default).launch {
                        DAOImpl.putString(StateKeyValue.sAdbSelect.first,it.first)
                        AdbModule.changeAdb(it.first)
                    }
                }
            }
        }
        if (GlobalState.adbSelect.value == StateKeyValue.CUSTOME.first) {
            TextField(
                GlobalState.adbCustomPath.value,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(1f).height(48.dp).padding(end = 10.dp),
                enabled = false,
                trailingIcon = {
                    TooltipArea(tooltip = {
                        Text("切换")
                    }) {
                        Icon(
                            painterResource(ImgUtil.getRealLocation("folder")),
                            null,
                            modifier = Modifier.size(30.dp).clickable {
                                val path = PathSelectorUtil.selectFile(StringBuilder("请选择adb执行程序"))
                                if (path.isNotBlank()) {
                                    GlobalState.adbCustomPath.value = path
                                    CoroutineScope(Dispatchers.Default).launch {
                                        DAOImpl.putString(StateKeyValue.sAdbCustomPath.first,path)
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


@Composable
fun SettingLable(str: String, width: Int = 100) {
    Text(str, color = route_left_item_color, modifier = Modifier.width(width.dp))
}

@Composable
fun SelectButton(str: String, backgroundColor: Color, textColor: Color, click: () -> Unit) {
    Button(
        onClick = click,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    ) {
        Text(str, color = textColor)
    }
}