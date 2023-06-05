package com.godq.xskin

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.godq.xskin.entity.SkinResourceInfo
import com.godq.xskin.entity.SkinViewWrapper
import com.godq.xskin.load.SkinLoadCallback
import com.godq.xskin.load.SkinResourceLoader
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/5/30 4:47 PM
 */
object SkinManager {

    private lateinit var mApplicationContext: Context

    private val mScope = MainScope()

    private val mSkinResourceLoader = SkinResourceLoader()

    private val mSkinInflaterFactory = SkinInflaterFactory()

    private val mSkinLifecycleListener = SkinLifecycleListener()

    private val mSkinViews = ArrayList<SkinViewWrapper>()

    private var mSkinResourceInfo: SkinResourceInfo? = null

    private var mIsDebug: Boolean? = null

    fun init(application: Application) {
        this.mApplicationContext = application.applicationContext
        this.mSkinResourceInfo = SkinResourceInfo(application.resources, application.packageName)
        this.mSkinLifecycleListener.listen(application)
    }

    //根据来源创建loader 来加载资源
    fun loadSkin(url: String, callback: SkinLoadCallback? = null) {
        mScope.launch {
            val newRes = mSkinResourceLoader.loadSkin(url) {
                callback?.onProgress(it)
            }
            callback?.onFinish(newRes != null)
            mSkinResourceInfo = newRes?: mSkinResourceInfo ?: SkinResourceInfo(mApplicationContext.resources, mApplicationContext.packageName)
            notifySkinChanged()
        }
    }

    fun reset() {
        mScope.launch {
            mSkinResourceInfo = SkinResourceInfo(mApplicationContext.resources, mApplicationContext.packageName)
            notifySkinChanged()
        }
    }

    internal fun getCurrentResourceInfo() = mSkinResourceInfo

    internal fun getSkinInflaterFactory() = mSkinInflaterFactory

    internal fun getSkinContext() = mApplicationContext


    /*****************  data  ********************/
    internal fun addSkinView(skinView: SkinViewWrapper) {
        skinView.apply()
        mSkinViews.add(skinView)
    }

    private fun notifySkinChanged() {
        Timber.tag("SkinManager").e("size: ${mSkinViews.size}")
        mSkinViews.forEach {
            it.apply()
        }

    }

    internal fun clearInvalidReference() {
        if (isDebug()) {
            Runtime.getRuntime().gc()
        }
        Timber.tag("SkinManager").e("clearInvalidReference before size: ${mSkinViews.size}")
        with(mSkinViews.iterator()) {
            while (hasNext()) {
                val next = next()
                if (next.isInvalid()) {
                    remove()
                }
            }
        }
        Timber.tag("SkinManager").e("clearInvalidReference after size: ${mSkinViews.size}")

    }
    /*****************  data  ********************/

    private fun isDebug(): Boolean {
        var isDebug = mIsDebug
        if (isDebug != null) {
            return isDebug
        }
        val info: ApplicationInfo
        try {
            info = mApplicationContext.applicationInfo
            isDebug = info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (ignore: Exception) {
        }
        mIsDebug = isDebug
        return isDebug ?: false
    }

}