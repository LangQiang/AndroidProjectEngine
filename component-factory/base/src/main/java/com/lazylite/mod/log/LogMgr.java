package com.lazylite.mod.log;

import android.util.Log;

import com.example.basemodule.BuildConfig;
import com.lazylite.mod.utils.AppInfo;

/**
 * @author qyh
 * email：yanhui.qiao@kuwo.cn
 * @date 2021/5/31.
 * description：
 */
public class LogMgr {
    public static boolean isDebug = AppInfo.IS_DEBUG;
    private static String commonTag = "lazy";


    public static void setDebug(boolean debug) {
        isDebug = debug;
    }


    public static void i(String log) {
        i(commonTag, log);
    }


    public static void e(String log) {
        e(commonTag, log);
    }


    public static void v(String log) {
        v(commonTag, log);
    }


    public static void d(String log) {
        d(commonTag, log);
    }


    public static void w(String log) {
        w(commonTag, log);
    }


    public static void i(String tag, String log) {
        if (isDebug) {
            Log.i(tag, log);
        }
    }


    public static void e(String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
        }
    }

    public static void e(String tag, Throwable throwable) {
        if (isDebug) {
            Log.e(tag, throwable.getMessage() + "");
        }
    }


    public static void v(String tag, String log) {
        if (isDebug) {
            Log.v(tag, log);
        }
    }


    public static void d(String tag, String log) {
        if (isDebug) {
            Log.d(tag, log);
        }
    }


    public static void w(String tag, String log) {
        if (isDebug) {
            Log.w(tag, log);
        }
    }

    public static boolean logRealMsg(String strAct, String strContent, int ret) {
        return LogUploadManager.getInstance().logRealMsg(strAct, strContent, ret);
    }

    public static boolean saveRealMsg(String strAct, String strContent, int ret) {
        return LogUploadManager.getInstance().saveRealMsg(strAct, strContent, ret);
    }

    public static boolean asynSendOfflineLog(String content, int arg1) {
        return LogUploadManager.getInstance().asynSendOfflineLog(content, arg1);
    }

    /**
     * 检测本地是否有没有及时上传的日志，检测成功后，自动上传
     */
    public static void checkLocalLogAndUpload() {
        LogUploadManager.getInstance().checkLocalLogAndUpload();
    }
}
