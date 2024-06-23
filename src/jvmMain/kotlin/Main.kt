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
import com.xiaming.config.window_height
import com.xiaming.config.window_width
import com.xiaoming.module.AdbModule
import com.xiaoming.utils.ImgUtil
import com.xiaoming.router.Router
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import com.xiaoming.utils.PropertiesUtil
import com.xiaoming.utils.initOsType
import com.xiaoming.utils.isWindows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import theme.GOOGLE_BLUE
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
    //显示大小
    val width = window_width
    val height = window_height
    //居中显示
    val x: Double = screenSize.getWidth() / 2 - width / 2
    val y: Double = screenSize.getHeight() / 2 - height / 2
    val state = rememberWindowState(width = width.dp, height = height.dp, position = WindowPosition(x.dp, y.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "AdbTool By Desktop",
        state = state,
        icon = painterResource(ImgUtil.getLogoByBrand(""))
    ) {
        LaunchedEffect(state) {
            snapshotFlow { state.isMinimized }
                .onEach(::onMinimized).launchIn(this)
        }
        initOsType()
        writeFile()
        App()
        dataInit()
    }
}


private fun init(){
    CoroutineScope(Dispatchers.Default).launch {
        //初始化 adb
        AdbModule.init()
    }
}

private fun dataInit(){
    CoroutineScope(Dispatchers.Default).launch {
        delay(10)
        PropertiesUtil.all().forEach {entry->
            LocalDataKey.sList.forEach {
                if (it.first == entry.key.toString()){
                    LocalDataKey.getMap()[it.first]?.value = entry.value.toString()
                    println("key = ${it.first},value = ${entry.value}")
                }
            }
        }
        // 设置启动页
        GlobalState.sCurrentIndex.value = GlobalState.sDefaultStartIndex.value.toInt()
        AdbModule.changeAdb(GlobalState.adbSelect.value)
    }

}

/**
 * 写入必需文件
 */
private fun writeFile(){
    val adb = Pair(if (isWindows) "adb.exe" else "adb","adb")
    val properties = Pair("cfg.properties","cfg")
    val push = Pair("push.sh","shell")
    val pull = Pair("pull.sh","shell")
    val listFile = listOf(adb,properties,push,pull)
    listFile.forEach {
        val file = File(GlobalState.workDir, it.first)
        if (!file.exists()){
            file.createNewFile()
            file.setExecutable(true)
            useResource(it.second + "/" + it.first) {inputStream->
                inputStream.copyTo(file.outputStream())
            }
        }
    }
}

private fun onMinimized(isMinimized: Boolean) {

}