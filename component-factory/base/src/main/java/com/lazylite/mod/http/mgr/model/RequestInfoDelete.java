package com.lazylite.mod.http.mgr.model;

import android.os.Handler;

import java.util.Map;

/**
 * Created by lzf on 2022/1/14 3:35 下午
 */
public class RequestInfoDelete extends RequestInfo{
    public RequestInfoDelete(String url, byte[] body, Map<String, String> headers, Handler handler) {
        super(url, body, headers, handler);
    }
}
