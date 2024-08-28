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
    /**
     * 页面路由配置
     */
    val pages = listOf(
        Page("常用功能", ImgUtil.getRealLocation("pin")) { QuickScreen() },
        Page("进程管理", ImgUtil.getRealLocation("process")) { TaskScreen() },
        Page("文件管理", ImgUtil.getRealLocation("folder")) { FileScreen() },
        Page("快捷广播", ImgUtil.getRealLocation("broad")) { BroadScreen() },
        Page("程序设置", ImgUtil.getRealLocation("settings")) { SettingScreen() }
    )

    /**
     * 已连接设备列表
     */
    val sDeviceSet = mutableSetOf<IDevice>()

    /**
     * 当前选中设备
     */
    val sCurrentDevice = mutableStateOf<DeviceImpl?>(null)

    /**
     * 文件操作service
     * @see FileListingService
     */
    val sFileListingService = mutableStateOf<FileListingService?>(null)

    /**
     * 设备切换列表是否展开
     */
    val sExpanded = mutableStateOf(false)

    /**
     * 当前选中路由的页码
     */
    val sCurrentIndex = mutableStateOf(0)

    /**
     * 程序默认打开页面
     */
    val sDefaultStartIndex = mutableStateOf(LocalDataKey.sDefaultPageIndex.second)

    /**
     * 文件保存路径
      */
    val sFileSavePath = mutableStateOf(LocalDataKey.sFileSavePath.second)

    /**
     * 家目录
     */
    val sHomePath: String = System.getProperty("user.home")

    /**
     * 任务管理搜索关键词
     */
    val sTaskKeyWords = mutableStateOf(LocalDataKey.sTaskSearchKeyWords.second)

    /**
     * adb 环境配置记忆
     */
    val adbSelect = mutableStateOf(LocalDataKey.sAdbSelect.second)

    /**
     * 自定义的 adb 执行程序路径
     */
    val adbCustomPath = mutableStateOf(LocalDataKey.sAdbCustomPath.second)

    /**
     * 真实执行 adb 的执行程序
     */
    val adb = mutableStateOf("adb")

    /**
     * 是否保存日志开关
     */
    val saveLog = mutableStateOf(LocalDataKey.sSaveLog.second)

    /**
     * wifi adb 端口
     */
    val port = mutableStateOf("5555")

    /**
     * 文件写入目录
     */
    val workDir: String = File(sHomePath,".adbTool").absolutePath


}
