package cn.godq.applogcat.filter

import cn.godq.applogcat.ui.LogcatEntity


/**
 * @author  GodQ
 * @date  2023/3/20 6:30 下午
 */
class ALCBootMarkFilter(private val bootMark: String): IALCFilter {
    override fun filter(entity: LogcatEntity) = entity.bootMark == bootMark
}