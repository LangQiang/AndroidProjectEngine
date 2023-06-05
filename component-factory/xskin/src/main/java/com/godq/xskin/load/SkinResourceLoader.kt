package com.godq.xskin.load

import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.godq.xskin.SkinManager
import com.godq.xskin.entity.SkinResourceInfo
import com.lazylite.mod.http.mgr.HttpWrapper
import com.lazylite.mod.http.mgr.IKwHttpFetcher
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.DownReqInfo
import com.lazylite.mod.utils.crypt.MD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @author  GodQ
 * @date  2023/6/5 4:15 PM
 */
class SkinResourceLoader {

    suspend fun loadSkin(url: String, callback: ((progress: Float) -> Unit)?): SkinResourceInfo? {
        val localPath = getLocalPath(url, callback)?: return null
        Timber.tag("SkinManager").e("loadSkin savePath:$localPath")
        return invokeResources(localPath)
    }

    private suspend fun invokeResources(resFilePath: String): SkinResourceInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val pm: PackageManager = SkinManager.getSkinContext().packageManager
                val info = pm.getPackageArchiveInfo(
                    resFilePath,
                    PackageManager.GET_ACTIVITIES
                ) ?: return@withContext null
                val skinPackageName = info.packageName

                Timber.tag("skinManager").e("packageName:${skinPackageName}")
                val assetManager = AssetManager::class.java.newInstance()
                val path = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
                path.invoke(assetManager, resFilePath)
                val superRes: Resources = SkinManager.getSkinContext().resources

                val res = Resources(
                    assetManager,
                    superRes.displayMetrics,
                    superRes.configuration
                )

                SkinResourceInfo(res, skinPackageName)
            } catch (e: Exception) {
                Timber.tag("SkinManager").e("e: ${e.message}")
                null
            }
        }
    }

    private suspend fun getLocalPath(url: String, callback: ((progress: Float) -> Unit)?): String? {
        return suspendCoroutine {
            val path = getSavePath(url)
            if (path == null) {
                it.resume(null)
                return@suspendCoroutine
            }
            if (File(path).exists()) {
                it.resume(path)
                callback?.invoke(1f)
                return@suspendCoroutine
            }
            val temp = "$path.temp"
            KwHttpMgr.getInstance().kwHttpFetch.asyncDownload(DownReqInfo(url, temp, 0), null, object : IKwHttpFetcher.DownloadListener {
                override fun onComplete(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                    val tempFile = File(temp)
                    if (tempFile.exists()) {
                        val newFile = File(path)
                        tempFile.renameTo(newFile)
                        callback?.invoke(1f)
                        it.resume(path)
                    } else {
                        it.resume(null)
                    }
                }

                override fun onCancel(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                    it.resume(null)
                }

                override fun onError(errorCode: Int, msg: String?, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                    it.resume(null)
                }

                override fun onProgress(currentPos: Long, totalLength: Long, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                    callback?.invoke(currentPos.toFloat() / totalLength)
                    Timber.tag("SkinManager").e("loadSkin progress:${currentPos.toFloat() / totalLength}")
                }
            })
        }
    }

    private fun getSavePath(url: String): String? {
        val getExternalFilesDirNoPermission = SkinManager.getSkinContext().getExternalFilesDir(null)?: return null
        val dir =  getExternalFilesDirNoPermission.absolutePath + File.separator + "Skin"
        with(File(dir)) {
            if (!this.exists()) {
                this.mkdirs()
            }
        }
        val fileName = MD5.getMD5Str(url) + ".apk"
        return  dir + File.separator + fileName
    }

}