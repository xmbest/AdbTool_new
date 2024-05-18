package com.xiaoming.utils

object ImgUtil {

    /**
     * 获取真实路径
     * @param name 路径下名称
     */
    fun getRealLocation(name: String): String {
        return "img/${name}.png"
    }


    /**
     * 获取 logo
     */
    private fun getLogoRealLocation(name: String): String {
        return "logo/${name}.png"
    }


    /**
     * 根据厂商名称获取 logo
     * @param name 厂商名称
     */
    fun getLogoByBrand(name: String): String {
        return if (name.contentEquals("xiaomi", true)) getLogoRealLocation("xiaomi")
        else if (name.contentEquals("huawei", true)) getLogoRealLocation("huawei")
        else if (name.contentEquals("oppo", true)) getLogoRealLocation("oppo")
        else if (name.contentEquals("vivo", true)) getLogoRealLocation("vivo")
        else if (name.contentEquals("google", true)) getLogoRealLocation("google")
        else if (name.contentEquals("samsung", true)) getLogoRealLocation("samsung")
        else getLogoRealLocation("android")
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
        else if (fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".gz") || fileName.endsWith(
                ".7z"
            )
        ) getRealLocation(
            "file-zip"
        )
        else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav") || fileName.endsWith(".rf")) getRealLocation(
            "music"
        )
        else getRealLocation("file")
    }
}