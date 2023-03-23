package cn.godq.applogcat.utils

import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.LogcatVm
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.ui.color.ColorSelector
import java.util.*


/**
 * @author  GodQ
 * @date  2023/3/8 12:10 下午
 */
val colorSelector = ColorSelector()

fun buildLogcatEntity(log: String?, tag: String?, color: AlcColor?, optFlag: Long): LogcatEntity? {
    log?: return null
    val tTag: String = if (tag.isNullOrEmpty()) LogcatVm.DEFAULT_TAG else tag
    val tColor = color?: colorSelector.nextColor()
    return LogcatEntity(
        null,
        UUID.randomUUID().toString(),
        log,
        tTag,
        tColor,
        isMainThread(),
        isMainProcess(),
        System.currentTimeMillis(),
        optFlag,
        AppLogcat.INSTANCE.thisBootMark
    )
}