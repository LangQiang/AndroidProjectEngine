package cn.godq.applogcat.filter

import cn.godq.applogcat.ui.LogcatEntity


/**
 * @author  GodQ
 * @date  2023/3/20 6:26 下午
 */
interface IALCFilter {
    fun filter(entity: LogcatEntity): Boolean
}