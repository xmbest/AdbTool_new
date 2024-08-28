package com.xiaoming.utils

/**
 * 是否 MAC
 */
var isMac = false

/**
 * 是否 Windows
 */
var isWindows = false

/**
 * 是否 Linux
 */
var isLinux = false

/**
 * Other
 */
var isOther = false


/**
 * 初始化系统类别
 */
fun initOsType(){
    val type = System.getProperty("os.name").uppercase()
    return with(type){
        when {
            contains("WIN") -> isWindows = true
            contains("MAC") -> isMac = true
            contains("LIN") -> isLinux = true
            else -> isOther = true
        }
    }
}

/**
 * 获取 adb
 */
fun getAdb() = if (isWindows) "adb.exe" else "adb"
