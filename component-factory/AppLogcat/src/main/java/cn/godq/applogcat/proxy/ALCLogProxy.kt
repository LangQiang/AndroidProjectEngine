package cn.godq.applogcat.proxy

import cn.godq.applogcat.init.ALCConfig
import cn.godq.applogcat.mgr.AppLogcat
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/3/7 3:35 下午
 */

fun proxyOtherLog(config: ALCConfig?) {
    try {
        //Timber
        if (assembleWithTimber()) {
            Timber.plant(object : Timber.Tree(){
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    AppLogcat.getInstance().log(message, tag)
                }
            })
        }

        //android.util.log
        SysLogHooker.load()
    } catch (e: Exception) {

    }
}

fun assembleWithTimber(): Boolean {
    return try {
        Class.forName("timber.log.Timber")
        true
    } catch (e: Exception) {
        false
    }
}
