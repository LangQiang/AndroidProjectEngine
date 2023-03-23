package cn.godq.applogcat.mgr

import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.color.AlcColor


/**
 * @author  GodQ
 * @date  2023/3/3 4:10 下午
 */
interface IAlcApi {
    fun log(log: String?) //for java
    fun log(log: String?, tag: String?) //for java
    fun log(log: String?, tag: String?, color: AlcColor?) //for java
    fun log(log: String?, tag: String? = null, color: AlcColor? = null, flag: Long = LogcatEntity.getDefaultOptFlag())
}