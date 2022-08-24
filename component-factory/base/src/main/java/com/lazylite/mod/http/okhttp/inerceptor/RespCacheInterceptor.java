package com.lazylite.mod.http.okhttp.inerceptor;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

public class RespCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(chain.request()).newBuilder()
                .removeHeader("Pragma") //移除影响
                .removeHeader("Cache-Control") //移除影响
                .addHeader("Cache-Control", CacheControl.FORCE_CACHE.toString())
                .build();
    }
}
