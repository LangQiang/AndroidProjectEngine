package com.lazylite.mod.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;


/**
 * Created by DongJr on 2017/8/15.
 *
 * 全局生命周期监听,暂用于判断应用是否处于前台
 */

public class KwLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static int mActivityCount;

    private static KwLifecycleCallback mInstance = new KwLifecycleCallback();
    private static WeakReference<Activity> sTopActivityRef;

    private KwLifecycleCallback(){}

    public static KwLifecycleCallback getInstance() {
        return mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCount++;
        AppInfo.updateForgroundState();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        sTopActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Activity top = getTopActivity();
        if (top != null && top == activity) {
            sTopActivityRef.clear();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCount--;
        AppInfo.updateForgroundState();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public static boolean isForeground() {
        return mActivityCount > 0;
    }

    // ActivityLifecycleCallback不像am.getRunningTasks，这个只能获取自己应用的。
    public static Activity getTopActivity() {
        Activity topActivity = null;
        if (sTopActivityRef != null) {
            topActivity = sTopActivityRef.get();
        }
        return topActivity;
    }
}
