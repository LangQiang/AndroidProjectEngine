package com.lazylite.mod.http.okhttp.inerceptor;

import com.lazylite.mod.receiver.network.NetworkStateUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ReqCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        if (!NetworkStateUtil.isAvailable()) {
            //无网下强制缓存
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }

        Request newRequest = builder.build();

//        //有种情况是code=504 没有缓存还非得取缓存 取不到。。 可以根据需求在这个if里面去掉forceCache重复请求
//        if (response.code() == 504 && NetworkStateUtil.isAvailable()) {
//        }

        return chain.proceed(newRequest);
    }
}
