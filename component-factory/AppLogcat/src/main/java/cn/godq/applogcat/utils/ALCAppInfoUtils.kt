package cn.godq.applogcat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import java.lang.StringBuilder


/**
 * @author  GodQ
 * @date  2023/3/3 4:17 下午
 */
fun isDebug(context: Context): Boolean {
    val info: ApplicationInfo
    try {
        info = context.applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    } catch (ignore: Exception) {
    }
    return false
}

@SuppressLint("LogNotTimber")
fun printAppInfo(context: Context) {
    val sb = StringBuilder()
    sb.append(" \n\n=================================")


    sb.append("\npackageName: ${context.packageName} \n")
    sb.append("isDebug: ${isDebug(context)} \n")
    sb.append("hasTimber: ${assembleWithTimber()} \n")


    sb.append("=================================\n\n ")
    Log.e("alc", sb.toString())

}