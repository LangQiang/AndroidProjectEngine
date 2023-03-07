package cn.godq.applogcat.mgr

import cn.godq.applogcat.ui.color.AlcColor


/**
 * @author  GodQ
 * @date  2023/3/3 4:10 下午
 */
interface IAlcApi {
    fun log(log: String?)
    fun log(log: String?, tag: String?)
    fun log(log: String?, tag: String?, color: AlcColor?)
}