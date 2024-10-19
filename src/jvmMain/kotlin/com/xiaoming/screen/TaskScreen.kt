package com.xiaoming.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.Task
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import com.xiaoming.utils.*
import com.xiaoming.widget.Toast
import com.xiaoming.config.route_left_item_color
import kotlinx.coroutines.*
import com.xiaoming.theme.GOOGLE_BLUE
import com.xiaoming.theme.GOOGLE_GREEN
import com.xiaoming.theme.GOOGLE_RED
import com.xiaoming.theme.GOOGLE_YELLOW

val checkA = mutableStateOf(true)
val taskList = mutableStateListOf<Task>()
val taskTitle = mutableStateOf(Task(origin = "USER           PID  PPID     VSZ    RSS WCHAN            ADDR S NAME"))

/**
 * 进程管理
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskScreen() {
    if (GlobalState.sCurrentDevice.value == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("请先连接设备")
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                stickyHeader {
                    Column(modifier = Modifier.background(Color.White)) {
                        Row {
                            TaskNav()
                        }
                        Row {
                            TaskItem(
                                taskTitle.value,
                                -1,
                                false
                            ) { checked ->
                                taskTitle.value = taskTitle.value.copy(checked = checked)
                                val list: ArrayList<Task> = ArrayList()
                                taskList.forEach { item ->
                                    val copyItem = item.copy(checked = checked)
                                    list.add(copyItem)
                                }
                                taskList.clear()
                                taskList.addAll(list)
                            }
                        }
                    }

                }
                itemsIndexed(taskList) { index: Int, item: Task ->
                    TaskItem(item, index)
                }
            }
        }
        LaunchedEffect(checkA.value) {
            findTask()
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(task: Task, index: Int, needBtn: Boolean = true, checked: ((Boolean) -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, end = 10.dp).background(Color.White)
            .clip(
                RoundedCornerShape(5.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Checkbox(
            task.checked,
            onCheckedChange = {
                if (index > -1)
                    taskList[index] = task.copy(checked = it)
                checked?.invoke(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = GOOGLE_BLUE)
        )
        Row(modifier = Modifier.weight(1f)) {
            SelectionContainer {
                Text(task.origin)
            }
        }

        if (needBtn) {
            TooltipArea(tooltip = {
                Text("kill process")
            }) {
                Icon(
                    painter = painterResource(ImgUtil.getRealLocation("kill")),
                    "icon",
                    tint = GOOGLE_YELLOW,
                    modifier = Modifier.size(42.dp).clickable {
                        kill(getPid(task))
                    }.padding(4.dp)
                )
            }

            TooltipArea(tooltip = {
                Text("force stop process")
            }) {
                Icon(
                    painter = painterResource(ImgUtil.getRealLocation("stop")),
                    "icon",
                    tint = GOOGLE_RED,
                    modifier = Modifier.size(42.dp).clickable {
                        forceStop(getName(task))
                    }.padding(0.dp)
                )
            }

        }

    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TaskNav() {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 5.dp, end = 10.dp).background(Color.White)
            .clip(
                RoundedCornerShape(5.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "-A",
            color = route_left_item_color,
            modifier = Modifier.align(Alignment.CenterVertically).clickable {
                checkA.value = !checkA.value
            })
        Checkbox(
            checkA.value,
            onCheckedChange = {
                checkA.value = it
            },
            colors = CheckboxDefaults.colors(checkedColor = GOOGLE_BLUE)
        )
        TextField(
            GlobalState.sTaskKeyWords.value,
            trailingIcon = {
                if (GlobalState.sTaskKeyWords.value.isNotBlank()) Icon(
                    Icons.Default.Close,
                    null,
                    modifier = Modifier.width(20.dp).height(20.dp).clickable {
                        GlobalState.sTaskKeyWords.value = ""
                    },
                    tint = route_left_item_color
                )
            },
            placeholder = { Text("keyword") },
            onValueChange = { GlobalState.sTaskKeyWords.value = it },
            singleLine = true,
            modifier = Modifier.weight(1f).height(52.dp).padding(end = 10.dp).onKeyEvent {
                if (it.key.keyCode == Key.Enter.keyCode) {
                    findTask()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
        TooltipArea(tooltip = {
            Text("refresh")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("reload")),
                "icon",
                tint = GOOGLE_GREEN,
                modifier = Modifier.size(50.dp).clickable {
                    findTask()
                }.padding(8.dp)
            )
        }

        TooltipArea(tooltip = {
            Text("kill process")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("kill")),
                "icon",
                tint = GOOGLE_YELLOW,
                modifier = Modifier.size(50.dp).clickable {
                    stopArrByPid()
                }.padding(8.dp)
            )
        }

        TooltipArea(tooltip = {
            Text("force stop process")
        }) {
            Icon(
                painter = painterResource(ImgUtil.getRealLocation("stop")),
                "icon",
                tint = GOOGLE_RED,
                modifier = Modifier.size(50.dp).clickable {
                    stopArrByName()
                }.padding(4.dp)
            )
        }


    }
}


/**
 * 获取进程
 */
fun findTask() {
    CoroutineScope(Dispatchers.Default).launch {
        // 存储关键词
        PropertiesUtil.setValue(LocalDataKey.sTaskSearchKeyWords.first, GlobalState.sTaskKeyWords.value)
        AdbUtil.findProcessByKeyword(GlobalState.sTaskKeyWords.value, checkA.value) { list ->
            // 搜索归位
            taskTitle.value = taskTitle.value.copy(checked = false)
            taskList.clear()
            list.forEach { item ->
                taskList.add(Task(origin = item))
            }
        }
    }
}

/**
 * 停止多个任务pid
 */
fun stopArrByPid() {
    CoroutineScope(Dispatchers.Default).launch {
        val list = taskList.filter { it.checked }.map { getPid(it) }
        if (list.isEmpty()) {
            Toast.show("Please select at least one.")
        } else {
            runBlocking {
                kill(list.joinToString(" "))
                delay(100)
                findTask()
            }
        }
    }

}

/**
 * 停止多个任务packagename
 */
fun stopArrByName() {
    CoroutineScope(Dispatchers.Default).launch {
        val list = taskList.filter { it.checked }.map { getName(it) }
        if (list.isEmpty()) {
            Toast.show("Please select at least one.")
        } else {
            list.forEach {
                forceStop(it)
            }
            delay(100)
            findTask()
        }
    }
}


/**
 * kill指定进程
 */
fun kill(pids: String) {
    runBlocking {
        AdbUtil.shell("kill $pids")
        delay(100)
        findTask()
    }
}


/**
 * 根据 packageName 强制停止进程
 */
fun forceStop(packageName: String) {
    runBlocking {
        AdbUtil.forceStop(packageName)
    }
}


/**
 * 获取pid
 */
fun getPid(task: Task): String {
    val contentArr = task.origin.trim().split(" ").filter {
        it.trim().isNotEmpty()
    }
    return if (contentArr.size > 1) contentArr[1] else ""
}

fun getName(task: Task): String {
    val contentArr = task.origin.trim().split(" ").filter {
        it.trim().isNotEmpty()
    }
    LogUtil.d("getName contentArr = $contentArr")
    return if (contentArr.size > 8) contentArr[8] else ""
}


