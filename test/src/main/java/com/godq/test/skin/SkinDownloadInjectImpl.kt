package com.godq.test.skin

import com.godq.xskin.inject.IDownloadInject
import com.lazylite.mod.http.mgr.HttpWrapper
import com.lazylite.mod.http.mgr.IKwHttpFetcher
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.DownReqInfo


/**
 * @author  GodQ
 * @date  2023/6/6 11:31 AM
 */
class SkinDownloadInjectImpl: IDownloadInject {

    override fun asyncDownload(url: String, savePath: String, downloadCallback: IDownloadInject.DownloadCallback): IDownloadInject.IHttpWrapper {
        val downReqInfo = DownReqInfo(url, savePath, 0)
        val httpWrapper = KwHttpMgr.getInstance().kwHttpFetch.asyncDownload(downReqInfo, null, object : IKwHttpFetcher.DownloadListener {
            override fun onComplete(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>) {
                downloadCallback.onComplete()
            }

            override fun onError(errorCode: Int, msg: String, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>) {
                downloadCallback.onError(errorCode, msg)
            }

            override fun onStart(startPos: Long, totalLength: Long, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>) {
                downloadCallback.onStart(startPos, totalLength)
            }

            override fun onProgress(currentPos: Long, totalLength: Long, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>) {
                downloadCallback.onProgress(currentPos, totalLength)
            }

            override fun onCancel(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>) {
                downloadCallback.onCancel()
            }
        })
        return object : IDownloadInject.IHttpWrapper {
            override fun cancel() {
                httpWrapper.cancel()
            }
        }
    }
}