package com.xiaoming.utils

import com.mifmif.common.regex.Generex

/**
 * 生成对应规则文本
 */
class GenerexUtils {
    companion object {
        fun generateAll(text: String, toCn: Boolean): String {
            val text1 = text.trim().replace("\\[(\\d*|\\d+-\\d+)]".toRegex(), "【$1】")
                .replace("[", "(")
                .replace("]", ")?")
                .replace("（", "(")
                .replace("）", ")")
                .replace("【", "[")
                .replace("】", "]")
                .replace("；", "")
                .replace(";", "")
                .replace(",", "")
                .replace("，", "")
                .replace("。", "")
                .replace("\n", "|")
                .replace("\\d*\\.".toRegex(), "")
            println(text1)
            val generex = Generex(text1)
            val str = StringBuilder()
            val allMatchedStrings = generex.allMatchedStrings
            for (i in 0 until allMatchedStrings.size) {
                val s = if (toCn) NumberValueUtil.num2CNStr(allMatchedStrings[i]) else allMatchedStrings[i]
                str.append("\"" + s + "\"")
                if (i < allMatchedStrings.size - 1)
                    str.append(",")
            }
            return str.toString()
        }
    }
}