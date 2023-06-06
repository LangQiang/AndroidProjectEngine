package com.godq.xskin

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.godq.xskin.entity.SkinResourceInfo
import com.godq.xskin.entity.SkinViewWrapper
import com.godq.xskin.inject.DownloadInjectImpl
import com.godq.xskin.inject.IDownloadInject
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

    private lateinit var mDownloadInject: IDownloadInject

    private val mScope = MainScope()

    private val mSkinResourceLoader = SkinResourceLoader()

    private val mSkinInflaterFactory = SkinInflaterFactory()

    private val mSkinLifecycleListener = SkinLifecycleListener()

    private val mSkinViews = ArrayList<SkinViewWrapper>()

    private var mSkinResourceInfo: SkinResourceInfo? = null

    private var mIsDebug: Boolean? = null

    fun init(application: Application, httpInject: IDownloadInject? = null) {
        this.mApplicationContext = application.applicationContext
        this.mDownloadInject = httpInject?: DownloadInjectImpl()
        this.mSkinResourceInfo = SkinResourceInfo(application.resources, application.packageName)
        this.mSkinResourceLoader.downloadInject = this.mDownloadInject
        this.mSkinLifecycleListener.listen(application)
    }

    /**
     * 同一个资源不会重复下载，加载资源可以重复调用此方法，内部做了缓存处理
     * @param autoApply true:自动切换资源 false:只下载不应用
     * */
    fun loadSkin(url: String, autoApply: Boolean = true, callback: SkinLoadCallback? = null) {
        takeIf { !this::mApplicationContext.isInitialized }?.apply {
            Timber.tag("SkinManager").e("XSkin is not Initialized")
            callback?.onFinish(false)
            return
        }
        mScope.launch {
            val newRes = mSkinResourceLoader.loadSkinFromNet(url) {
                callback?.onProgress(it)
            }
            callback?.onFinish(newRes != null)
            if (autoApply) {
                mSkinResourceInfo =
                    newRes ?: mSkinResourceInfo ?: SkinResourceInfo(mApplicationContext.resources, mApplicationContext.packageName)
                notifySkinChanged()
            }
        }
    }

    fun loadLocalExistsSkin(localPath: String) {
        takeIf { !this::mApplicationContext.isInitialized }?.apply {
            Timber.tag("SkinManager").e("XSkin is not Initialized")
            return
        }
        mScope.launch {
            val newRes = mSkinResourceLoader.loadSkinFromLocal(localPath)
            mSkinResourceInfo =
                newRes ?: mSkinResourceInfo ?: SkinResourceInfo(mApplicationContext.resources, mApplicationContext.packageName)
            notifySkinChanged()
        }
    }

    fun getExistsSkinLocalPathByUrl(url: String): String? {
        return mSkinResourceLoader.getExistsSkinLocalPathByUrl(url)
    }

    fun reset() {
        takeIf { !this::mApplicationContext.isInitialized }?.apply {
            Timber.tag("SkinManager").e("XSkin is not Initialized")
            return
        }
        mScope.launch {
            mSkinResourceInfo = SkinResourceInfo(mApplicationContext.resources, mApplicationContext.packageName)
            notifySkinChanged()
        }
    }

    internal fun getCurrentResourceInfo() = mSkinResourceInfo

    internal fun getSkinInflaterFactory() = mSkinInflaterFactory

    internal fun getSkinContext() = mApplicationContext


    /*****************  data  ********************/
    fun setSkinAttrsWhenAddViewByCode(skinView: SkinViewWrapper) {
        addSkinView(skinView)
    }

    internal fun addSkinView(skinView: SkinViewWrapper) {
        skinView.apply()
        mSkinViews.add(skinView)
    }

    private fun notifySkinChanged() {
        mSkinViews.forEach {
            it.apply()
        }

    }

    internal fun clearInvalidReference() {
        takeIf { !this::mApplicationContext.isInitialized }?.apply {
            Timber.tag("SkinManager").e("XSkin is not Initialized")
            return
        }
        if (isDebug()) {
            Runtime.getRuntime().gc()
        }
        Timber.tag("SkinManager").d("clearInvalidReference before size: ${mSkinViews.size}")
        with(mSkinViews.iterator()) {
            while (hasNext()) {
                val next = next()
                if (next.isInvalid()) {
                    remove()
                }
            }
        }
        Timber.tag("SkinManager").d("clearInvalidReference after size: ${mSkinViews.size}")

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