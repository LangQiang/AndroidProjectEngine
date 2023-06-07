package com.godq.xskin

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/6/5 2:36 PM
 *
 * 监听view回收，负责清理
 */
class SkinLifecycleListener {

    private var isListening = false

    private val mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {

        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
            super.onActivityPreCreated(activity, savedInstanceState)
            LayoutInflaterCompat.setFactory2(activity.layoutInflater, SkinManager.getSkinInflaterFactory())
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Timber.tag("SkinManager").d("ActivityLifecycleCallback ${activity.javaClass.name}: onActivityCreated")

            (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(object : FragmentLifecycleCallbacks(){
                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    Timber.tag("SkinManager").d("FragmentLifecycleCallback ${f.javaClass.name}: onFragmentDestroyed")
                    SkinManager.clearInvalidReference()
                }
            }, false)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            Timber.tag("SkinManager").d("ActivityLifecycleCallbacks ${activity.javaClass.name}: onActivityDestroyed")
            SkinManager.clearInvalidReference()
        }
    }

    fun listen(application: Application) {
        if (isListening) return
        isListening = true
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

}