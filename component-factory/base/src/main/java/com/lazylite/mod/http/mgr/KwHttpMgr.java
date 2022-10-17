package com.lazylite.mod.http.mgr;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class KwHttpMgr {

    private static final String TAG = "KwHttpMgr";

    private static volatile boolean sIsInitialized = false;

    private static boolean sIsDebug = false;

    private static boolean sIsApkDebug = true;

    private static Application sApplication;

    private KwHttpConfig kwHttpConfig;

    public void init(Context context, @NonNull KwHttpConfig config) {

        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("context必须是Application或者Application的子类");
        }

        if (sIsInitialized) {
            if (isDebug()) {
                Log.i(TAG, "already init");
            }
        } else {
            sApplication = (Application) context;
            sIsInitialized = true;
            kwHttpConfig = config;
        }
    }

//    @NonNull
//    public Map<String, String> getCommonHeaders() {
//        return kwHttpConfig.getCommonHeaders();
//    }
//
//    @NonNull
//    public Map<String, String> getCommonParams() {
//        return kwHttpConfig.getCommonParams();
//    }
//
//    @NonNull
//    public List<IHttpResultCheckPolicy> getResultCheckPolicies(){
//        return kwHttpConfig.getResultCheckPolicies();
//    }

    @NonNull
    public IKwHttpFetcher getKwHttpFetch() {

        if (!sIsInitialized) {
            throw new RuntimeException("kwHttpMgr未初始化！！！");
        }

        IKwHttpFetcher kwHttpFetch = kwHttpConfig.getKwHttpFetch();

        if (isDebug()) {
            Log.i(TAG, "http frame: " + kwHttpFetch.getFrameName());
        }

        return kwHttpFetch;
    }

    public void addCommonParamProvider(ICommonParamProvider commonParamProvider) {
        kwHttpConfig.addCommonParamProvider(commonParamProvider);
    }

    public static void setDebugEnable(boolean enable) {
        sIsDebug = enable;
    }

    public static boolean isDebug() {

        if (sIsDebug) { //强制设置debug模式
            return true;
        }

        if (!sIsApkDebug || sApplication == null) {
            return false;
        }

        try {
            ApplicationInfo info = sApplication.getApplicationInfo();
            sIsApkDebug = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            return sIsApkDebug;
        } catch (Exception ignore) {

        }

        return false;
    }

    public static boolean isHttpRequest(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http") || url.startsWith("https");
    }

    public static String printCommonParamInfo() {
        return getInstance().kwHttpConfig.printCommonParamInfo();
    }

    private static class Inner {
        private static final KwHttpMgr inner = new KwHttpMgr();
    }

    public static KwHttpMgr getInstance() {
        return Inner.inner;
    }
}
