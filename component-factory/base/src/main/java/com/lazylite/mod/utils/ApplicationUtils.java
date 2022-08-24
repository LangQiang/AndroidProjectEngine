package com.lazylite.mod.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.lazylite.mod.App;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by lzf on 2022/1/24 2:39 下午
 */
public class ApplicationUtils {

    private static ActivityManager getActivityManager() {
        if(null == App.getInstance()){
            return null;
        }
        ActivityManager am;
        try {
            am = (ActivityManager) App.getInstance().getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
        } catch (Exception e) {
            return null;
        }
        return am;
    }

    // 是否主进程
    public static boolean isMainProcess() {
        return "com.tencent.metarare".equals(getCurrentProcessName());
    }

    public static boolean isPlayMusicProcess(){
        return "com.tencent.metarare:service".equals(getCurrentProcessName());
    }

    // 是否是目标进程
    public static boolean isProcess(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        return target.equals(getCurrentProcessName());
    }


    // 当前进程名
    public static String getCurrentProcessName() {
        String processName = getProcessNameAboveP();
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }
        processName = getProcessNameFile();
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }
        processName = getProcessNameReflect();
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }
        // 最后都不行才连远程接口获取
        processName = getProcessNameAms();
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }
        return null;
    }

    // android p
    private static String getProcessNameAboveP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return null;
    }

    // 通过文件
    private static String getProcessNameFile() {
        String processName = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/cmdline"));
            processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    // 通过系统接口远程服务
    private static String getProcessNameAms() {
        List<ActivityManager.RunningAppProcessInfo> processInfos = null;
        if (ConfMgr.getBoolValue(IConfDef.SEC_APP, IConfDef.KEY_PROTOCOL_DIALOG_IS_SHOWED, false)) {
            processInfos = getRunningAppProcesses();
        }
        if (processInfos == null) {
            return null;
        }
        int myPid = android.os.Process.myPid();
        if (isListNotNull(processInfos)) {
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid) {
                    return info.processName;
                }
            }
        }
        return null;
    }

    private static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() {
        return getActivityManager() != null ? getActivityManager().getRunningAppProcesses() : null;
    }

    // 通过反射
    private static String getProcessNameReflect() {
        String processName = null;
        try {
            Application app = App.getApplication();
            if(null == app){
                return "";
            }
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);

            Method getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
            processName = (String) getProcessName.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }

    private static boolean isListNotNull(List list){
        return list != null && !list.isEmpty();
    }
}
