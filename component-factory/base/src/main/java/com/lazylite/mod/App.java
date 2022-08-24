package com.lazylite.mod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lazylite.mod.utils.ApplicationUtils;
import com.lazylite.mod.utils.DeviceInfo;

/**
 * Created by lzf on 5/28/21 2:44 PM
 */
public class App {

    private static boolean mainProcess = true;

    private static Boolean isDebug = null;

    @SuppressLint("StaticFieldLeak") //销毁重建时重新赋值，不会泄漏忽略提示
    private static Activity mainActivity;

    @SuppressLint("StaticFieldLeak") //实际为ApplicationContext 可以忽略泄漏提示
    private static Context sContext;

    public static void init(Context context) {
        if (context instanceof Application) {
            sContext = context;
            ((Application) sContext).registerActivityLifecycleCallbacks(lifecycleCallbacks);
        } else {
            sContext = context.getApplicationContext();
        }
        mainProcess = checkMainProcess();
    }

    private static boolean checkMainProcess() {
        return sContext.getPackageName().equals(ApplicationUtils.getCurrentProcessName());
    }

    public static void setMainActivity(Activity activity) {
        mainActivity = activity;
    }

    public static boolean isMainProcess() {
        return mainProcess;
    }

    public static boolean isDebug() {
        if (isDebug != null) {
            return isDebug;
        }
        ApplicationInfo info;
        try {
            info = sContext.getApplicationInfo();
            isDebug = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            return isDebug;
        } catch (Exception ignore) {

        }
        return false;
    }

    public static Context getInstance() {
        return sContext;
    }

    @Nullable
    public static Activity getMainActivity() {
        return mainActivity;
    }

    public static Application getApplication() {
        if (sContext instanceof Application) {
            return (Application) sContext;
        }
        return null;
    }


    public static boolean isExiting() {
        return false;
    }


    private static final Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (sContext instanceof Application) {
                ((Application) sContext).unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
                DeviceInfo.initScreenInfo(activity);
            }

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


}
