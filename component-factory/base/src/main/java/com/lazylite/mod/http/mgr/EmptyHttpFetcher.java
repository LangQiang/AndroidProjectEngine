package com.lazylite.mod.http.mgr;

import android.os.Handler;

import com.lazylite.mod.http.mgr.model.IDownloadInfo;
import com.lazylite.mod.http.mgr.model.IRequestInfo;
import com.lazylite.mod.http.mgr.model.IResponseInfo;


public class EmptyHttpFetcher implements IKwHttpFetcher {

    @Override
    public IResponseInfo get(IRequestInfo requestInfo) {
        return null;
    }

    @Override
    public IResponseInfo post(IRequestInfo requestInfo) {
        return null;
    }

    @Override
    public HttpWrapper<FetchCallback> asyncGet(IRequestInfo requestInfo, FetchCallback fetchCallback) {
        return new HttpWrapper<>(fetchCallback);
    }

    @Override
    public HttpWrapper<FetchCallback> asyncPost(IRequestInfo requestInfo, FetchCallback fetchCallback) {
        return new HttpWrapper<>(fetchCallback);
    }

    @Override
    public void download(IDownloadInfo downloadInfo, DownloadListener downloadListener) {
        if (downloadListener != null) {
            downloadListener.onError(-1, "empty download impl", new HttpWrapper<>(downloadListener));
        }
    }

    @Override
    public HttpWrapper<DownloadListener> asyncDownload(IDownloadInfo iDownloadInfo, Handler handler, DownloadListener downloadListener) {
        HttpWrapper<DownloadListener> downloadListenerHttpWrapper = new HttpWrapper<>(downloadListener);
        if (downloadListener != null) {
            downloadListener.onError(-1, "empty download impl", downloadListenerHttpWrapper);
        }
        return downloadListenerHttpWrapper;
    }

    @Override
    public String getFrameName() {
        return "empty http fetcher";
    }
}
