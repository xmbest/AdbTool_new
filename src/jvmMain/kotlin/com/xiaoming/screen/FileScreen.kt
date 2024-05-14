package com.xiaming.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ddmlib.FileListingService
import com.android.ddmlib.FileListingService.FileEntry
import com.xiaming.utils.ImgUtil
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.FileUtil
import org.slf4j.LoggerFactory
import theme.*

private val log = LoggerFactory.getLogger("FileScreen")

/**
 * 文件列表
 */
private val fileList = mutableStateListOf<FileEntry>()

/**
 * 当前文件夹路径
 */
private val currentPath = mutableStateOf("sdcard")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileScreen() {
    LaunchedEffect(currentPath.value) {
        findFile()
    }

    LazyColumn {
        stickyHeader {
            Row(modifier = Modifier.background(Color.White)) {
                FileNav()
            }
        }
        items(fileList) {
            FileView(it)
        }
    }

}

/**
 * 切换视图
 * @param path 需要展示的路径
 */
fun findFile() {
    AdbUtil.findFileList(currentPath.value) { entry, children ->
        {
            fileList.clear()
            children?.forEach {
                log.debug("${it.fullPath}")
                fileList.add(it)
            }
        }
    }
}

/**
 * 返回父目录
 */
fun backParent() {
    if (currentPath.value.isEmpty()) return
    if (currentPath.value.contains("/")) {
        val path = currentPath.value.substring(0, currentPath.value.lastIndexOf("/"))
        changePath(path)
    } else {
        changePath("")
    }
}

/**
 * 切换文件路径去除“/”前缀
 * @param path 文件路径
 */
fun changePath(path: String) {
    var temp = path
    while (temp.startsWith("/")) {
        temp = temp.removePrefix("/")
    }
    log.debug("temp = $temp")
    currentPath.value = temp
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileNav(
    block: () -> Unit = {
        backParent()
    }
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, start = 10.dp).background(Color.White).clip(
            RoundedCornerShape(5.dp)
        ).combinedClickable(onDoubleClick = { block() }, onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(ImgUtil.getFileIcon(currentPath.value, true)),
            "icon",
            tint = GOOGLE_BLUE,
            modifier = Modifier.size(36.dp)
        )
        Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
            Row {
                SelectionContainer {
                    Text(
                        buildAnnotatedString {
                            append("/" + currentPath.value)
                        }
                    )
                }
            }
            Row {
                Text("back", fontSize = 12.sp, color = SIMPLE_GRAY)
            }
        }
    }
}


/**
 * 文件显示
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileView(
    file: FileEntry, block: () -> Unit = {
        if (file.isDirectory)
            changePath(file.fullPath)
    }
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, start = 10.dp).background(Color.White).clip(
            RoundedCornerShape(5.dp)
        ).combinedClickable(onDoubleClick = { block() }, onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(ImgUtil.getFileIcon(file.name, file.isDirectory)),
            "icon",
            tint = if (file.isDirectory) GOOGLE_BLUE else SIMPLE_GRAY,
            modifier = Modifier.size(36.dp)
        )
        Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
            Row {
                SelectionContainer {
                    Text(
                        buildAnnotatedString {
                            append(file.name + " ")
                            withStyle(style = SpanStyle(SIMPLE_GRAY)) {
                                append(file.permissions)
                            }
                        }
                    )
                }
            }
            Row {
                Text(file.date, fontSize = 12.sp, color = SIMPLE_GRAY)
                Text(
                    FileUtil.byte2Gb(file.sizeValue),
                    fontSize = 12.sp,
                    color = SIMPLE_GRAY,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }
}

