package com.xiaoming.state

import androidx.compose.runtime.mutableStateOf
import com.android.ddmlib.FileListingService
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaoming.screen.FileScreen
import com.xiaming.screen.SettingScreen
import com.xiaoming.utils.ImgUtil
import com.xiaoming.entity.Page
import com.xiaoming.screen.TaskScreen
import com.xiaoming.screen.OrderScreen
import com.xiaoming.screen.QuickScreen
import javax.swing.filechooser.FileSystemView

object GlobalState{
    // 配置布局列表数据
    val pages = listOf(
        Page("常用功能", ImgUtil.getRealLocation("pin")) { QuickScreen() },
        Page("进程管理", ImgUtil.getRealLocation("process")) { TaskScreen() },
        Page("文件管理", ImgUtil.getRealLocation("folder")) { FileScreen() },
        Page("快捷指令", ImgUtil.getRealLocation("order")) { OrderScreen() },
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
    // 桌面位置
    val sDesktopPath = mutableStateOf(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
    // 文件存储路径
    val sHomePath: String = System.getProperty("user.home")
    // 任务管理搜索关键词
    val sTaskKeyWords = mutableStateOf(StateKeyValue.sTaskSearchKeyWords.second)
    // adb路径，配置环境变量即 adb
    val adb = mutableStateOf("adb")
    val port = mutableStateOf("5555")
}
