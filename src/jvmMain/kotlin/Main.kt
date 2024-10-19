import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.gson.reflect.TypeToken
import com.xiaoming.config.FastBroadConfig
import com.xiaoming.config.window_height
import com.xiaoming.config.window_width
import com.xiaoming.module.AdbModule
import com.xiaoming.router.Router
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import com.xiaoming.theme.GOOGLE_BLUE
import com.xiaoming.utils.*
import java.awt.Dimension
import java.awt.Toolkit
import java.io.File

@Composable
@Preview
fun App() {
    MaterialTheme(lightColors(primary = GOOGLE_BLUE)) {
        Router()
    }
}

fun main() = application {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    /**
     * 页面宽度
     */
    val width = window_width

    /**
     * 页面高度
     */
    val height = window_height

    /**
     * x中心
     */
    val x: Double = screenSize.getWidth() / 2 - width / 2

    /**
     * y 中心
     */
    val y: Double = screenSize.getHeight() / 2 - height / 2
    val state = rememberWindowState(width = width.dp, height = height.dp, position = WindowPosition(x.dp, y.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "adbTool by desktop",
        state = state,
        icon = painterResource(ImgUtil.getLogoByBrand(""))
    ) {
        LaunchedEffect(state) {
            snapshotFlow { state.isMinimized }
                .launchIn(this)
        }
        writeFile()
        App()
        dataInit()
    }
}


/**
 * 数据初始化，读取默认配置、用户配置
 */
private fun dataInit() {
    CoroutineScope(Dispatchers.Default).launch {
        delay(10)
        // 解析快捷广播UI
        val uuidList = arrayListOf<String>()
        try {
            val jsonFile = File(GlobalState.workDir, "cfg.json")
            val jsonStr = jsonFile.readText(Charsets.UTF_8)
            val uuidResult = parseJsonCfg(jsonStr)
            uuidList.addAll(uuidResult)
        } catch (e: Exception) {
            useResource("cfg/cfg.json") {
                val jsonStr = it.reader(Charsets.UTF_8).readText()
                val uuidResult = parseJsonCfg(jsonStr)
                uuidList.addAll(uuidResult)
            }
        }

        // 加载配置文件
        PropertiesUtil.all().forEach { entry ->
            LocalDataKey.sList.forEach {
                if (it.first == entry.key.toString()) {
                    LocalDataKey.getMap()[it.first]?.value = entry.value.toString()
                    println("key = ${it.first},value = ${entry.value}")
                }
            }
            // 删除无效key-uuid
            if (entry.key.toString().startsWith("uuid-") && !uuidList.contains(entry.key.toString())) {
                PropertiesUtil.remove(entry.key.toString())
            }
        }
        // 设置启动页
        GlobalState.sCurrentIndex.value = GlobalState.sDefaultStartIndex.value.toInt()
        // 设置 adb环境
        AdbModule.changeAdb(GlobalState.adbSelect.value)
    }

}

/**
 * 写入必需文件
 */
private fun writeFile() {
    val adb = Pair(getAdb(), "adb")
    val properties = Pair("cfg.properties", "cfg")
    val json = Pair("cfg.json", "cfg")
    val push = Pair("push.sh", "shell")
    val pull = Pair("pull.sh", "shell")
    val listFile = listOf(adb, properties, json, push, pull)
    val parentDir = File(GlobalState.workDir)
    if (!parentDir.exists()) {
        parentDir.mkdirs()
    }
    listFile.forEach {
        val file = File(parentDir, it.first)
        if (!file.exists()) {
            file.createNewFile()
            file.setExecutable(true)
            useResource(it.second + "/" + it.first) { inputStream ->
                inputStream.copyTo(file.outputStream())
            }
        }
    }
}

/**
 * 处理cfg.json
 * @param jsonStr 字符串
 */
private fun parseJsonCfg(jsonStr: String?): List<String> {
    jsonStr?.let {
        LogUtil.d("jsonStr = $jsonStr")
        val listMap: List<Map<String,Any>> = GsonUtil.gson.fromJson(jsonStr, object : TypeToken<List<Map<String,Any>>>() {}.type)
        val uuidList = arrayListOf<String>()
        listMap.forEach {
            LogUtil.d(it.toString())
            uuidList.add(it["uuid"].toString())
            FastBroadConfig.list.add(GsonUtil.gson.fromJson(GsonUtil.gson.toJson(it),
                FastBroadConfig.map[it["type"]]
            ))
        }
        LogUtil.d("FastBroadConfig.list.size = ${FastBroadConfig.list.size}")
        return uuidList
    }
    return listOf()
}