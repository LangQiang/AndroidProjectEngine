package com.godq.androidprojectengine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lazylite.mod.global.CommonInit
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CommonInit.initOnAppCreate(this.applicationContext)
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://kuwo.cn"), null)
    }
}