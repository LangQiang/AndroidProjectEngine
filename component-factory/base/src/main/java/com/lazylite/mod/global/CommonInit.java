package com.lazylite.mod.global;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;

import com.example.basemodule.BuildConfig;
import com.lazylite.mod.App;
import com.lazylite.mod.http.mgr.KwHttpConfig;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.imageloader.fresco.load.impl.FrescoImageLoader;
import com.lazylite.mod.log.LogMgr;
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
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

public class CommonInit {

    private static boolean isInit;

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static void initAfterAgreeProtocol(Context context) {
        //
    }

    public static void initOnAppCreate(Context context) {

        if (isInit) {
            return;
        }

        isInit = true;

        App.init(context);
        MMKV.initialize(context.getFilesDir().getAbsolutePath() + "/mmkv", MMKVLogLevel.LevelNone);
        DeviceInfo.initScreenInfo(context);
        AppInfo.init(context);
        FrescoImageLoader.getInstance().initialize(context);
        //http
        KwHttpConfig.Builder builder = KwHttpConfig.newOkHttpBuilder(context, new Handler(Looper.getMainLooper()));
        if(BuildConfig.IS_ALLOW_PROXY) {
            builder.setHostnameVerifier(DO_NOT_VERIFY);
            builder.setTrustManager(getX509TrustManager());
            builder.setSslSocketFactory(getSSLSocketFactory());
        }
        LogMgr.e("https",BuildConfig.IS_ALLOW_PROXY + "");
        KwHttpMgr.getInstance().init(context, builder.build());
        //
        NetworkStateUtil.init(context);
        SDCardUtils.init(context);

        if (isDebug(context)) {
            Timber.plant(new Timber.DebugTree());
        }
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
        } catch (Exception e) {
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
