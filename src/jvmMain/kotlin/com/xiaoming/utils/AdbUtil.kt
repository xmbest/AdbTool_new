package com.xiaoming.utils

import com.android.ddmlib.InstallReceiver
import com.android.ddmlib.MultiLineReceiver
import com.android.ddmlib.Timeout
import com.xiaming.module.AdbModule
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

    /**
     * 无需结果的cmd命令
     * @param cmd shell命令
     */
    fun shell(cmd: String) {
        CoroutineScope(Dispatchers.Default).launch {
            println(cmd)
            GlobalState.sCurrentDevice.value?.executeShellCommand(cmd, object : MultiLineReceiver() {
                override fun isCancelled(): Boolean = false
                override fun processNewLines(lines: Array<out String>?) {
                }
            })
        }
    }


    /**
     * 执行shell并且返回结果
     * @param cmd shell命令
     * @param timeMillis 超时时间
     */
    suspend fun shell(cmd: String, timeMillis: Long) = suspendCoroutine {
        val suspendSingle = it
        CoroutineScope(Dispatchers.Default).launch {
            var resume = false
            GlobalState.sCurrentDevice.value?.executeShellCommand(cmd, object : MultiLineReceiver() {
                override fun isCancelled(): Boolean = false
                override fun processNewLines(lines: Array<out String>?) {
                    if (lines?.isNotEmpty() == true && isActive && !resume) {
                        resume = true
                        val str = lines.filter { line -> line.isNotEmpty() }.joinToString("\n")
                        if (str.isNotBlank()){
                            suspendSingle.resume(str)
                            println("=======================")
                            println(str)
                        }
                    }
                }
            })
            println(cmd)
            delay(timeMillis)
            if (isActive && !resume){
                resume = true
                suspendSingle.resume("")
            }
        }
    }

    fun getProp(key: String): String {
        return GlobalState.sCurrentDevice.value?.getProperty(key) ?: ""
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
                val desktop =
                    if (GlobalState.sHomePath.value.contains("Desktop")) File(GlobalState.sHomePath.value) else File(
                        GlobalState.sHomePath.value,
                        "Desktop"
                    )
                val screenshot = it.screenshot.asBufferedImage()
                ClipboardUtils.setClipboardImage(screenshot)
                ImageIO.write(screenshot, "png", File(desktop, fileName))
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
        CoroutineScope(Dispatchers.Default).launch {
            val launchActivity = getLaunchActivity(packageName)
            println("am start packageName = $packageName,launchActivity = $launchActivity")
            if (launchActivity.isBlank()) shell("monkey -p $packageName -v 1") else shell("am start $launchActivity")
        }
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
     * 拉取文件到本地
     * @param local 本地路径
     * @param remote 目标调试设备路径
     */
    fun pull(remote: String, local: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.pullFile(remote, local)
        }
    }

    /**
     * 推送本地文件到设备
     * @param local 本地路径
     * @param remote 目标调试设备路径
     */
    fun push(local: String, remote: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.pushFile(local, remote)
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
     * 查找应用启动activity
     */
    suspend fun getLaunchActivity(packageName: String) = suspendCoroutine {
        CoroutineScope(Dispatchers.Default).launch {
            val launchActivity = dumpsys(packageName, "-A 1 MAIN", 500)
            if (launchActivity.isBlank()) return@launch it.resume("")
            val outLines = launchActivity.lines()
            if (outLines.isEmpty()) {
                return@launch it.resume("")
            } else {
                for (value in outLines) {
                    if (value.contains("$packageName/")) {
                        return@launch it.resume(
                            value.substring(
                                value.indexOf("$packageName/"), value.indexOf(" filter")
                            )
                        )
                    }
                }
                return@launch it.resume("")
            }
        }

    }


    /**
     * dumpsys命令
     * @param packageName 包名
     * @param filter 过滤关键词
     */
    private suspend fun dumpsys(packageName: String, filter: String = "", timeout: Long = 300) = suspendCoroutine {
        CoroutineScope(Dispatchers.Default).launch {
            val result =
                shell(
                    "dumpsys package $packageName${if (filter.isNotBlank()) " findStr -E '$filter'" else ""}",
                    timeout
                )
            it.resume(result)
        }
    }

}