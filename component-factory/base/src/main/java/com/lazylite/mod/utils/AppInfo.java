/*
 * Have a nice day.
 * @author YangSong
 * @mail song.yang@kuwo.cn
 */
package com.lazylite.mod.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.lazylite.mod.App;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.channel.outhelper.ChannelReaderHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// by haiping
public final class AppInfo {

    // name，某些api会用到
    public static final String APP_NAME = "YuanXi";
    private static final String FST_LAUNCH_KEY = "fstLaunch";
    public static String CLIENT_IP = "0.0.0.0";
    public static boolean IS_FORGROUND = false;
    public static boolean COVER_INSTALL = false;
    public static long START_TIME = 0;
    public static long START_TIMES;
    public static boolean START_LOG_SENDED = false;
    // 是否是debug版本
    public static boolean IS_DEBUG = true;
    // 版本号，0.0.0.0格式
    public static String VERSION_CODE;
    // 极速版，kwbooklite_ar_0.0.0.0格式
    public static String VERSION_NAME;

    // 内部版本号，暂时只在日志里出现
    public static int INTERNAL_VERSION;
    // 安装源
    public static String INSTALL_SOURCE;

    // 渠道
    public static String INSTALL_CHANNEL;
    public static String APP_UID="";
    private static boolean sIsInit;
    private static IAppUidResource sAppUid_Resource;

    public static void init(Context ctx) {
        if (sIsInit) {
            return;
        }
        sIsInit = true;

        ApplicationInfo info;
        try {
            info = ctx.getApplicationInfo();
            initParams(ctx);
        } catch (Exception e) {
            KwDebug.classicAssert(false);
            return;
        }

        IS_DEBUG = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

    }

    // 判断是否当日初次启动
    public static boolean isTodayFstLaunch() {
        String savedDate = ConfMgr.getStringValue("", FST_LAUNCH_KEY, "00000000");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
                Locale.SIMPLIFIED_CHINESE);
        String currentDate = format.format(date);
        if (!currentDate.equals(savedDate)) {
            ConfMgr.setStringValue("", FST_LAUNCH_KEY, currentDate, false);
            return true;
        } else {
            return false;
        }
    }

    public static String getVersionCode(@NonNull Context context) {
        if (!TextUtils.isEmpty(VERSION_CODE) && !"0.0.0.0".equals(VERSION_CODE)) {
            return VERSION_CODE;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            KwDebug.classicAssert(false, e);
        }
        return "0.0.0.0";
    }

    private static void initParams(Context context) {
        VERSION_CODE = getVersionCode(context);
        VERSION_NAME = "tmepodcast_ar_" + VERSION_CODE;
        String channel = ChannelReaderHelper.readChannel(context.getPackageCodePath());
        INSTALL_CHANNEL = channel;
        if (TextUtils.isEmpty(channel)) {
            INSTALL_SOURCE = VERSION_NAME;
        } else {
            INSTALL_SOURCE = VERSION_NAME + "_" + INSTALL_CHANNEL;
        }
    }

    public static String getAppUid(){
        if(TextUtils.isEmpty(APP_UID)){
            final IAppUidResource resource = sAppUid_Resource;
            if(null != resource){
                resource.fetch(new IAppUidResourceCallback() {
                    @Override
                    public void onFetch(String appUid) {
                        APP_UID = appUid;
                    }
                });
            }
        }
        return APP_UID;
    }

    public static void setAppUidResource(@NonNull IAppUidResource resource){
        sAppUid_Resource = resource;
    }

    public static void updateForgroundState() {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                innerUpdateForgroundState();
            }
        });
    }

    private static boolean forceForground;

    public static void setForceForground(boolean force) {
        forceForground = force;
        innerUpdateForgroundState();
    }

    private static void innerUpdateForgroundState() {
        boolean isForGround;
        if (forceForground) {
            isForGround = true;
        } else {
            isForGround= checkAppForground();
        }
        if (IS_FORGROUND == isForGround) {
            return;
        }
        IS_FORGROUND = isForGround;
    }

    private static boolean checkAppForground(){
        return KwLifecycleCallback.isForeground();
    }

    public interface IAppUidResource{
        void fetch(IAppUidResourceCallback callback);
    }//
    public interface IAppUidResourceCallback{
        void onFetch(String appUid);
    }//
}
