package cn.godq.applogcat.mgr

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import cn.godq.applogcat.repo.LogcatRepository
import cn.godq.applogcat.ui.LogcatComponent
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.ui.content.recycler.ContentRecyclerViewCtrl
import cn.godq.applogcat.utils.buildLogcatEntity
import cn.godq.applogcat.utils.proxyOtherLog
import cn.godq.applogcat.utils.runOnUiThread
import kotlinx.coroutines.*
import java.util.*


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

    internal var mContext: Application? = null

    internal val thisBootMark = UUID.randomUUID().toString()

    private var logcatComponent: LogcatComponent? = null

    internal fun init(context: Application) {

        if (init) {
            return
        }

        init = true

        mContext = context

        logcatComponent = LogcatComponent(context, ContentRecyclerViewCtrl())

        context.registerActivityLifecycleCallbacks(ALCActivityLifeCircleCallback())

        proxyOtherLog()

        MainScope().launch(Dispatchers.IO) {
            LogcatRepository.clearDiskLogData(System.currentTimeMillis() - 1000 * 30)
        }
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
        log(log, tag, null)
    }

    override fun log(log: String?, tag: String?, color: AlcColor?) {
        log(log, tag, color, LogcatEntity.getDefaultOptFlag())
    }

    override fun log(log: String?, tag: String?, color: AlcColor?, flag: Long) {
        if (!init) return
        logcatComponent?: return
        val entity = buildLogcatEntity(log, tag, color, flag) ?: return
        runOnUiThread {
            logcatComponent?.log(entity)
        }
    }
}