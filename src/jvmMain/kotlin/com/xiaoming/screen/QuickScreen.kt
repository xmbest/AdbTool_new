package com.xiaoming.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.xiaming.utils.ImgUtil.getRealLocation
import com.xiaoming.componts.*
import com.xiaoming.entity.DeviceInfo
import com.xiaoming.entity.KeyMapper
import com.xiaoming.utils.AdbUtil
import config.route_left_item_color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.GOOGLE_BLUE
import theme.GOOGLE_GREEN
import theme.GOOGLE_RED
import javax.swing.filechooser.FileSystemView

val packageName = mutableStateOf("")
val quickSettingKeyword = mutableStateOf("")
val appList = mutableStateListOf<String>()
val expanded = mutableStateOf(false)
val deviceInfo = mutableStateOf(DeviceInfo())
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuickScreen() {
    val keyMapperList1 = listOf(
        KeyMapper(getRealLocation("task"), 187, "任务列表"),
        KeyMapper(getRealLocation("home"), 3, "回到桌面"),
        KeyMapper(getRealLocation("back"), 4, "返回上级"),
        KeyMapper(getRealLocation("power"), 26, "锁定屏幕")
    )
    val keyMapperList2 = listOf(
        KeyMapper(getRealLocation("plus"), 24, "增加音量"),
        KeyMapper(getRealLocation("minus"), 25, "减少音量"),
        KeyMapper(getRealLocation("up"), 221, "增加亮度"),
        KeyMapper(getRealLocation("down"), 220, "减少亮度"),

        )

    val keyMapperList3 = listOf(
        KeyMapper(getRealLocation("down"), 1, "显示状态栏"),
        KeyMapper(getRealLocation("up"), 2, "隐藏状态栏"),
        KeyMapper(getRealLocation("image"), 0, "截图"),
        KeyMapper(getRealLocation("settings"), 0, "进入设置")
    )

    val keyMapperList4 = listOf(
        KeyMapper(getRealLocation("eye"), 1, "查看当前Activity"),
        KeyMapper(getRealLocation("delete"), 2, "清理logcat缓存"),
        KeyMapper(getRealLocation("android"), 0, "挂载设备"),
        KeyMapper(getRealLocation("sync"), 0, "重启设备")
    )
    val scroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().fillMaxHeight().verticalScroll(scroll)) {

        General(title = "系统信息", height = 2, color = GOOGLE_GREEN) {
            Row(modifier = Modifier.fillMaxSize().padding(start = 20.dp, top = 20.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    SelectionContainer {
                        Text(
                            "${deviceInfo.value.brand} ${deviceInfo.value.device} \n" +
                                    "安卓版本: ${deviceInfo.value.androidVersion} \n" +
                                    "系统版本: ${deviceInfo.value.systemVersion} \n" +
                                    "代号: ${deviceInfo.value.model} \n" +
                                    "处理器: ${deviceInfo.value.cpu} \n" +
                                    "序列号: ${deviceInfo.value.serialNo} \n" +
                                    "分辨率: ${deviceInfo.value.density} \n" +
                                    "可用内存: ${deviceInfo.value.memory} \n"
                        )
                    }
                }
            }

        }

        General(title = "按键模拟", height = 4, content = {
            ContentMoreRowColumn {
                ContentNRow {
                    keyMapperList1.forEach {
                        Item(it.icon, it.name) {
                            AdbUtil.inputKey(it.key)
                        }
                    }
                }
                ContentNRow {
                    keyMapperList2.forEach {
                        Item(it.icon, it.name) {
                            AdbUtil.inputKey(it.key)
                        }
                    }
                }
                ContentNRow {
                    Item(keyMapperList3[0].icon, keyMapperList3[0].name) {
                        AdbUtil.shell("service call statusbar 1")
                    }
                    Item(keyMapperList3[1].icon, keyMapperList3[1].name) {
                        AdbUtil.shell("service call statusbar 2")

                        println(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
                    }
                    Item(keyMapperList3[2].icon, keyMapperList3[2].name) {
                        AdbUtil.screenshot()
                    }
                    Item(keyMapperList3[3].icon, keyMapperList3[3].name) {
                        AdbUtil.shell("am start  -n com.android.settings/com.android.settings.Settings")
                    }
                }
                ContentNRow {
                    Item(keyMapperList4[0].icon, keyMapperList4[0].name, false) {
                        CoroutineScope(Dispatchers.Default).launch {
                            val str = AdbUtil.findCurrentActivity()
                            if (str.isNotBlank()) {
                                SimpleDialog.info(str,"current activity:")
                            }
                        }
                    }
                    Item(keyMapperList4[1].icon, keyMapperList4[1].name, false) {
                        SimpleDialog.confirm("是否清理logcat缓存"){
                            AdbUtil.shell("logcat -c")
                        }
                    }
                    Item(keyMapperList4[2].icon, keyMapperList4[2].name, false) {

                    }
                    Item(keyMapperList4[3].icon, keyMapperList4[3].name, false) {
                        SimpleDialog.confirm("是否重启设备"){
                            AdbUtil.reboot()
                        }
                    }
                }
            }
        })
        General(title = "应用相关", color = GOOGLE_RED, height = 2, content = {
            ContentMoreRowColumn {
                ContentNRow {
                    Item(getRealLocation("start"), "启动应用") {
                        applicationManager {
                            AdbUtil.start(packageName.value)
                        }
                    }
                    Item(getRealLocation("power"), "停止运行", false) {
                        SimpleDialog.confirm("是否停止运行 ${packageName.value}?"){
                            AdbUtil.forceStop(packageName.value)
                        }
                    }
                    Item(getRealLocation("clear"), "清除数据", false) {
                        SimpleDialog.confirm("是否清除 ${packageName.value} 数据?"){
                            AdbUtil.clear(packageName.value)
                        }
                    }
                    Item(getRealLocation("delete"), "卸载应用", false) {
                        SimpleDialog.confirm("是否卸载应用 ${packageName.value}?"){
                            AdbUtil.uninstall(packageName.value)
                        }
                    }
                }
                ContentNRow {
                    Item(getRealLocation("go"), "授予所有权限", false) {

                    }

                    Item(getRealLocation("back"), "撤销所有权限", false) {

                    }

                    Item(getRealLocation("eye"), "查看应用信息", false) {

                    }
                    Item(getRealLocation("save"), "保存程序到电脑", false) {
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
                                expanded = expanded.value ,
                                onDismissRequest = {
                                    expanded.value  = false
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
                                if (appList.size == 0) {
                                    DropdownMenuItem(onClick = {
                                        expanded.value  = false
                                    }) {
                                        Text(text = "未找到相关应用")
                                    }
                                } else {
                                    appList.forEach {
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

fun applicationManager(runnable: () -> Unit) {
    if (packageName.value.isBlank()) {
        CoroutineScope(Dispatchers.Default).launch {

        }
    } else {
        runnable.invoke()
    }
}

fun syncAppList(keyWord: String = "") {
    val list = ArrayList<String>()
    var cmd = "pm list packages -f"
    if (keyWord.isNotBlank()) {
        cmd += "| grep -E '$keyWord'"
    }
    CoroutineScope(Dispatchers.Default).launch{
        val packages = AdbUtil.shell(cmd,500)
        val split = packages.split("\n").filter { it.isNotBlank() }.map { it.substring(8) }
        split.forEach {
            val index = it.lastIndexOf("=")
            val packageName = it.substring(index + 1)
            list.add(packageName)
        }
        appList.clear()
        appList.addAll(list)
    }
}
