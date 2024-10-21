package com.xiaoming.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoming.config.FastBroadConfig
import com.xiaoming.config.route_left_item_color
import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.entity.InputSendData
import com.xiaoming.entity.ShellSendData
import com.xiaoming.state.GlobalState
import com.xiaoming.widget.ButtomGroupWidget
import com.xiaoming.widget.InputSendWidget
import com.xiaoming.widget.ShellSendWidget
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BroadScreen() {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(end = 2.dp), horizontalArrangement = Arrangement.End) {
            TooltipArea(tooltip = {
                val file = File(GlobalState.workDir,"cfg.json")
                Text("config path ${file.absolutePath}")
            }) {
                Icon(
                    Icons.Default.Info,
                    "tip",
                    modifier = Modifier.size(24.dp),
                    tint = route_left_item_color
                )
            }

        }

        FastBroadConfig.list.forEach {
            if (it is InputSendData) {
                InputSendWidget(it)
            } else if (it is ButtomGroupData) {
                ButtomGroupWidget(it)
            } else if (it is ShellSendData) {
                ShellSendWidget(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
