package com.xiaoming.screen

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
import com.android.ddmlib.FileListingService.FileEntry
import com.xiaoming.state.GlobalState
import com.xiaoming.utils.*
import com.xiaoming.widget.InputDialog
import com.xiaoming.widget.SimpleDialog
import com.xiaoming.widget.inputText
import com.xiaoming.widget.showingInputDialog
import kotlinx.coroutines.*
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
    if (GlobalState.sCurrentDevice.value == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("请先连接设备")
        }
    } else {
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
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, start = 10.dp).background(Color.White)
            .clip(
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
        FileTool(path = "/" + currentPath.value, isParentTool = true)
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
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, start = 10.dp).background(Color.White)
            .clip(
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
                Text(file.date + " " + file.time, fontSize = 12.sp, color = SIMPLE_GRAY)
                Text(
                    FileUtil.byte2Gb(file.size),
                    fontSize = 12.sp,
                    color = SIMPLE_GRAY,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
        FileTool(file.fullPath, parentPath = file.parent.fullPath, name = file.name, isParentTool = false)
    }
}


/**
 * 文件工具栏
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileTool(path: String, parentPath: String? = "", name: String = "", isParentTool: Boolean = false) {
    TooltipArea(tooltip = {
        Text("copy absolutePath")
    }) {
        Icon(
            painter = painterResource(ImgUtil.getRealLocation("copy")),
            "icon",
            tint = GOOGLE_BLUE,
            modifier = Modifier.size(50.dp).clickable {
                ClipboardUtils.setSysClipboardText(path)
            }.padding(10.dp)
        )
    }

    if (isParentTool) {
        Spacer(modifier = Modifier.width(10.dp))
        TooltipArea(tooltip = {
            Text("refresh")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("reload")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    findFile()
                }.padding(10.dp),
                tint = GOOGLE_BLUE
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        TooltipArea(tooltip = {
            Text("push file")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("upload")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    runBlocking {
                        val selectDir = PathSelectorUtil.selectFileOrDir("请选择需要上传的文件、目录")
                        if (selectDir.isNotEmpty()) {
                            AdbUtil.push(selectDir, path)
                        }
                    }
                }.padding(10.dp),
                tint = GOOGLE_BLUE
            )
        }
    } else {
        TooltipArea(tooltip = {
            Text("rename")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("rename")),
                "icon",
                tint = GOOGLE_BLUE,
                modifier = Modifier.size(50.dp).clickable {
                    inputText.value = name
                    InputDialog.confirm(hint = "请输入新文件名称", title = "重命名") {
                        if (inputText.value.isEmpty() || inputText.value == name) {
                            // 不可为空
                            return@confirm
                        }
                        val filterList = fileList.filter { it.name == inputText.value }
                        if (filterList.isNotEmpty()) {
                            // 重复
                            return@confirm
                        }
                        AdbUtil.mv(path, parentPath + "/" + inputText.value)
                        showingInputDialog.value = false
                    }
                }.padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        TooltipArea(tooltip = {
            Text("pull file")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("download")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    runBlocking {
                        if (path.isBlank()) return@runBlocking
                        val selectDir = PathSelectorUtil.selectDir("请选择存储目录")
                        if (selectDir.isNotEmpty()) {
                            AdbUtil.pull(path, selectDir)
                        }
                    }
                }.padding(10.dp),
                tint = GOOGLE_BLUE
            )
        }
    }

    TooltipArea(tooltip = {
        Text("delete")
    }) {
        Icon(
            painter = painterResource(ImgUtil.getRealLocation("delete")),
            "icon",
            tint = GOOGLE_RED,
            modifier = Modifier.size(50.dp).clickable {
                SimpleDialog.confirm("是否删除 `${path}`") {
                    AdbUtil.rf(path)
                }
            }.padding(10.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
    }
}


