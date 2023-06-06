package com.godq.xskin.inject

import timber.log.Timber

interface IDownloadInject {
    fun asyncDownload(url: String, savePath: String, downloadCallback: DownloadCallback): IHttpWrapper?

    interface DownloadCallback {
        fun onComplete() {}
        fun onError(errorCode: Int, msg: String?) {
            Timber.tag("SkinManager").e("download error code:$errorCode msg:$msg")
        }
        fun onStart(startPos: Long, totalLength: Long) {}
        fun onProgress(currentPos: Long, totalLength: Long) {}
        fun onCancel() {}
    }

    interface IHttpWrapper {
        fun cancel()
    }
}