package cn.godq.applogcat.utils

import cn.godq.applogcat.db.log.LogDBEntity
import cn.godq.applogcat.ui.LogcatEntity


/**
 * @author  GodQ
 * @date  2023/3/15 6:12 下午
 */

fun LogcatEntity.transform2LogDBEntity(): LogDBEntity {
    return LogDBEntity(
        tag = this.tag,
        content = this.log,
        level = 0,
        isMainThread = this.isMainThread,
        isMainProcess = this.isMainProcess,
        timestamp = System.currentTimeMillis(),
        optFlag = this.optFLag,
        uuid = this.uuid,
        bootMark = this.bootMark,
    )
}

fun List<LogDBEntity>?.toLogcatEntityList(): List<LogcatEntity> {
    return ArrayList<LogcatEntity>().apply {
        if (!this@toLogcatEntityList.isNullOrEmpty()) {
            this@toLogcatEntityList.forEach {
                this.add(
                    LogcatEntity(
                        it.logId,
                        it.uuid,
                        it.content,
                        it.tag,
                        colorSelector.nextColor(),
                        it.isMainThread,
                        it.isMainProcess,
                        it.timestamp,
                        it.optFlag,
                        it.bootMark,
                    )
                )
            }
        }
    }
}

fun Long.removeOptFlag(flag: Long): Long {
    return this and flag.inv()
}

fun Long.addOptFlag(flag: Long): Long {
    return this or flag
}

fun Long.hasOptFlag(flag: Long): Boolean {
    return (this and flag) == flag
}