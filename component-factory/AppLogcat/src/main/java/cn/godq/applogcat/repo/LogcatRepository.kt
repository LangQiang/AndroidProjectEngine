package cn.godq.applogcat.repo

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import cn.godq.applogcat.db.ALCDBInsertHelper
import cn.godq.applogcat.db.ALCDBManager
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.LogcatVm
import cn.godq.applogcat.utils.toLogcatEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author  GodQ
 * @date  2023/3/6 5:41 下午
 */
object LogcatRepository {

    @MainThread
    fun insertLog(log: LogcatEntity) {
        ALCDBInsertHelper.insertOne(log)
    }

    @MainThread
    suspend fun getHistoryLogsByTag(tag: String?): List<LogcatEntity> {
        val historyLogList = withContext(Dispatchers.IO) {
            if (tag == null || tag == "default")
                ALCDBManager.getLogDBDao()?.queryAllLogs(Long.MAX_VALUE, 100).toLogcatEntityList()
            else
                ALCDBManager.getLogDBDao()?.queryLogsByTag(tag, Long.MAX_VALUE, 100).toLogcatEntityList()
        }
        return mergeList(tag ?: "default",historyLogList)
    }

    private fun mergeList(tag: String,
        historyLogList: List<LogcatEntity>,
    ): List<LogcatEntity> {
        val recordList = ALCDBInsertHelper.getAllInsertingEntity()

        if (recordList.isEmpty()) return historyLogList

        val lastEntity = if (historyLogList.isEmpty()) null else historyLogList[0]

        val retList = ArrayList(historyLogList)

        var find = false
        var index = -1
        for (i in recordList.indices) {
            if (recordList[i].uuid == lastEntity?.uuid) {
                find = true
                index = i + 1
                break
            }
        }
        val addList = if (find) {
            if (index in recordList.indices) {
                recordList.subList(index, recordList.size)
            } else {
                emptyList()
            }
        } else {
            recordList
        }
        addList.forEach {

            if (it.tag == tag || tag == LogcatVm.DEFAULT_TAG) {
                retList.add(0, it)
            }
        }
        return retList
    }

    @WorkerThread
    fun clearDiskLogData(timestampMs: Long = Long.MAX_VALUE) {
        ALCDBManager.getLogDBDao()?.delete(timestampMs)
    }
}