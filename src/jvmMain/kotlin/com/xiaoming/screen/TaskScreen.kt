package com.xiaoming.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import com.xiaoming.entity.Task
import com.xiaoming.state.GlobalState
import com.xiaoming.state.LocalDataKey
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.PropertiesUtil
import com.xiaoming.widget.Toast
import config.route_left_item_color
import kotlinx.coroutines.*
import theme.GOOGLE_BLUE
import theme.GOOGLE_RED

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
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_RED),
                onClick = {
                    kill(getPid(task))
                },
                modifier = Modifier.height(42.dp)
            ) {
                Text(text = "kill", color = Color.White)
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
        Button(
            onClick = {
                findTask()
            },
            modifier = Modifier.height(42.dp)
        ) {
            Text(text = "sync", color = Color.White)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = GOOGLE_RED),
            onClick = {
                stopArr()
            },
            modifier = Modifier.height(42.dp)
        ) {
            Text(text = "kill", color = Color.White)
        }
    }
}


/**
 * 获取进程
 */
fun findTask() {
    CoroutineScope(Dispatchers.Default).launch {
        // 存储关键词
        PropertiesUtil.setValue(LocalDataKey.sTaskSearchKeyWords.first,GlobalState.sTaskKeyWords.value)
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
 * 停止多个任务
 */
fun stopArr() {
    CoroutineScope(Dispatchers.Default).launch {
        val list = taskList.filter { it.checked }.map { getPid(it) }
        if (list.isEmpty()) {
            Toast.show("至少选中一个")
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
 * 获取pid
 */
fun getPid(task: Task): String {
    val contentArr = task.origin.trim().split(" ").filter {
        it.trim().isNotEmpty()
    }
    return if (contentArr.size > 2) contentArr[1] else ""
}


