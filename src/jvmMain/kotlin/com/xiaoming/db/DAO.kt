package com.xiaoming.db

interface DAO {
    suspend fun getString(key: String, default: String = ""): String
    suspend fun putString(key: String, value: String)
    suspend fun updateString(key: String, value: String)
    suspend fun findAll(): List<KeyValue>
}