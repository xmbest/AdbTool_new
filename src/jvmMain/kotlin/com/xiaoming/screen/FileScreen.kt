package com.xiaoming.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ddmlib.FileListingService.FileEntry
import com.xiaoming.state.GlobalState
import com.xiaoming.utils.*
import com.xiaoming.widget.*
import com.xiaoming.config.route_left_item_color
import kotlinx.coroutines.*
import theme.*

/**
 * 文件列表
 */
private val fileList = mutableStateListOf<FileEntry>()

/**
 * 当前文件夹路径
 */
private val currentPath = mutableStateOf("sdcard")
private val requester = FocusRequester()
private var filter = mutableStateOf("")

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
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
        Box(modifier = Modifier.fillMaxSize().onKeyEvent {
            if ((it.isCtrlPressed || it.isMetaPressed) && it.isShiftPressed && it.key.keyCode == Key.N.keyCode) {
                createDir("/" + currentPath.value)
                LogUtil.d("ctrl + shift + n")
                return@onKeyEvent true
            }
            if ((it.isCtrlPressed || it.isMetaPressed) && it.key.keyCode == Key.N.keyCode) {
                createFile("/" + currentPath.value)
                LogUtil.d("ctrl + n")
                return@onKeyEvent true
            }
            if ((it.isCtrlPressed || it.isMetaPressed) && it.key.keyCode == Key.C.keyCode) {
                LogUtil.d("ctrl + c")
                ClipboardUtils.setSysClipboardText("/" + currentPath.value)
                Toast.show("已将路径写入剪切板")
                return@onKeyEvent true
            }
            if (it.isCtrlPressed || it.isMetaPressed) {
                LogUtil.d("filter ctrl、window、command")
                return@onKeyEvent true
            }
            if (it.type == KeyEventType.KeyDown) {
                if (it.key.keyCode >= Key.A.keyCode && it.key.keyCode <= Key.Z.keyCode) {
                    filter.value += Char(it.key.nativeKeyCode).lowercase()
                } else if (it.key.keyCode == Key.Backspace.keyCode) {
                    if (filter.value.isNotBlank())
                        filter.value = filter.value.substring(0, filter.value.length - 1)
                } else if (it.key.keyCode == Key.Delete.keyCode) {
                    deleteFile("/${currentPath.value}/")
                    return@onKeyEvent true
                } else if (it.key.keyCode == Key.F5.keyCode) {
                    findFile()
                    return@onKeyEvent true
                } else if (it.key.keyCode == Key.Escape.keyCode) {
                    if (filter.value.isBlank()) {
                        LogUtil.d("backParent Esc")
                        backParent()
                        return@onKeyEvent true
                    } else {
                        filter.value = ""
                    }
                }
                findFile()
                true
            } else {
                false
            }
        }.focusRequester(requester).focusable()) {
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
            Row(
                modifier = Modifier.background(if (filter.value.isEmpty()) Color.Transparent else route_left_item_color)
                    .align(alignment = Alignment.TopStart), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = filter.value,
                    color = SIMPLE_WHITE,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 3.dp, bottom = 3.dp)
                )
            }
        }
        SideEffect {
            // 直接在重组完成后请求Box的焦点
            requester.requestFocus()
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
            LogUtil.d("findFile callback")
            fileList.clear()
            children?.filter { it.name.contains(filter.value, true) }?.forEach {
                fileList.add(it)
            }
        }
    }
}


