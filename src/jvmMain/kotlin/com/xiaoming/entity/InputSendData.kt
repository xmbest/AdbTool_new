package com.xiaoming.entity

import com.xiaoming.config.FastBroadType

/**
 * @param title 标题
 * @param cmd 命令
 * @param template 命令中需要替换的文本模板
 * @param btnText 按钮文本，默认 Send
 * @param hint 输入框提示文本
 */
data class InputSendData(
    val title: String,
    val cmd: String,
    val template: String,
    val btnText: String = "Send",
    val hint: String = ""
) : BaseFastBroadData(FastBroadType.INPUT_SEND)
