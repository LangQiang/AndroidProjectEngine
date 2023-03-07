package cn.godq.applogcat.mgr

import android.app.Activity
import android.app.Application
import android.os.Bundle


/**
 * @author  GodQ
 * @date  2023/3/3 3:55 下午
 */
class ALCActivityLifeCircleCallback: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        AppLogcat.INSTANCE.attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        AppLogcat.INSTANCE.detach(activity)
    }
}