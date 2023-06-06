package com.godq.xskin.inject

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.godq.xskin.inject.IDownloadInject.IHttpWrapper
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * @author  GodQ
 * @date  2023/6/6 10:53 AM
 */
class DownloadInjectImpl: IDownloadInject {

    override fun asyncDownload(url: String, savePath: String, downloadCallback: IDownloadInject.DownloadCallback): IHttpWrapper {
        DownloadTask(url, savePath, downloadCallback).apply {
            start()
            return object : IHttpWrapper {
                override fun cancel() {
                    this@apply.cancel()
                }
            }
        }
    }

    class DownloadTask(private val url: String, private val savePath: String, private val listener: IDownloadInject.DownloadCallback) {

        private val handler = Handler(Looper.getMainLooper())

        private var isCancelled = false

        fun cancel() {
            isCancelled = true
        }

        fun start() {
            Thread {
                var connection: HttpURLConnection? = null
                var inputStream: BufferedInputStream? = null
                var outputStream: FileOutputStream? = null

                try {
                    val url = URL(url)
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val fileLength = connection.contentLength.toLong()
                        inputStream = BufferedInputStream(connection.inputStream)
                        outputStream = FileOutputStream(savePath)

                        val buffer = ByteArray(1024)
                        var totalBytesRead = 0L
                        var bytesRead: Int = -1
                        var lastProgress = 0L


                        while (!isCancelled && (inputStream.read(buffer).also { bytesRead = it } != -1)) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            val progress = (totalBytesRead * 100 / fileLength)
                            if (progress - lastProgress >= 1) {
                                lastProgress = progress
                                val current = totalBytesRead
                                handler.post {
                                    listener.onProgress(current, fileLength)
                                }
                                SystemClock.sleep(1)
                            }

                            // Check if download is cancelled
                            if (isCancelled) {
                                handler.post {
                                    listener.onCancel()
                                }
                                return@Thread
                            }
                        }

                        handler.post {
                            listener.onComplete()
                        }
                    } else {
                        handler.post {
                            listener.onError(connection.responseCode, "Server returned HTTP ${connection.responseCode}")
                        }
                    }
                } catch (e: Exception) {
                    handler.post {
                        listener.onError(-1,e.message ?: "Unknown error occurred")
                    }
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                    connection?.disconnect()
                }
            }.start()
        }
    }
}