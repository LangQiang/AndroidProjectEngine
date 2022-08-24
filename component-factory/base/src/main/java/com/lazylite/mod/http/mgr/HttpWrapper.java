package com.lazylite.mod.http.mgr;

import java.util.UUID;

public class HttpWrapper<T> {

    private T callback;

    private boolean isCancel;

    private String traceId = UUID.randomUUID().toString();

    public HttpWrapper(T callback) {
        this.callback = callback;
    }

    public void cancel() {
        if (!(callback instanceof IKwHttpFetcher.DownloadListener)) {
            callback = null;
        }
        isCancel = true;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public T getCallback() {
        return callback;
    }

    public String getTraceId() {
        return traceId;
    }

}
