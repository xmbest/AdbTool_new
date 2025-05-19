package com.xiaoming.router

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.xiaoming.config.route_left_background
import com.xiaoming.config.route_left_item_background
import com.xiaoming.config.route_left_item_clicked_color
import com.xiaoming.config.route_left_item_color
import com.xiaoming.config.route_left_item_height
import com.xiaoming.config.route_left_item_rounded
import com.xiaoming.config.route_left_item_spacer
import com.xiaoming.config.route_left_padding_bottom
import com.xiaoming.config.route_left_padding_left
import com.xiaoming.config.route_left_padding_right
import com.xiaoming.config.route_left_padding_top
import com.xiaoming.config.route_left_width
import com.xiaoming.config.route_right_background
import com.xiaoming.module.AdbModule
import com.xiaoming.state.GlobalState
import com.xiaoming.utils.AdbUtil
import com.xiaoming.utils.ImgUtil.getRealLocation
import com.xiaoming.widget.InputDialog
import com.xiaoming.widget.SimpleDialog
import com.xiaoming.widget.Toast
import com.xiaoming.widget.inputCallback
import com.xiaoming.widget.inputHintText
import com.xiaoming.widget.inputTitleColor
import com.xiaoming.widget.inputTitleText
import com.xiaoming.widget.showingInputDialog
import com.xiaoming.widget.showingSimpleDialog
import com.xiaoming.widget.simpleCallback
import com.xiaoming.widget.simpleContentText
import com.xiaoming.widget.simpleNeedCancel
import com.xiaoming.widget.simpleTitle
import com.xiaoming.widget.simpleTitleColor

@Composable
fun Router() {
    Row(modifier = Modifier.fillMaxSize()) {
        Left(
            Modifier.fillMaxHeight().width(route_left_width).background(route_left_background)
                .padding(
                    start = route_left_padding_left,
                    end = route_left_padding_right,
                    top = route_left_padding_top,
                    bottom = route_left_padding_bottom
                )
        )
        Right(Modifier.fillMaxHeight().weight(1f).background(route_right_background))
    }
    //显示
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "1.0.2 xmbest@github",
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp).clickable {
                Toast.show("hello world!")
            })
        Toast()
        if (showingSimpleDialog.value) {
            SimpleDialog(
                title = simpleTitle.value,
                titleColor = simpleTitleColor.value,
                contentText = simpleContentText.value,
                needCancel = simpleNeedCancel.value,
                callback = simpleCallback.value
            )
        }

        if (showingInputDialog.value) {
            InputDialog(
                title = inputTitleText.value,
                titleColor = inputTitleColor.value,
                hint = inputHintText.value,
                callback = inputCallback.value,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Left(modifier: Modifier) {
    Column(
        modifier
    ) {
        GlobalState.pages.forEachIndexed { index, page ->
            Spacer(modifier = Modifier.height(route_left_item_spacer))
            ListItem(
                text = {
                    Text(
                        page.name,
                        color = if (GlobalState.sCurrentIndex.value == index) route_left_item_clicked_color else route_left_item_color
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(page.icon),
                        page.name,
                        tint = if (GlobalState.sCurrentIndex.value != index) page.color else Color.White
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(route_left_item_rounded))
                    .height(route_left_item_height)
                    .clickable {
                        GlobalState.sCurrentIndex.value = index
                    }
                    .background(if (GlobalState.sCurrentIndex.value == index) route_left_item_background else route_left_background)
            )
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            ListItem(
                text = {
                    Text(
                        GlobalState.sCurrentDevice.value?.serialNumber ?: "请选择设备",
                        color = route_left_item_color,
                        maxLines = 2
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(getRealLocation("mobile")),
                        null,
                        tint = route_left_item_color,
                        modifier = Modifier.clickable {
                            AdbUtil.devices()
                        }
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(route_left_item_rounded))
                    .height(route_left_item_height)
                    .clickable {
                        GlobalState.sExpanded.value = true
                    }
            )
        }
        DropdownMenu(
            expanded = GlobalState.sExpanded.value,
            onDismissRequest = {
                GlobalState.sExpanded.value = false
            },
            offset = DpOffset(x = 2.dp, y = 2.dp),
            modifier = Modifier.width(route_left_width - route_left_padding_left - route_left_padding_right)
        ) {
            if (GlobalState.sDeviceSet.size == 0) {
                DropdownMenuItem(onClick = {

                }) {
                    Text(text = "当前设备列表为空")
                }
            } else {
                GlobalState.sDeviceSet.forEach {
                    DropdownMenuItem(onClick = {
                        GlobalState.sExpanded.value = false
                        AdbModule.changeDevice(it)
                    }) {
                        Text(text = it.serialNumber)
                    }
                }
            }
        }
    }
}


@Composable
fun Right(modifier: Modifier) {
    Column(modifier) {
        GlobalState.pages[GlobalState.sCurrentIndex.value].comp()
    }
}