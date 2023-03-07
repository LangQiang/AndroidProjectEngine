package cn.godq.applogcat.utils

import android.os.Handler
import android.os.Looper


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