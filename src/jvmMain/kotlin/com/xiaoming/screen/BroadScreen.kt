package com.xiaoming.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoming.config.route_left_item_color

@Composable
fun BroadScreen() {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(end = 2.dp),horizontalArrangement = Arrangement.End) {
            Icon(
                Icons.Default.Edit,
                "编辑",
                modifier = Modifier.size(24.dp).clickable {
                    quickSettingKeyword.value = ""
                },
                tint = route_left_item_color
            )
        }
    }

}