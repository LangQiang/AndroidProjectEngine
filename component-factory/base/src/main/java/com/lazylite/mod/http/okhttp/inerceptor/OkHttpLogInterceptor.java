package com.lazylite.mod.http.okhttp.inerceptor;

import android.util.Log;

import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.okhttp.OkHttpConstants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        try {

            Request request = chain.request();

            //1.请求前--打印请求信息
            long t1 = System.nanoTime();

            if (KwHttpMgr.isDebug()) {
                Log.i(OkHttpConstants.TAG, String.format("OkHttpLogInterceptor: %nSending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers()));
            }

            //2.网络请求
            Response response;

            try {
                response = chain.proceed(request);
            } catch (IOException e) {
                if (KwHttpMgr.isDebug()) {
                    Log.i(OkHttpConstants.TAG, "OkHttpLogInterceptor: " + e.getMessage());
                }
                throw e;
            }

            //3.网络响应后--打印响应信息
            long t2 = System.nanoTime();

            if (response != null) {

                String responseType;

                if (response.networkResponse() != null) {
                    responseType = "networkResponse";
                } else if (response.cacheResponse() != null) {
                    responseType = "cacheResponse";
                } else {
                    responseType = "no response";
                }

                if (KwHttpMgr.isDebug()) {
                    Log.i(OkHttpConstants.TAG,
                            String.format("OkHttpLogInterceptor: %nReceived response for " + "%s " + "in " + "%.1f" + "ms%n" +
                                            "%s" + "%n" +
                                            "Method: %s, ResponseType: %s%n%n ",
                                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), request.method(), responseType));
                }
            }

            return response;

        } catch (Exception e) {

            return chain.proceed(chain.request());

        }
    }
}
