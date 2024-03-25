package com.xiaoming.utils

import com.android.ddmlib.IShellOutputReceiver
import com.android.ddmlib.InstallReceiver
import com.xiaoming.state.GlobalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

object AdbUtil {

    //默认执行
    private val receiver = object : IShellOutputReceiver {
        override fun addOutput(data: ByteArray?, offset: Int, length: Int) {

        }

        override fun flush() {
        }

        override fun isCancelled(): Boolean {
            return true
        }

    }

    /**
     * 执行shell命令
     * @param cmd shell命令
     * @param toast 是否需要弹窗
     * @param iShellOutputReceiver 执行shell回调
     */
    fun shell(
        cmd: String, toast: Boolean = false, iShellOutputReceiver: IShellOutputReceiver? = null
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.executeShellCommand(cmd, iShellOutputReceiver ?: receiver)
        }
    }


    /**
     *  adb she'll input keyevent $key
     */
    fun inputKey(key: Int) {
        shell("input keyevent $key")
    }

    /**
     * 屏幕截图到桌面、剪切板
     */
    fun screenshot() {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.let {
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss")
                val path = currentDateTime.format(formatter) + ".png"
                val screenshot = it.screenshot.asBufferedImage()
                ClipboardUtils.setClipboardImage(screenshot)
                ImageIO.write(screenshot, "png", File(GlobalState.desktop.value, path))
            }
        }
    }

    /**
     * root 超级用户权限
     */
    fun root() {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.root()
        }
    }

    /**
     * reboot 重启
     */
    fun reboot() {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.reboot("")
        }
    }

    /**
     * 根据包名启动应用
     * @param packageName 包名
     */
    fun start(packageName: String) {
        shell("am start $packageName")
    }

    /**
     * 根据包名强制停止应用
     * @param packageName 包名
     */
    fun forceStop(packageName: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.forceStop(packageName)
        }
    }

    /**
     * 根据包名清空应用数据
     * @param packageName 包名
     */
    fun clear(packageName: String) {
        shell("pm clear $packageName")
    }

    /**
     * 根据包名卸载应用
     * @param packageName 包名
     */
    fun uninstall(packageName: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.uninstallPackage(packageName)
        }
    }

    /**
     * 安装应用
     * @param packagePath 应用路径
     * @param receiver 执行回调
     */
    fun install(packagePath: String, receiver: InstallReceiver? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.installPackage(packagePath, true, receiver)
        }
    }
}