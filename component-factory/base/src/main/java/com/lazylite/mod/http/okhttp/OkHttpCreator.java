package com.lazylite.mod.http.okhttp;

import android.content.Context;
import android.util.Log;

import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpConfig;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.okhttp.inerceptor.FixedIpRetryInterceptor;
import com.lazylite.mod.http.okhttp.inerceptor.OkHttpLogInterceptor;
import com.lazylite.mod.http.okhttp.inerceptor.ProgressInterceptor;
import com.lazylite.mod.http.okhttp.inerceptor.ReqCacheInterceptor;
import com.lazylite.mod.http.okhttp.inerceptor.RespCacheInterceptor;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.OkHttpClient;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OkHttpCreator {

    public static IKwHttpFetcher create(KwHttpConfig kwHttpConfig) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (KwHttpMgr.isDebug()) {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            X509TrustManager x509TrustManager = getX509TrustManager();
            if (sslSocketFactory != null) {
                builder.sslSocketFactory(sslSocketFactory, x509TrustManager);
            }
            builder.hostnameVerifier(getHostnameVerifier());
        }else {
            if(null != kwHttpConfig.getHostnameVerifier()){
                builder.hostnameVerifier(kwHttpConfig.getHostnameVerifier());
            }
            if(null != kwHttpConfig.getTrustManager() && null != kwHttpConfig.getSslSocketFactory()){
                builder.sslSocketFactory(kwHttpConfig.getSslSocketFactory(),kwHttpConfig.getTrustManager());
            }
        }

        builder.dns(getDns());

        builder.connectTimeout(OkHttpConstants.TIME_OUT_CONNECT_SECONDS, TimeUnit.SECONDS);
        builder.writeTimeout(OkHttpConstants.TIME_OUT_WRITE_SECONDS, TimeUnit.SECONDS);
        builder.readTimeout(OkHttpConstants.TIME_OUT_READ_SECONDS, TimeUnit.SECONDS);

        builder.cache(createCache(kwHttpConfig.getContext()));

        builder.addInterceptor(new OkHttpLogInterceptor());
        builder.addInterceptor(new ReqCacheInterceptor());
        builder.addInterceptor(new FixedIpRetryInterceptor());

        builder.addNetworkInterceptor(new RespCacheInterceptor());
        builder.addInterceptor(new ProgressInterceptor());

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(128);
        dispatcher.setMaxRequestsPerHost(128);
        builder.dispatcher(dispatcher);

        return new OkHttpFetcher(builder.build(), kwHttpConfig);
    }


    /* ****************************** 忽略证书 ************************************** */

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

    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (KwHttpMgr.isDebug()) {
                    Log.e(OkHttpConstants.TAG, "verify hostname:" + hostname);
                }
                return true;
            }
        };
    }

    private static Dns getDns() {
        //ok 默认实现 可换成httpDns的方式
        return new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                if (hostname == null) throw new UnknownHostException("hostname == null");
                try {
                    InetAddress[] allByName = InetAddress.getAllByName(hostname);
                    if (KwHttpMgr.isDebug()) {
                        for (InetAddress inetAddress : allByName) {
                            Log.i("Testdns", "host:" + hostname + "  IP:" + inetAddress.getHostAddress());

                        }
                    }
                    return Arrays.asList(allByName);
                } catch (NullPointerException e) {
                    UnknownHostException unknownHostException =
                            new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
                    unknownHostException.initCause(e);
                    throw unknownHostException;
                }
            }
        };
    }

    private static Cache createCache(Context context) {
        //缓存文件夹
        File cacheFile = new File(context.getExternalCacheDir(), OkHttpConstants.CHILD_CACHE_FILE);
        //缓存大小为10M
        int cacheSize = OkHttpConstants.CACHE_SIZE;
        //返回缓存对象
        return new Cache(cacheFile,cacheSize);
    }

}
