package cn.godq.applogcat.proxy

import cn.godq.applogcat.mgr.AppLogcat
import com.bytedance.android.bytehook.ByteHook


/**
 * @author  GodQ
 * @date  2023/4/24 2:37 下午
 */
object SysLogHooker {

    fun load() {
        System.loadLibrary("plthook")
        ByteHook.init()
        init(object : PLTHookCallback{
            override fun onLog(tag: String, log: String) {
                AppLogcat.getInstance().log(log = log, tag = tag)
            }
        })
    }

    external fun init(pltHookCallback: PLTHookCallback)
}