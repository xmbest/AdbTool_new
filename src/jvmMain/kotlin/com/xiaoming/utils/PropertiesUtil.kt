package com.xiaoming.utils

import com.xiaoming.state.GlobalState
import java.io.*
import java.util.*

object PropertiesUtil {
    private val properties: Properties
    init {
        properties = getProperties()
    }

    /**
     * 新增/修改数据
     * @param key
     * @param value
     */
    fun setValue(key: String?, value: String?, comments: String? = "") {
        key?.let {
            properties.setProperty(key, value)
        }

        var fileOutputStream: FileOutputStream? = null
        try {
            val file = File(GlobalState.workDir, "cfg.properties")
            fileOutputStream = FileOutputStream(file)
            properties.store(fileOutputStream, comments)
        } catch (e: FileNotFoundException) {
            e.message?.let { LogUtil.e(it) }
        } catch (e: IOException) {
            e.message?.let { LogUtil.e(it) }
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                LogUtil.e("cfg.properties文件流关闭出现异常")
            }
        }
    }

    /**
     * 根据key查询value值
     * @param key key
     * @return
     */
    fun getValue(key: String?): String? {
        return properties.getProperty(key)
    }

    /**
     * 获取所有配置
     */
    fun all(): MutableSet<MutableMap.MutableEntry<Any, Any>> {
        return properties.entries
    }

    /**
     * 删除key
     */
    fun remove(key: String) {
        properties.remove(key)
        setValue(null,"")
    }


    /**
     * 获取Properties对象
     * @return
     */
    private fun getProperties(): Properties {
        val properties = Properties()
        var inputStream: InputStream? = null
        try {
            val file = File(GlobalState.workDir, "cfg.properties")
            inputStream = FileInputStream(file)
            properties.load(inputStream)
        } catch (e: FileNotFoundException) {
            LogUtil.e("cfg.properties文件未找到!")
        } catch (e: IOException) {
            println("出现IOException")
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                LogUtil.e("cfg.properties文件流关闭出现异常")
            }
        }
        return properties
    }
}