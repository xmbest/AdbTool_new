package com.xiaming.utils

object ImgUtil {

    /**
     * 获取真实路径
     * @param name 路径下名称
     */
    fun getRealLocation(name:String):String{
        return "img/${name}.png"
    }


    /**
     * 获取文件类型图标
     * @param fileName 文件名称
     * @param isDir 是否文件夹
     */
    fun getFileIcon(fileName: String, isDir: Boolean): String {
        return if (isDir) getRealLocation("folder")
        else if (fileName.endsWith(".apk")) getRealLocation("android")
        else if (fileName.endsWith(".jar")) getRealLocation("java")
        else if (fileName.endsWith(".json")) getRealLocation("json")
        else if (fileName.endsWith(".so")) getRealLocation("dependency")
        else if (fileName.endsWith(".cfg") || fileName.endsWith(".conf")) getRealLocation("settings")
        else if (fileName.endsWith(".txt") || fileName.endsWith(".xml")) getRealLocation("file-text")
        else if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) getRealLocation("file-image")
        else if (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".gz") || fileName.endsWith(".7z")) getRealLocation(
            "file-zip"
        )
        else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav") || fileName.endsWith(".rf")) getRealLocation(
            "music"
        )
        else getRealLocation("file")
    }
}