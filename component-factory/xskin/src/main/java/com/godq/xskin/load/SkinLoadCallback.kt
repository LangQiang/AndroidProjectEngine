package com.godq.xskin.load


/**
 * @author  GodQ
 * @date  2023/6/5 7:00 PM
 */
interface SkinLoadCallback {
    fun onProgress(progress: Float) {}
    fun onFinish(success: Boolean)
}