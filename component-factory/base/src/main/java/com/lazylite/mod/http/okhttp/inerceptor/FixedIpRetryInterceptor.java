package com.lazylite.mod.http.okhttp.inerceptor;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FixedIpRetryInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Response response = null;

        IOException ioException = null;

        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            ioException = e;
        }

        if ((response == null || !response.isSuccessful()) && request.isHttps()) {
            String host = request.url().host();
            String ip = getIp(host);
            if (ip != null && ip.length() > 0) {
                HttpUrl newUrl = request.url().newBuilder().host(ip).build();
                try {
                    response = chain.proceed(request.newBuilder().url(newUrl)
                            .header("host", host)
                            .build());
                } catch (IOException ioe) {
                    ioException = ioe;
                }
            }
        }

        if (response == null) {
            if (ioException != null) {
                throw ioException;
            } else {
                throw new IOException("fixedRetry failed");
            }
        }
        return response;
    }

    private String getIp(String host) {
        return null;
    }
}