fun findFile(time: Long) {
    CoroutineScope(Dispatchers.Default).launch {
        delay(time)
        findFile()
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
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp).background(Color.White)
            .clip(
                RoundedCornerShape(5.dp)
            ).combinedClickable(onDoubleClick = { block() }, onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
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
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp).background(Color.White)
            .clip(
                RoundedCornerShape(5.dp)
            ).combinedClickable(onDoubleClick = { block() }, onClick = {}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
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
    if (isParentTool) {
        TooltipArea(tooltip = {
            Text("Jump to the copied path")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("jump")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    var path = ClipboardUtils.getSysClipboardText() ?: ""
                    CoroutineScope(Dispatchers.Default).launch {
                        val res = AdbUtil.shell("ls $path", 50)
                        LogUtil.d("res = $res")
                        if (path.trim()
                                .isBlank() || res.contains("No such file or directory") || res.contains("syntax error")
                        ) {
                            Toast.show("无效路径")
                            return@launch
                        } else {
                            path = path.substring(0, path.lastIndexOf("/"))
                            while (path.startsWith("/")) {
                                path = path.substring(path.indexOf("/") + 1, path.length)
                                LogUtil.d(path)
                            }
                            LogUtil.d(path)
                            currentPath.value = path
                        }
                    }
                }.padding(10.dp),
                tint = GOOGLE_BLUE
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
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
            Text("new file")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("file-add")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    createFile(path)
                }.padding(10.dp),
                tint = GOOGLE_BLUE
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        TooltipArea(tooltip = {
            Text("new folder")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("folder-add")),
                null,
                modifier = Modifier.size(50.dp).clickable {
                    createDir(path)
                }.padding(10.dp),
                tint = GOOGLE_GREEN
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
                            findFile(200)
                            Toast.show("文件上传中，未及时刷新请手动刷新(F5)")
                        }
                    }
                }.padding(10.dp),
                tint = GOOGLE_YELLOW
            )
        }
    } else {
        TooltipArea(tooltip = {
            Text("rename")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("rename")),
                "icon",
                tint = GOOGLE_YELLOW,
                modifier = Modifier.size(50.dp).clickable {
                    inputText.value = name
                    InputDialog.confirm(hint = "请输入新名称", title = "重命名") {
                        if (inputText.value.isEmpty() || inputText.value == name) {
                            Toast.show("名称不可为空")
                            return@confirm
                        }
                        val filterList = fileList.filter { it.name == inputText.value }
                        if (filterList.isNotEmpty()) {
                            Toast.show("名称已存在")
                            return@confirm
                        }
                        AdbUtil.mv(path, parentPath + "/" + inputText.value)
                        showingInputDialog.value = false
                        findFile(200)
                        Toast.show("重命名中...未及时刷新请手动刷新(F5)")
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
                tint = GOOGLE_GREEN
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
                deleteFile(path + if (isParentTool) "/" else "")
                findFile(200)
            }.padding(10.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
    }
}

/**
 * 删除文件
 * @param path 需要删除的路径
 */
fun deleteFile(path: String) {
    SimpleDialog.confirm("是否删除 `${path}`") {
        AdbUtil.rf(path)
        findFile(200)
        Toast.show("文件删除中，未及时刷新请手动刷新(F5)")
    }
}


/**
 * 创建文件
 * @param path 父目录
 */
fun createFile(path: String) {
    inputText.value = ""
    InputDialog.confirm("创建新文件", hint = "请输入文件名称") {
        if (inputText.value.isNotBlank()) {
            AdbUtil.touch(path = path + "/${inputText.value}")
            showingInputDialog.value = false
            findFile(200)
            Toast.show("文件创建中，未及时刷新请手动刷新(F5)")
        } else {
            Toast.show("请输入文件名称")
        }
    }
}


/**
 * 创建文件夹
 * @param path 父目录
 */

fun createDir(path: String) {
    inputText.value = ""
    InputDialog.confirm("创建文件夹", hint = "请输入文件夹名称") {
        if (inputText.value.isNotBlank()) {
            AdbUtil.mkdir(path = path + "/${inputText.value}", 777)
            showingInputDialog.value = false
            findFile(200)
            Toast.show("文件夹创建中，未及时刷新请手动刷新(F5)")
        } else {
            Toast.show("请输入文件夹名称")
        }
    }
}


