package cn.godq.applogcat.db

import androidx.annotation.MainThread
import cn.godq.applogcat.db.log.LogDBEntity
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.utils.toLogcatEntityList
import cn.godq.applogcat.utils.transform2LogDBEntity
import kotlinx.coroutines.*


/**
 * @author  GodQ
 * @date  2023/3/15 6:02 下午
 */
object ALCDBInsertHelper {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val tempWaitingInsertList = ArrayList<LogDBEntity>()

    private val insertingList = ArrayList<LogDBEntity>()

    private var isRunning = false

    @MainThread
    fun insertOne(log: LogcatEntity) {
        tempWaitingInsertList.add(log.transform2LogDBEntity())
        doExecute()
    }

    private fun doExecute() {
        if (isRunning) {
            return
        }
        isRunning = true

        scope.launch {
            while (tempWaitingInsertList.isNotEmpty()) {
                val insertList = ArrayList(tempWaitingInsertList)
                insertingList.clear()
                insertingList.addAll(insertList)
                tempWaitingInsertList.clear()
                withContext(Dispatchers.IO) {
                    ALCDBManager.getLogDBDao()?.insertAll(*insertList.toTypedArray())
                }
            }
            isRunning = false
        }
    }

    fun getAllInsertingEntity(): List<LogcatEntity> {
        val retList = ArrayList<LogcatEntity>()
        retList.addAll(insertingList.toLogcatEntityList())
        retList.addAll(tempWaitingInsertList.toLogcatEntityList())
        return retList
    }
}