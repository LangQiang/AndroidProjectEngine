package com.lazylite.bridge.init;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.godq.deeplink.DeepLinkConfig;
import com.godq.deeplink.DeepLinkUtils;
import com.godq.threadpool.TasksKt;
import com.godq.threadpool.ThreadPool;
import com.lazylite.mod.App;
import com.lazylite.mod.global.BaseConfig;
import com.lazylite.mod.http.mgr.KwHttpConfig;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.imageloader.fresco.load.impl.FrescoImageLoader;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.receiver.sdcard.SDCardUtils;
import com.lazylite.mod.utils.AppInfo;
import com.lazylite.mod.utils.DeviceInfo;
import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVLogLevel;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

class CommonInit {

    private static boolean isInit;

    final static HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

    public static void initOnAppCreate(@NonNull Context context, BaseConfig config) {

        if (isInit) {
            return;
        }

        isInit = true;

        App.init(context);
        DeepLinkConfig deepLinkConfig = new DeepLinkConfig();
        deepLinkConfig.schemeName = config == null || TextUtils.isEmpty(config.deepLinkScheme) ? "debug" : config.deepLinkScheme;
        deepLinkConfig.iExecutor = runnable -> ThreadPool.exec(TasksKt.TASK_MODE_IO, runnable);
        DeepLinkUtils.init(deepLinkConfig);
        MMKV.initialize(context.getFilesDir().getAbsolutePath() + "/mmkv", MMKVLogLevel.LevelNone);
        DeviceInfo.initScreenInfo(context);
        AppInfo.init(context);
        FrescoImageLoader.getInstance().initialize(context);
        //http
        KwHttpConfig.Builder builder = KwHttpConfig.newOkHttpBuilder(context, new Handler(Looper.getMainLooper()));
        if(config != null && config.allowProxy) {
            builder.setHostnameVerifier(DO_NOT_VERIFY);
            builder.setTrustManager(getX509TrustManager());
            builder.setSslSocketFactory(getSSLSocketFactory());
        }
        KwHttpMgr.getInstance().init(context, builder.build());
        //
        NetworkStateUtil.init(context);
        SDCardUtils.init(context);

        if (isDebug(context)) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static void initAfterAgreeProtocol(@NonNull Context context, BaseConfig config) {
        //
    }

    private static boolean isDebug(Context context) {
        ApplicationInfo info;
        try {
            info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignore) {

        }
        return false;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { getX509TrustManager() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignore) {
        }

        return ssfFactory;
    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

    }
}
