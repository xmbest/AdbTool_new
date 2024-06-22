package com.xiaoming.entity

data class Task(
    val user: String = "",
    val pid: String = "",
    val tid: String = "",
    val time: String = "",
    val name: String = "",
    var checked: Boolean = false,
    val origin: String
)
