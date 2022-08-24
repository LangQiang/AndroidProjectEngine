package com.lazylite.mod.http.mgr;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.lazylite.mod.http.mgr.model.IDownloadInfo;
import com.lazylite.mod.http.mgr.model.IRequestInfo;
import com.lazylite.mod.http.mgr.model.IResponseInfo;


public interface IKwHttpFetcher {

    IResponseInfo get(IRequestInfo requestInfo);

    IResponseInfo post(IRequestInfo requestInfo);

    HttpWrapper<FetchCallback> asyncGet(IRequestInfo requestInfo, FetchCallback fetchCallback);

    HttpWrapper<FetchCallback> asyncPost(IRequestInfo requestInfo, FetchCallback fetchCallback);

    /**
     * 这个方法和回调都在workThread中 注意
     *
     * 同步下载也提供downloadListener 方便在下载中的各个阶段执行其他代码逻辑
     * */
    void download(IDownloadInfo iDownloadInfo, DownloadListener downloadListener);

    HttpWrapper<DownloadListener> asyncDownload(IDownloadInfo iDownloadInfo, Handler handler, DownloadListener downloadListener);

    String getFrameName();

    interface FetchCallback {
        void onFetch(@NonNull IResponseInfo responseInfo);
    }

    interface DownloadListener {

        default void onComplete(HttpWrapper<DownloadListener> httpWrapper){}

        default void onError(int errorCode, String msg, HttpWrapper<DownloadListener> httpWrapper){}

        default void onStart(long startPos, long totalLength, HttpWrapper<DownloadListener> httpWrapper){}

        default void onProgress(long currentPos, long totalLength, HttpWrapper<DownloadListener> httpWrapper){}

        default void onCancel(HttpWrapper<DownloadListener> httpWrapper){}
    }
}
