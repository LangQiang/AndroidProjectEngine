package com.godq.androidprojectengine

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import timber.log.Timber

@DeepLink(path = "/test")
class TestRouter: AbsRouter() {
    override fun parse(uri: Uri?) {

    }

    override fun route(): Boolean {
        Timber.tag("router").e("TestRouter invoke")
        return true
    }
}