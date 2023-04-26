package com.lazylite.mod.http.okhttp.inerceptor;

import android.util.Log;

import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.okhttp.OkHttpConstants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

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

            printCurl(request);

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
                                            "Method: %s, ResponseType: %s%n%n"+
                                            "Response Code：%s%n%n ",
                                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), request.method(), responseType,response.code()));
                }
            }

            return response;

        } catch (Exception e) {

            return chain.proceed(chain.request());

        }
    }

    private void printCurl(Request request) {
        String url = request.url().toString();
        String method = request.method();
        Map<String, List<String>> headers = request.headers().toMultimap();

        StringBuilder curlSb = new StringBuilder();
        curlSb.append("curl ").append(url);
        curlSb.append(" -X ").append(method);

        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            curlSb.append(" -H ");
            curlSb.append("'");
            curlSb.append(stringListEntry.getKey());
            curlSb.append(":");
            for (int i = 0; i < stringListEntry.getValue().size(); i++) {
                curlSb.append(stringListEntry.getValue().get(i));
                if (i != (stringListEntry.getValue().size() - 1)) {
                    curlSb.append(",");
                }
            }
            curlSb.append("'");
        }

        if ("POST".equalsIgnoreCase(method)) {
            String body = getBody(request);
            if (body != null) {
                curlSb.append(" -d ").append("'").append(body).append("'");
            }
        }
        String curlStr = curlSb.toString();
        Timber.tag("okhttp").d(curlStr);
    }

    private String getBody(Request request) {
        try {
            RequestBody requestBody = request.body();
            if (requestBody == null) return null;
            okio.Buffer buffer = new okio.Buffer();
            requestBody.writeTo(buffer);
            Charset charset = StandardCharsets.UTF_8;;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8);
            }

            return buffer.readString(charset);
        } catch (Exception e) {
            return null;
        }
    }

}
