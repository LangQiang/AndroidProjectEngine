package cn.godq.applogcat.proxy

import android.util.Log
import cn.godq.applogcat.mgr.AppLogcat
import com.bytedance.android.bytehook.ByteHook
import timber.log.Timber


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

//            override fun onNewTread() {
//                for (i in Thread.currentThread().stackTrace) {
//                    Log.i("onNewTread", i.toString())
//                }
//                Timber.tag("thread").e("onNewTread!!!!!!")
//            }
        })
    }

    external fun init(pltHookCallback: PLTHookCallback)
}