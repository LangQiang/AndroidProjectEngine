package com.lazylite.mod.log;

import com.lazylite.mod.utils.AppInfo;

import timber.log.Timber;

public class LogMgr {
    public static boolean isDebug = AppInfo.IS_DEBUG;
    private static final String commonTag = "base";


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
            Timber.tag(tag).i(log);
        }
    }


    public static void e(String tag, String log) {
        if (isDebug) {
            Timber.tag(tag).e(log);
        }
    }

    public static void e(String tag, Throwable throwable) {
        if (isDebug) {
            Timber.tag(tag).e("%s", throwable.getMessage());
        }
    }


    public static void v(String tag, String log) {
        if (isDebug) {
            Timber.tag(tag).v(log);
        }
    }


    public static void d(String tag, String log) {
        if (isDebug) {
            Timber.tag(tag).d(log);
        }
    }


    public static void w(String tag, String log) {
        if (isDebug) {
            Timber.tag(tag).w(log);
        }
    }

}
