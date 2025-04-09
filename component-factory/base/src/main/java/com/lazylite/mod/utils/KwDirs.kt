package com.lazylite.mod.utils

import android.os.Environment
import com.lazylite.mod.App
import java.io.File

object KwDirs {

    // 目录类型常量
    const val HOME = 0
    const val CACHE = 1
    const val CRASH = 2
    const val FRESCO = 3
    const val SETTING = 4

    private var APP_DIR_NAME = "APP"

    // 外部存储根目录路径
    private var SD_ROOTPATH = getExternalStorageDirectory()

    // 应用可见的根目录路径
    private var USER_VISIBLE_ROOT_PATH = "${SD_ROOTPATH}${APP_DIR_NAME}${File.separator}"

    // 主目录路径
    private var HOME_PATH = "${SD_ROOTPATH}${APP_DIR_NAME}${File.separator}"

    // 隐藏目录路径（以.开头）
    private var HOME_PATH_FOR_HIDE = "${HOME_PATH}."

    /**
     * 初始化工具类，设置应用目录名称
     * @param appDirName 应用目录名称
     */
    @JvmStatic
    fun init(appDirName: String) {
        if (appDirName.isNotEmpty()) {
            APP_DIR_NAME = appDirName
            // 更新相关路径
            USER_VISIBLE_ROOT_PATH = "${SD_ROOTPATH}${APP_DIR_NAME}${File.separator}"
            HOME_PATH = "${SD_ROOTPATH}${APP_DIR_NAME}${File.separator}"
            HOME_PATH_FOR_HIDE = "${HOME_PATH}."
        }
    }

    /**
     * 获取指定类型的目录路径
     * @param dirType 目录类型，使用常量定义
     * @return 返回目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getDir(dirType: Int): String {
        val dirPath = when (dirType) {
            HOME -> HOME_PATH
            CACHE -> "${HOME_PATH_FOR_HIDE}data"
            CRASH -> "${HOME_PATH_FOR_HIDE}crash"
            FRESCO -> "${HOME_PATH_FOR_HIDE}fresco"
            SETTING -> "${HOME_PATH_FOR_HIDE}setting"
            else -> HOME_PATH
        }

        // 确保目录存在
        val dir = File(dirPath)
        if (!dir.exists()) {
            try {
                dir.mkdirs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 确保路径末尾有分隔符
        return if (dirPath.endsWith(File.separator)) dirPath else "$dirPath${File.separator}"
    }

    /**
     * 获取home目录路径
     * @return 返回home目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getHomeDir(): String {
        return getDir(HOME)
    }

    /**
     * 获取缓存目录路径
     * @return 返回缓存目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getCacheDir(): String {
        return getDir(CACHE)
    }

    /**
     * 获取自动下载缓存目录路径
     * @return 返回自动下载缓存目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getCrashDir(): String {
        return getDir(CRASH)
    }

    /**
     * 获取Fresco缓存目录路径
     * @return 返回Fresco缓存目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getFrescoDir(): String {
        return getDir(FRESCO)
    }

    /**
     * 获取设置目录路径
     * @return 返回设置目录路径，路径末尾已包含"/"
     */
    @JvmStatic
    fun getSettingDir(): String {
        return getDir(SETTING)
    }

    /**
     * 获取应用目录名称
     * @return 应用目录名称
     */
    @JvmStatic
    fun getAppDirName(): String {
        return APP_DIR_NAME
    }

    /**
     * 获取外部存储目录
     * @return 返回外部存储目录路径，路径末尾已包含"/"
     */
    private fun getExternalStorageDirectory(): String {
        try {
            return "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}"
        } catch (e: SecurityException) {
            // 如果没有存储权限，则使用应用内部存储
            return "${getAppRootPath().absolutePath}${File.separator}"
        }
    }

    /**
     * 获取应用根目录
     * @return 应用根目录File对象
     */
    private fun getAppRootPath(): File {
        val file = App.getInstance().getExternalFilesDir(null)
        return file ?: App.getInstance().filesDir
    }

}