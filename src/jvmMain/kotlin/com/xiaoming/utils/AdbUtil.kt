package com.xiaoming.utils

import com.android.ddmlib.*
import com.xiaoming.state.GlobalState
import com.xiaoming.widget.SimpleDialog
import kotlinx.coroutines.*
import org.jetbrains.skiko.hostOs
import theme.GOOGLE_YELLOW
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * 获取 adb
 */
fun getAdb() = if (hostOs.isWindows) "adb.exe" else "adb"

object AdbUtil {

    /**
     * ⚠️危险目录
     */
    private val riskPathList =
        listOf(
            "/",
            "/dev",
            "/etc",
            "/data",
            "/sys",
            "/system",
            "/system_ext",
            "/vendor",
            "/system_dlkm",
            "/storage",
            "/config",
            "/sdcard",
            "/mnt",
            "/init",
            "/init.environ.rc",
            "/init.recovery.qcom.rc",
        )

    /**
     * 执行在控制台命令
     * @param cmd adb 后的命令 === adb cmd
     */
    private fun shellByProcess(cmd: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.let {
                LogUtil.d("${GlobalState.adb.value} -s $it $cmd")
                BashUtil.execCommand("${GlobalState.adb.value} -s $it $cmd")
            }
        }
    }

    fun devices() {
        CoroutineScope(Dispatchers.Default).launch {
            BashUtil.execCommand("${GlobalState.adb.value} devices")
        }
    }

    /**
     * 无需结果的cmd命令
     * @param cmd shell命令
     */
    fun shell(cmd: String) {
        CoroutineScope(Dispatchers.Default).launch {
            LogUtil.d("${GlobalState.adb.value} shell \"$cmd\"")
            GlobalState.sCurrentDevice.value?.executeShellCommand(cmd.replace("adb shell",""), object : MultiLineReceiver() {
                override fun isCancelled(): Boolean = false
                override fun processNewLines(lines: Array<out String>?) {
                    lines?.forEach {
                        LogUtil.d(it)
                    }
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
                        if (str.isNotBlank()) {
                            suspendSingle.resume(str)
                            LogUtil.d("${GlobalState.adb.value} shell \"$cmd\"\n$str")
                        }
                    }
                }
            })
            delay(timeMillis)
            if (isActive && !resume) {
                LogUtil.d("${GlobalState.adb.value} shell '$cmd' timout,timeout = $timeMillis")
                resume = true
                suspendSingle.resume("")
            }
        }
    }

    fun getProp(key: String): String {
        return GlobalState.sCurrentDevice.value?.getProperty(key) ?: ""
    }

    /**
     * 删除文件
     * @param path 路径
     */
    fun rf(path: String) {
        if (path.isBlank()) return
        if (riskPathList.contains(path)) {
            SimpleDialog.info(
                text = "Deleting a `$path` is dangerous. Therefore, manually delete it",
                titleText = "⚠️警告",
                titleTextColor = GOOGLE_YELLOW
            )
            return
        }
        CoroutineScope(Dispatchers.Default).launch {
            shell("rm -rf $path")
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
                ImageIO.write(screenshot, "png", File(GlobalState.sFileSavePath.value, fileName))
            }
        }
    }

    /**
     * root 超级用户权限
     */
    fun root() {
        CoroutineScope(Dispatchers.Default).launch {
            LogUtil.d("adb root")
            shellByProcess("root")
        }
    }


    /**
     * tcpip
     */
    fun tcpip(port: String) {
        shellByProcess("tcpip $port")
    }

    /**
     * reboot 重启
     */
    fun reboot() {
        CoroutineScope(Dispatchers.Default).launch {
            LogUtil.d("adb reboot")
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
            LogUtil.d("am start packageName = $packageName,launchActivity = $launchActivity")
            if (launchActivity.isBlank()) shell("monkey -p $packageName -v 1") else shell("am start $launchActivity")
        }
    }

    /**
     * 根据包名强制停止应用
     * @param packageName 包名
     */
    fun forceStop(packageName: String) {
        LogUtil.d("${GlobalState.adb.value} shell am force-stop $packageName")
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.forceStop(packageName)
        }
    }

    /**
     * 根据包名kill应用
     * @param packageName 包名
     */
    fun kill(packageName: String){
        LogUtil.d("${GlobalState.adb.value} shell kill $packageName")
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.kill(packageName)
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
            LogUtil.d("adb uninstall $packageName")
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
            LogUtil.d("adb install $packagePath")
            GlobalState.sCurrentDevice.value?.installPackage(packagePath, true, receiver)
        }
    }


    /**
     * mv 命令
     * @param start 开始路径
     * @param end 目标路径
     */
    fun mv(start: String, end: String) {
        GlobalState.sCurrentDevice.value?.let { device ->
            CoroutineScope(Dispatchers.Default).launch {
                shell("mv $start $end")
            }
        }
    }


    /**
     * touch 命令
     * @param path 文件路径
     */
    fun touch(path: String) {
        GlobalState.sCurrentDevice.value?.let {
            CoroutineScope(Dispatchers.Default).launch {
                shell("touch $path")
            }
        }
    }

    /**
     * mkdir命令
     * @param path 文件路径
     * @param auth 权限
     */
    fun mkdir(path: String, auth: Int) {
        GlobalState.sCurrentDevice.value?.let {
            CoroutineScope(Dispatchers.Default).launch {
                shell("mkdir -m $auth $path")
            }
        }
    }


    /**
     * mkdir命令
     * @param path 文件路径
     * @param auth 权限
     */
    fun chmod(path: String, auth: Int) {
        GlobalState.sCurrentDevice.value?.let {
            CoroutineScope(Dispatchers.Default).launch {
                shell("chmod $auth $path")
            }
        }
    }


    /**
     * 拉取文件到本地
     * @param local 本地路径
     * @param remote 目标调试设备路径
     */
    fun pull(remote: String, local: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.let {
                if (hostOs.isMacOS) {
                    FileUtil.writeShell("pull", "${GlobalState.adb.value} -s $it pull $remote $local/")
                    BashUtil.execCommand("open -b com.apple.terminal ${GlobalState.workDir + "/" + "pull.sh"}")
                } else if (hostOs.isWindows) {
                    BashUtil.execCommand("cmd.exe /c start cmd.exe /K ${GlobalState.adb.value} -s $it pull $remote $local/")
                } else {
                    shellByProcess("pull $remote $local/")
                }

            }
        }
    }

    /**
     * 推送本地文件到设备
     * @param local 本地路径
     * @param remote 目标调试设备路径
     */
    fun push(local: String, remote: String) {
        CoroutineScope(Dispatchers.Default).launch {
            GlobalState.sCurrentDevice.value?.let {
                if (hostOs.isMacOS) {
                    FileUtil.writeShell("push", "${GlobalState.adb.value} -s $it push $local $remote/")
                    BashUtil.execCommand("open -b com.apple.terminal ${GlobalState.workDir + "/" + "push.sh"}")
                } else if (hostOs.isWindows) {
                    BashUtil.execCommand("cmd.exe /c start cmd.exe /K ${GlobalState.adb.value} -s $it push $local $remote/")
                } else {
                    shellByProcess("push $local $remote/")
                }
            }
        }
    }

    /**
     * 对应用进行授权
     * @param packageName 应用名称
     * @callback 执行后回调
     */
    fun grant(packageName: String, callback: () -> Unit) {
        findAllPermissionList(packageName) { it ->
            it.forEach {
                if (it.startsWith("android.permission"))
                    grant(packageName, it)
            }
            callback.invoke()
        }
    }

    /**
     * 对应用进行取消授权
     * @param packageName 应用名称
     * @callback 执行后回调
     */
    fun unGrant(packageName: String, callback: () -> Unit) {
        findAllPermissionList(packageName) { it ->
            it.forEach {
                if (it.startsWith("android.permission"))
                    unGrant(packageName, it)
            }
            callback.invoke()
        }
    }

    /**
     * 对应用进行授权
     * @param packageName 应用名称
     * @param permission 权限名称
     */
    private fun grant(packageName: String, permission: String) {
        shell("pm grant $packageName $permission")
    }


    private fun unGrant(packageName: String, permission: String) {
        shell("pm revoke $packageName $permission")
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
     * 查询应用所需权限
     * @param packageName 包名
     */
    private fun findAllPermissionList(packageName: String, block: (Set<String>) -> Unit) {
        val permissionSet: HashSet<String> = HashSet()
        val permissionStr: ArrayList<String> = ArrayList()
        GlobalState.sCurrentDevice.value?.executeShellCommand("pm dump $packageName", object : MultiLineReceiver() {
            override fun isCancelled(): Boolean = false
            override fun processNewLines(lines: Array<out String>?) {
                lines?.let {
                    permissionStr.addAll(it)
                }
            }

            override fun done() {
                super.done()
                LogUtil.d("findAllPermissionList done")
                for (value in permissionStr) {
                    if (value.contains("android.permission.")) {
                        val permissionLine = value.replace(" ", "").split(":")
                        if (permissionLine.isEmpty()) {
                            continue
                        }
                        permissionSet.add(permissionLine[0])
                    }
                }
                block.invoke(permissionSet)
            }
        })
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


    suspend fun path(packageName: String): String {
        return shell("pm path $packageName", 300)
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


    /**
     * dump命令
     * @param packageName 包名
     * @param filter 过滤关键词
     */
    suspend fun dump(packageName: String, filter: String = "", timeout: Long = 300) = suspendCoroutine {
        CoroutineScope(Dispatchers.Default).launch {
            val result =
                shell(
                    "pm dump $packageName${if (filter.isNotBlank()) " | grep -E '$filter'" else ""}",
                    timeout
                )
            it.resume(result)
        }
    }


    /**
     * 查找该${path}路径下的文件
     * @param path 路径
     * @param func 回调
     */
    fun findFileList(
        path: String,
        func: (FileListingService.FileEntry?, Array<out FileListingService.FileEntry>?) -> () -> Unit
    ) {
        LogUtil.d("findFileList path = $path")
        GlobalState.sFileListingService.value?.let {
            it.getChildren(
                if (path.isEmpty()) it.root else FileListingService.FileEntry(
                    it.root,
                    path,
                    FileListingService.TYPE_DIRECTORY,
                    false
                ),
                false,
                object : FileListingService.IListingReceiver {
                    override fun setChildren(
                        entry: FileListingService.FileEntry?,
                        children: Array<out FileListingService.FileEntry>?
                    ) {
                        func(entry, children).invoke()
                    }

                    override fun refreshEntry(entry: FileListingService.FileEntry?) {
                        LogUtil.d("refreshEntry")
                    }

                }
            )
        }
    }

    /**
     * 查找当前task
     * @param keyWord 关键词
     * @param block 回调函数
     */
    fun findProcessByKeyword(keyWord: String = "system", needA: Boolean = true, block: (List<String>) -> Unit) {
        val set = mutableSetOf<String>()
        val cmd = "ps ${if (needA) "-A" else ""} | grep $keyWord"

        GlobalState.sCurrentDevice.value?.executeShellCommand(cmd, object : MultiLineReceiver() {
            override fun isCancelled(): Boolean {
                return false
            }

            override fun processNewLines(lines: Array<out String>?) {
                lines?.let {
                    set.addAll(it)
                }
            }

            override fun done() {
                super.done()
                LogUtil.d("findProcessByKeyword done")
                block.invoke(set.filter { it.isNotEmpty() })
            }
        })
    }

}