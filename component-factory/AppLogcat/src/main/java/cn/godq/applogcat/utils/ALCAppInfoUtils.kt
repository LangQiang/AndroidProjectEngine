package cn.godq.applogcat.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log
import cn.godq.applogcat.init.ALCConfig
import cn.godq.applogcat.mgr.AppLogcat
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * @author  GodQ
 * @date  2023/3/3 4:17 下午
 */

private var currentProcessName: String? = null


fun isDebug(context: Context): Boolean {
    val info: ApplicationInfo
    try {
        info = context.applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    } catch (ignore: Exception) {
    }
    return false
}

// 当前进程名
fun getCurrentProcessName(application: Application?): String? {
    currentProcessName?.also {
        return it
    }
    var processName = getProcessNameAboveP()
    if (!TextUtils.isEmpty(processName)) {
        return processName
    }
    processName = getProcessNameFile()
    if (!TextUtils.isEmpty(processName)) {
        return processName
    }
    processName = getProcessNameReflect(application)
    return (if (!TextUtils.isEmpty(processName)) {
        processName
    } else null).apply { currentProcessName = this }
}

// android p
private fun getProcessNameAboveP(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else null
}

// 通过文件
private fun getProcessNameFile(): String? {
    var processName: String?
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/" + Process.myPid() + "/cmdline"))
        processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            reader?.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }
    return null
}

// 通过反射
private fun getProcessNameReflect(application: Application?): String? {
    var processName: String? = null
    try {
        if (null == application) {
            return ""
        }
        val loadedApkField = application.javaClass.getField("mLoadedApk")
        loadedApkField.isAccessible = true
        val loadedApk = loadedApkField[application]
        val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
        activityThreadField.isAccessible = true
        val activityThread = activityThreadField[loadedApk]
        val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
        processName = getProcessName.invoke(activityThread) as String
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return processName
}

@SuppressLint("LogNotTimber")
fun printAppInfo(context: Context) {
    val sb = StringBuilder()
    sb.append(" \n\n=================================")


    sb.append("\npackageName: ${context.packageName} \n")
    sb.append("isDebug: ${isDebug(context)} \n")
    sb.append("isMainProcess: ${isMainProcess()} \n")
    sb.append("hasTimber: ${assembleWithTimber()} \n")
    sb.append("bootMark: ${AppLogcat.INSTANCE.thisBootMark} \n")
    sb.append("forceBootAlc: ${getMetaDataConfig(context)?.forceBootAlc} \n")


    sb.append("=================================\n\n ")
    Log.e("alc", sb.toString())

}

fun getMetaDataConfig(context: Context): ALCConfig? {
    val packageManager: PackageManager = context.packageManager
    val applicationInfo: ApplicationInfo
    try {
        applicationInfo = packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        if (applicationInfo.metaData != null) {
            return ALCConfig(
                applicationInfo.metaData.getBoolean("force_boot_alc"),
            )
        }
    } catch (e: Exception) {

    }
    return null
}