package com.xiaoming.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.xiaoming.entity.DeviceInfo
import com.xiaoming.state.GlobalState
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.ClipboardUtils
import com.xiaoming.utils.ImgUtil
import com.xiaoming.utils.ImgUtil.getRealLocation
import com.xiaoming.utils.LogUtil
import com.xiaoming.widget.*
import com.xiaoming.config.route_left_item_color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.xiaoming.theme.GOOGLE_BLUE
import com.xiaoming.theme.GOOGLE_GREEN
import com.xiaoming.theme.GOOGLE_RED
import javax.swing.filechooser.FileSystemView

val packageName = mutableStateOf("")
val quickSettingKeyword = mutableStateOf("")
val quickAppList = mutableStateListOf<String>()
val expanded = mutableStateOf(false)
val deviceInfo = mutableStateOf(DeviceInfo())

@OptIn(ExperimentalMaterialApi::class, ExperimentalUnitApi::class)
@Composable
fun QuickScreen() {
    val tripleList1s = listOf(
        Triple(getRealLocation("task"), 187, "任务列表"),
        Triple(getRealLocation("home"), 3, "回到桌面"),
        Triple(getRealLocation("back"), 4, "返回上级"),
        Triple(getRealLocation("power"), 26, "锁定屏幕")
    )
    val tripleList2s = listOf(
        Triple(getRealLocation("plus"), 24, "增加音量"),
        Triple(getRealLocation("minus"), 25, "减少音量"),
        Triple(getRealLocation("up"), 221, "增加亮度"),
        Triple(getRealLocation("down"), 220, "减少亮度"),

        )

    val tripleList3s = listOf(
        Triple(getRealLocation("down"), 1, "显示状态栏"),
        Triple(getRealLocation("up"), 2, "隐藏状态栏"),
        Triple(getRealLocation("image"), 0, "截图"),
        Triple(getRealLocation("settings"), 0, "进入设置")
    )

    val tripleList4s = listOf(
        Triple(getRealLocation("eye"), 1, "查看当前Activity"),
        Triple(getRealLocation("delete"), 2, "清理logcat缓存"),
        Triple(getRealLocation("open"), 0, "设置端口"),
        Triple(getRealLocation("reboot"), 0, "重启设备")
    )
    val scroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().fillMaxHeight().verticalScroll(scroll)) {

        General(title = "系统信息", height = 2, color = GOOGLE_GREEN, topRight = {
            Text(deviceInfo.value.brand)
        }) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(ImgUtil.getLogoByBrand(deviceInfo.value.brand)),
                    "logo",
                    alpha = 0.8f,
                    modifier = Modifier.size(80.dp).padding(end = 20.dp, bottom = 10.dp).align(Alignment.BottomEnd)
                )
                Column(modifier = Modifier.fillMaxSize().padding(start = 20.dp, top = 10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SelectionContainer {
                            Text(
                                text = deviceInfo.value.device, style = TextStyle(
                                    fontWeight = FontWeight.W500, fontSize = TextUnit(
                                        24f,
                                        TextUnitType.Sp
                                    )
                                )
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(1f).padding(top = 10.dp)) {
                        SelectionContainer {
                            Text(
                                "fingerprint:  ${deviceInfo.value.systemVersion} \n" +
                                        "version:       ${deviceInfo.value.androidVersion} \n" +
                                        "model:         ${deviceInfo.value.model} \n" +
                                        "cpuinfo:       ${deviceInfo.value.cpu} \n" +
                                        "serialNo:      ${deviceInfo.value.serialNo} \n" +
                                        "density:       ${deviceInfo.value.density} \n" +
                                        "memory:      ${deviceInfo.value.memory} \n" +
                                        "ip:                ${deviceInfo.value.ip} \n"
                            )
                        }
                    }

                }
            }
        }

        General(title = "按键模拟", height = 4, content = {
            ContentMoreRowColumn {
                ContentNRow {
                    tripleList1s.forEach {
                        Item(it.first, it.third) {
                            AdbUtil.inputKey(it.second)
                        }
                    }
                }
                ContentNRow {
                    tripleList2s.forEach {
                        Item(it.first, it.third) {
                            AdbUtil.inputKey(it.second)
                        }
                    }
                }
                ContentNRow {
                    Item(tripleList3s[0].first, tripleList3s[0].third) {
                        AdbUtil.shell("service call statusbar 1")
                    }
                    Item(tripleList3s[1].first, tripleList3s[1].third) {
                        AdbUtil.shell("service call statusbar 2")

                        println(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
                    }
                    Item(tripleList3s[2].first, tripleList3s[2].third) {
                        AdbUtil.screenshot()
                    }
                    Item(tripleList3s[3].first, tripleList3s[3].third) {
                        AdbUtil.shell("am start  -n com.android.settings/com.android.settings.Settings")
                    }
                }
                ContentNRow {
                    Item(tripleList4s[0].first, tripleList4s[0].third, false) {
                        CoroutineScope(Dispatchers.Default).launch {
                            val str = AdbUtil.findCurrentActivity()
                            if (str.isNotBlank()) {
                                SimpleDialog.info(str, "current activity:")
                            }
                        }
                    }
                    Item(tripleList4s[1].first, tripleList4s[1].third, false) {
                        SimpleDialog.confirm("是否清理logcat缓存") {
                            AdbUtil.shell("logcat -c")
                        }
                    }
                    Item(tripleList4s[2].first, tripleList4s[2].third, false) {
                        AdbUtil.tcpip(GlobalState.port.value)
                    }
                    Item(tripleList4s[3].first, tripleList4s[3].third, false) {
                        SimpleDialog.confirm("是否重启设备") {
                            AdbUtil.reboot()
                            Toast.show("重启中....")
                        }
                    }
                }
            }
        })
        General(title = "应用相关", color = GOOGLE_RED, height = 2, content = {
            ContentMoreRowColumn {
                ContentNRow {
                    Item(getRealLocation("start"), "启动应用") {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            AdbUtil.start(packageName.value)
                        }
                    }
                    Item(getRealLocation("power"), "停止运行", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            SimpleDialog.confirm("是否停止运行 ${packageName.value}?") {
                                AdbUtil.forceStop(packageName.value)
                            }
                        }
                    }
                    Item(getRealLocation("clear"), "清除数据", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            SimpleDialog.confirm("是否清除 ${packageName.value} 数据?") {
                                AdbUtil.clear(packageName.value)
                                Toast.show("${packageName.value}清除缓存中....")
                            }
                        }
                    }
                    Item(getRealLocation("delete"), "卸载应用", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            SimpleDialog.confirm("是否卸载应用 ${packageName.value}?") {
                                AdbUtil.uninstall(packageName.value)
                                Toast.show("${packageName.value}卸载中....")
                            }
                        }
                    }
                }
                ContentNRow {
                    Item(getRealLocation("grant"), "授予所有权限", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            SimpleDialog.confirm("授予${packageName.value}应用所有权限?") {
                                CoroutineScope(Dispatchers.Default).launch {
                                    AdbUtil.grant(packageName.value) {
                                        LogUtil.d("授予所有权限....")
                                        Toast.show("授予权限中....")
                                    }
                                }
                            }
                        }
                    }

                    Item(getRealLocation("undo"), "撤销所有权限", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            SimpleDialog.confirm("撤销${packageName.value}应用所有权限?") {
                                CoroutineScope(Dispatchers.Default).launch {
                                    AdbUtil.unGrant(packageName.value) {
                                        LogUtil.d("撤销所有权限....")
                                        Toast.show("撤销权限中....")
                                    }
                                }
                            }
                        }
                    }

                    Item(getRealLocation("eye"), "查看应用信息", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            CoroutineScope(Dispatchers.Default).launch {
                                val info = AdbUtil.dump(packageName.value, "version")
                                SimpleDialog.info(info, "结果")
                            }
                        }
                    }

                    Item(getRealLocation("copy"), "复制应用路径", false) {
                        conditionExe(packageName.value.isNotBlank(), "请先选择应用") {
                            CoroutineScope(Dispatchers.Default).launch {
                                val path = AdbUtil.path(packageName.value).split("\n")[0]
                                val value = path.substring(path.indexOf(":") + 1)
                                ClipboardUtils.setSysClipboardText(value)
                                Toast.show("路径已写入剪切板")
                            }
                        }
                    }
                }
            }
        }, topRight = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                ListItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                packageName.value.ifBlank { "请选择应用" },
                                color = GOOGLE_BLUE,
                                maxLines = 2,
                                textAlign = TextAlign.End,
                                modifier = Modifier.clickable {
                                    syncAppList()
                                    expanded.value = true
                                }
                            )
                            DropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = {
                                    expanded.value = false
                                },
                                offset = DpOffset(x = 260.dp, y = 2.dp)
                            ) {
                                Row {
                                    TextField(
                                        quickSettingKeyword.value,
                                        trailingIcon = {
                                            if (quickSettingKeyword.value.isNotBlank()) Icon(
                                                Icons.Default.Close,
                                                null,
                                                modifier = Modifier.width(20.dp).height(20.dp).clickable {
                                                    quickSettingKeyword.value = ""
                                                    syncAppList(quickSettingKeyword.value)
                                                },
                                                tint = route_left_item_color
                                            )
                                        },
                                        placeholder = { Text("keyword") },
                                        onValueChange = {
                                            quickSettingKeyword.value = it
                                            syncAppList(quickSettingKeyword.value)
                                        },
                                        modifier = Modifier.weight(1f).height(48.dp)
                                            .padding(end = 10.dp, start = 10.dp)
                                    )
                                }
                                if (quickAppList.size == 0) {
                                    DropdownMenuItem(onClick = {
                                        expanded.value = false
                                    }) {
                                        Text(text = "未找到相关应用")
                                    }
                                } else {
                                    quickAppList.forEach {
                                        DropdownMenuItem(onClick = {
                                            expanded.value = false
                                            packageName.value = it
                                        }) {
                                            Text(text = it)
                                        }
                                    }
                                }

                            }
                        }
                    },
                    modifier = Modifier.width(480.dp)
                )
            }
        })
    }
}

/**
 * 刷新应用列表
 * @param keyWord 关键词
 */
fun syncAppList(keyWord: String = "") {
    val list = ArrayList<String>()
    var cmd = "pm list packages -f"
    if (keyWord.isNotBlank()) {
        cmd += "| grep -E '$keyWord'"
    }
    CoroutineScope(Dispatchers.Default).launch {
        val packages = AdbUtil.shell(cmd, 500)
        val split = packages.split("\n").filter { it.isNotBlank() }.map { it.substring(8) }
        split.forEach {
            val index = it.lastIndexOf("=")
            val packageName = it.substring(index + 1)
            list.add(packageName)
        }
        quickAppList.clear()
        quickAppList.addAll(list)
    }
}

/**
 * 满足条件执行
 * @param condition 条件
 * @param error 异常反馈信息
 * @param success 符合需要执行的函数
 */
fun conditionExe(condition: Boolean, error: String = "操作异常", success: () -> Unit) {
    if (condition)
        success.invoke()
    else
        Toast.show(text = error)
}
