package com.xiaoming.utils

import com.android.ddmlib.InstallReceiver
import com.android.ddmlib.MultiLineReceiver
import com.xiaoming.state.GlobalState
import kotlinx.coroutines.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AdbUtil {
    /**
     * 执行shell命令
     * @param cmd shell命令
     * @param receiver 执行shell回调
     */

    interface ShellCallBack {
        fun result(value: String)
    }

    fun shell(
        cmd: String,
        receiver: MultiLineReceiver? = null,
        result: ShellCallBack? = null
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.executeShellCommand(
                cmd,
                receiver ?: object : MultiLineReceiver() {
                    override fun isCancelled(): Boolean = false

                    override fun processNewLines(lines: Array<out String>?) {
                        result?.result(lines?.joinToString("") ?: "")
                    }
                })
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
                val fileName = currentDateTime.format(formatter) + ".png"
                val screenshot = it.screenshot.asBufferedImage()
                ClipboardUtils.setClipboardImage(screenshot)
                ImageIO.write(screenshot, "png", File(GlobalState.desktop.value, fileName))
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


    /**
     * 查找当前activity
     * @param delayMs 超时时间
     */
    suspend fun findCurrentActivity() = suspendCoroutine {
        CoroutineScope(Dispatchers.Default).launch {
            val shell = shell("dumpsys window | grep mCurrentFocus", 200)
            val regex = Regex(pattern = """\s\S+/\S+}""")
            val res = regex.find(shell)?.value?.replace("}", "")?.trim() ?: ""
            //复制到剪切板
            ClipboardUtils.setSysClipboardText(res)
            it.resume(res)
        }
    }


    /**
     * 执行shell并且返回结果
     * @param cmd shell命令
     * @param timeMillis 超时时间
     */
    suspend fun shell(cmd: String, timeMillis: Long) = suspendCoroutine {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.executeShellCommand(cmd, object : MultiLineReceiver() {
                override fun isCancelled(): Boolean = false
                override fun processNewLines(lines: Array<out String>?) {
                    it.resume(lines?.joinToString("") ?: "")
                }
            })
            delay(timeMillis)
            it.resume("")
        }
    }
}