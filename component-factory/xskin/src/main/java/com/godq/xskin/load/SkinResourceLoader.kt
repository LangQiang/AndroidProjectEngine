package com.godq.xskin.load

import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.godq.xskin.SkinManager
import com.godq.xskin.entity.SkinResource
import com.godq.xskin.inject.IDownloadInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @author  GodQ
 * @date  2023/6/5 4:15 PM
 */
class SkinResourceLoader {

    internal lateinit var downloadInject: IDownloadInject

    suspend fun loadSkinFromNet(url: String, callback: ((progress: Float) -> Unit)?): SkinResource? {
        val localPath = getLocalPath(url, callback)?: return null
        Timber.tag("SkinManager").d("loadSkin savePath:$localPath")
        return loadSkinFromLocal(localPath)
    }

    suspend fun loadSkinFromLocal(localPath: String): SkinResource? {
        return invokeResources(localPath)
    }

    fun getExistsSkinLocalPathByUrl(url: String): String? {
        val path = getSavePath(url) ?: return null
        return File(path).exists().let { if (it) path else "" }
    }

    private suspend fun invokeResources(resFilePath: String): SkinResource? {
        return withContext(Dispatchers.IO) {
            try {
                val pm: PackageManager = SkinManager.getSkinContext().packageManager
                val info = pm.getPackageArchiveInfo(
                    resFilePath,
                    PackageManager.GET_ACTIVITIES
                ) ?: return@withContext null
                val skinPackageName = info.packageName

                val assetManager = AssetManager::class.java.newInstance()
                val path = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
                path.invoke(assetManager, resFilePath)
                val superRes: Resources = SkinManager.getSkinContext().resources

                val res = Resources(
                    assetManager,
                    superRes.displayMetrics,
                    superRes.configuration
                )

                SkinResource(res, skinPackageName)
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
            downloadInject.asyncDownload(url, temp, object : IDownloadInject.DownloadCallback {
                override fun onComplete() {
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

                override fun onCancel() {
                    it.resume(null)
                }

                override fun onError(errorCode: Int, msg: String?) {
                    super.onError(errorCode, msg)
                    it.resume(null)
                }

                override fun onProgress(currentPos: Long, totalLength: Long) {
                    callback?.invoke(currentPos.toFloat() / totalLength)
                    Timber.tag("SkinManager").d("loadSkin progress:${currentPos * 100 / totalLength}")
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
        val fileName = getMD5Str(url) + ".apk"
        return  dir + File.separator + fileName
    }

    private fun getMD5Str(str: String): String? {
        val messageDigest: MessageDigest?
        try {
            messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(str.toByteArray(charset("UTF-8")))
            val byteArray = messageDigest.digest()
            val md5StrBuff = StringBuffer()
            for (i in byteArray.indices) {
                if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) md5StrBuff.append("0").append(
                    Integer.toHexString(0xFF and byteArray[i].toInt())
                ) else md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
            }
            return md5StrBuff.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


}