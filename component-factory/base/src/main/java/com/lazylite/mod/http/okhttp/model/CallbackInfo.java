package com.lazylite.mod.http.okhttp.model;

import android.os.Handler;

import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.model.IDownloadInfo;

public class CallbackInfo {

    public int code;

    public long startPos;

    public long currentPos;

    public long totalLength;

    public String msg;

    public IDownloadInfo iDownloadInfo;

    public Handler handler;

    public HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper;

    public CallbackInfo(IDownloadInfo iDownloadInfo, Handler handler, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
        this.iDownloadInfo = iDownloadInfo;
        this.handler = handler;
        this.httpWrapper = httpWrapper;
    }

}
