package cn.godq.applogcat.filter

import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.ui.LogcatEntity


/**
 * @author  GodQ
 * @date  2023/3/16 5:56 下午
 */



fun filter(logs: List<LogcatEntity>): List<LogcatEntity> {
    val filters = mutableListOf<IALCFilter>()
    filters.add(ALCBootMarkFilter(AppLogcat.INSTANCE.thisBootMark))
    return ArrayList<LogcatEntity>().apply {
        logs.forEach {
            var finalAdd = true
            for (filter in filters) {
                if (!filter.filter(it)) {
                    finalAdd = false
                    break
                }
            }
            if (finalAdd) {
                add(it)
            }
        }
    }
}