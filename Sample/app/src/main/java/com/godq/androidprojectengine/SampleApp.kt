package com.godq.androidprojectengine

import android.app.Application
import com.lazylite.bridge.init.ComponentInit
import com.lazylite.mod.global.BaseConfig
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.utils.KwLifecycleCallback


/**
 * @author  GodQ
 * @date  2023/1/30 6:12 下午
 */
class SampleApp: Application() {


    companion object {
        private var app: SampleApp? = null
        fun getInstance(): SampleApp? {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        val baseConfig = BaseConfig()
        baseConfig.allowProxy = true
        baseConfig.deepLinkScheme = "sample"
        ComponentInit.initOnAppCreate(this.applicationContext, baseConfig)
        registerActivityLifecycleCallbacks(KwLifecycleCallback.getInstance())
    }
}