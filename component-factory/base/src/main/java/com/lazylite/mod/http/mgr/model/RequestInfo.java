package com.lazylite.mod.http.mgr.model;

import android.os.Handler;

import java.util.Map;

public class RequestInfo implements IRequestInfo {

    private String url;

    private byte[] body;

    private Map<String, String> headers;

    private Handler handler;

    public RequestInfo(String url, byte[] body, Map<String, String> headers, Handler handler) {
        this.url = url;
        this.body = body;
        this.headers = headers;
        this.handler = handler;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

//    public static class Builder {
//        private String url;
//
//        private byte[] body;
//
//        private Map<String, String> headers;
//
//        private Handler handler;
//
//        public Builder setUrl(String url) {
//            this.url = url;
//            return this;
//        }
//
//        public Builder setBody(byte[] body) {
//            this.body = body;
//            return this;
//        }
//
//        public Builder setHeaders(Map<String, String> headers) {
//            this.headers = headers;
//            return this;
//        }
//
//        public Builder setHandler(Handler handler) {
//            this.handler = handler;
//            return this;
//        }
//
//        public RequestInfo build() {
//            return new RequestInfo(url, body, headers, handler);
//        }
//
//    }

    public static RequestInfo newGet(String url) {
        return new RequestInfo(url, null, null, null);
    }

    public static RequestInfo newPost(String url, byte[] body) {
        return new RequestInfo(url, body, null, null);
    }
    public static RequestInfo newPost(String url, Map<String, String> headers, byte[] body) {
        return new RequestInfo(url, body, headers, null);
    }
}
