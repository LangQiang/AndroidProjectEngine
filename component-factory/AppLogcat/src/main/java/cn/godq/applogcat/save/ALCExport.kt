package cn.godq.applogcat.save

import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.repo.LogcatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author  GodQ
 * @date  2023/4/18 7:07 下午
 */
object ALCExport {

    private var rootDir: File? = null

    private fun getExportDir(): File? {
        if (rootDir == null) {
            val root = AppLogcat.INSTANCE.mContext?.getExternalFilesDir(null)?.absolutePath?: return null
            rootDir = File(root, "ALC")
        }
        if (rootDir?.exists() != true) {
            rootDir?.mkdirs()
        }
        return rootDir
    }

    private fun getExportAbsoluteFile(tag: String?): File {
        return File(getExportDir(), "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_${tag}_${UUID.randomUUID()}.txt")
    }

    suspend fun exportByTag(tag: String?): String? {
        val list = LogcatRepository.getHistoryLogsByTag(tag).reversed()
        return withContext(Dispatchers.IO) {
            try {
                val saveFile = getExportAbsoluteFile(tag)
                saveFile.bufferedWriter().use { out ->
                    list.forEach {
                        out.write(it.formatForNormalSaveStr(tag))
                        out.newLine()
                        out.flush()
                    }
                }
                saveFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }
}