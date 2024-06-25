package com.xiaoming.state

import androidx.compose.runtime.mutableStateOf
import com.android.ddmlib.FileListingService
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaoming.utils.ImgUtil
import com.xiaoming.entity.Page
import com.xiaoming.screen.*
import java.io.File

object GlobalState{
    // 配置布局列表数据
    val pages = listOf(
        Page("常用功能", ImgUtil.getRealLocation("pin")) { QuickScreen() },
        Page("进程管理", ImgUtil.getRealLocation("process")) { TaskScreen() },
        Page("文件管理", ImgUtil.getRealLocation("folder")) { FileScreen() },
        Page("快捷指令", ImgUtil.getRealLocation("quick")) { OrderScreen() },
        Page("快捷广播", ImgUtil.getRealLocation("broad")) { BroadScreen() },
        Page("命令泛化", ImgUtil.getRealLocation("generalize")) { CommandScreen() },
        Page("程序设置", ImgUtil.getRealLocation("settings")) { SettingScreen() }
    )
    // 已连接设备列表
    val sDeviceSet = mutableSetOf<IDevice>()
    // 当前选中设备
    val sCurrentDevice = mutableStateOf<DeviceImpl?>(null)
    // 文件操作service
    val sFileListingService = mutableStateOf<FileListingService?>(null)
    // 设备列表是否展开
    val sExpanded = mutableStateOf(false)
    // 当前选中的页码
    val sCurrentIndex = mutableStateOf(0)
    val sDefaultStartIndex = mutableStateOf(LocalDataKey.sDefaultPageIndex.second)
    // 文件保存路径
    val sFileSavePath = mutableStateOf(LocalDataKey.sFileSavePath.second)
    // 文件存储路径
    val sHomePath: String = System.getProperty("user.home")
    // 任务管理搜索关键词
    val sTaskKeyWords = mutableStateOf(LocalDataKey.sTaskSearchKeyWords.second)
    // adb 选项
    val adbSelect = mutableStateOf(LocalDataKey.sAdbSelect.second)
    // 自定义adb路径
    val adbCustomPath = mutableStateOf(LocalDataKey.sAdbCustomPath.second)
    // adb执行路径
    val adb = mutableStateOf("adb")
    // 是否保存日志
    val saveLog = mutableStateOf(LocalDataKey.sSaveLog.second)
    val port = mutableStateOf("5555")
    // 文件写入目录
    val workDir: String = File(sHomePath,"AdbTool").absolutePath


}
