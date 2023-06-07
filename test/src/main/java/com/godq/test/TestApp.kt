package com.godq.test

import android.app.Application
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/7 4:02 PM
 */
class TestApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this)
    }
}