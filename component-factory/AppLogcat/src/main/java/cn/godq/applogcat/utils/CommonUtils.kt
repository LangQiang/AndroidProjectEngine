package cn.godq.applogcat.utils

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import cn.godq.applogcat.mgr.AppLogcat


/**
 * @author  GodQ
 * @date  2023/3/3 5:03 下午
 */

private val handle = Handler(Looper.getMainLooper())

fun runOnUiThread(delay: Int = 0, runnable: (() -> Unit)) {
    if (delay <= 0) {
        handle.post {
            runnable()
        }
    } else {
        handle.postDelayed(runnable, delay.toLong())
    }
}

fun runOnUiThread(delay: Int, runnable: Runnable) {
    runOnUiThread(delay) {
        runnable.run()
    }
}

fun isTouchInView(view: View?, event: MotionEvent): Boolean {
    if (view == null) {
        return false
    }
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = location[0]
    val y = location[1]
    return x < event.rawX && event.rawX < x + view.width && y < event.rawY && event.rawY < y + view.height
}

fun isMainThread(): Boolean {
    return Thread.currentThread() == Looper.getMainLooper().thread
}

fun isMainProcess(): Boolean {
    val context = AppLogcat.INSTANCE.mContext ?: return true
    return context.packageName == getCurrentProcessName(context)
}
