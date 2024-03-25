import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.xiaming.config.window_height
import com.xiaming.config.window_width
import com.xiaming.module.AdbModule
import com.xiaming.utils.ImgUtil
import com.xiaoming.router.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import theme.GOOGLE_BLUE
import java.awt.Dimension
import java.awt.Toolkit

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
        title = "AdbTool",
        state = state,
        icon = painterResource(ImgUtil.getRealLocation("ic_logo"))
    ) {
        App()
        LaunchedEffect(state) {
            snapshotFlow { state.isMinimized }
                .onEach(::onMinimized).launchIn(this)
        }
        init()
    }
}


private fun init(){
    CoroutineScope(Dispatchers.Default).launch {
        //初始化 adb
        AdbModule.init()
    }
}

private fun onMinimized(isMinimized: Boolean) {

}