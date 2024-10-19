package com.xiaoming.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoming.config.FastBroadConfig
import com.xiaoming.config.route_left_item_color
import com.xiaoming.entity.ButtomData
import com.xiaoming.entity.ButtomGroupData
import com.xiaoming.entity.InputSendData
import com.xiaoming.entity.ShellSendData
import com.xiaoming.state.GlobalState
import com.xiaoming.widget.ButtomGroupWidget
import com.xiaoming.widget.InputSendWidget
import com.xiaoming.widget.ShellSendWidget

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
                Text("config path ${GlobalState.workDir}\\cfg.json")
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
