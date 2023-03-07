package cn.godq.applogcat.mgr

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import cn.godq.applogcat.ui.LogcatComponent
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.utils.proxyOtherLog
import cn.godq.applogcat.utils.runOnUiThread


/**
 * @author  GodQ
 * @date  2023/2/17 3:19 下午
 */
class AppLogcat: IAlcApi {

    companion object {
        internal val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppLogcat()
        }
        @JvmStatic
        fun getInstance(): IAlcApi = INSTANCE
    }

    private var init = false

    private var mContext: Application? = null

    private var logcatComponent: LogcatComponent? = null

    internal fun init(context: Application) {

        if (init) {
            return
        }

        init = true

        mContext = context

        logcatComponent = LogcatComponent(context)

        context.registerActivityLifecycleCallbacks(ALCActivityLifeCircleCallback())

        proxyOtherLog()
    }

    internal fun attach(activity: Activity) {
        if (logcatComponent?.isAttached() == true && logcatComponent?.currentVisibleActivity == activity) return
        logcatComponent?.currentVisibleActivity = activity
        logcatComponent?.attach()?.also {
            (it.parent as? ViewGroup)?.removeView(it)
            activity.window.addContentView(it, it.layoutParams)
        }
    }

    internal fun detach(activity: Activity) {
        if (activity != logcatComponent?.currentVisibleActivity) return
        logcatComponent?.detach()?.also {
            (it.parent as? ViewGroup)?.removeView(it)
        }
    }

    override fun log(log: String?) {
        log(log, null)
    }

    override fun log(log: String?, tag: String?) {
        log(log, null, null)
    }

    override fun log(log: String?, tag: String?, color: AlcColor?) {
        logcatComponent?: return
        runOnUiThread {
            logcatComponent?.log(log, tag, color)
        }
    }


}