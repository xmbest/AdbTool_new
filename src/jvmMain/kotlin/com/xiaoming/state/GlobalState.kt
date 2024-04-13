package com.xiaoming.state

import androidx.compose.runtime.mutableStateOf
import com.android.ddmlib.FileListingService
import com.android.ddmlib.IDevice
import com.android.ddmlib.internal.DeviceImpl
import com.xiaming.screen.FileScreen
import com.xiaming.screen.HomeScreen
import com.xiaming.screen.SettingScreen
import com.xiaming.utils.ImgUtil
import com.xiaoming.entity.Page
import com.xiaoming.screen.QuickScreen
import javax.swing.filechooser.FileSystemView

object GlobalState{
    // 配置布局列表数据
    val pages = listOf(
        Page("快捷功能", ImgUtil.getRealLocation("pin")) { QuickScreen() },
        Page("文件管理", ImgUtil.getRealLocation("folder")) { FileScreen() },
        Page("应用管理", ImgUtil.getRealLocation("pin")) { HomeScreen() },
        Page("广播模拟", ImgUtil.getRealLocation("pin")) { HomeScreen() },
        Page("程序设置", ImgUtil.getRealLocation("pin")) { SettingScreen() }
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
    val sHomePath = mutableStateOf(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
    //
    val adb = mutableStateOf("adb")
}
