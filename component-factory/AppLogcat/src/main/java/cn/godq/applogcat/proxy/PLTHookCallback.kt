package cn.godq.applogcat.proxy


/**
 * @author  GodQ
 * @date  2023/4/24 2:42 下午
 */
interface PLTHookCallback {
    fun onLog(tag: String, log: String) {}
}