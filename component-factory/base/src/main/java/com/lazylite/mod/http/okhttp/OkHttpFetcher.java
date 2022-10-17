package com.lazylite.mod.http.okhttp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;


import com.godq.threadpool.ThreadPool;
import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IHttpResultCheckPolicy;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpConfig;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.CommonParam;
import com.lazylite.mod.http.mgr.model.IDownloadInfo;
import com.lazylite.mod.http.mgr.model.IRequestInfo;
import com.lazylite.mod.http.mgr.model.IResponseInfo;
import com.lazylite.mod.http.mgr.model.RequestInfoDelete;
import com.lazylite.mod.http.okhttp.model.CallbackInfo;
import com.lazylite.mod.http.okhttp.model.OkResponseInfo;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.LRSign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpFetcher implements IKwHttpFetcher {

    private final OkHttpClient okHttpClient;

    private final Handler defaultMainHandler;

    private final KwHttpConfig kwHttpConfig;

    OkHttpFetcher(OkHttpClient okHttpClient, KwHttpConfig kwHttpConfig) {
        this.okHttpClient = okHttpClient;
        this.defaultMainHandler = kwHttpConfig.getHandler();
        this.kwHttpConfig = kwHttpConfig;
    }

    @Override
    public IResponseInfo get(IRequestInfo requestInfo) {

        OkResponseInfo responseInfo = new OkResponseInfo();

        try {
            Request request = buildRequest(requestInfo, "get", responseInfo);

            try {
                Response response = okHttpClient.newCall(request).execute();
                assignmentResult(responseInfo, response);
                checkResultByPolicy(responseInfo);
            } catch (IOException ioe) {
                responseInfo.code = OkHttpConstants.CODE_IO_EXCEPTION; //701
                responseInfo.errorMsg = "okHttp IOException";
            } catch (Exception e) {
                responseInfo.code = OkHttpConstants.CODE_EXCEPTION; //702
                responseInfo.errorMsg = null != e.getLocalizedMessage() ?e.getLocalizedMessage(): "okHttp IOException";
            }
        } catch (Exception e) {
            responseInfo.code = OkHttpConstants.CODE_HTTP_URL_BUILD_ERROR; //705
            responseInfo.errorMsg = "okHttp IOException";
        }


        return responseInfo;
    }

    @Override
    public IResponseInfo post(IRequestInfo requestInfo) {

        OkResponseInfo responseInfo = new OkResponseInfo();

        try {
            Request request = buildRequest(requestInfo, "post", responseInfo);

            try {
                Response response = okHttpClient.newCall(request).execute();
                assignmentResult(responseInfo, response);
                checkResultByPolicy(responseInfo);
            } catch (IOException ioe) {
                responseInfo.code = OkHttpConstants.CODE_IO_EXCEPTION; //701
                responseInfo.errorMsg = "okHttp IOException";
            } catch (Exception e) {
                responseInfo.code = OkHttpConstants.CODE_EXCEPTION; //702
                responseInfo.errorMsg = "okHttp Exception";
            }
        } catch (Exception e) {
            responseInfo.code = OkHttpConstants.CODE_HTTP_URL_BUILD_ERROR; //705
            responseInfo.errorMsg = "okHttp Exception";
        }

        return responseInfo;
    }

    @Override
    public HttpWrapper<FetchCallback> asyncGet(final IRequestInfo requestInfo, final FetchCallback fetchCallback) {

        final HttpWrapper<FetchCallback> httpWrapper = new HttpWrapper<>(fetchCallback);

        final OkResponseInfo responseInfo = new OkResponseInfo();

        final Handler finalHandler = requestInfo.getHandler() == null ? defaultMainHandler : requestInfo.getHandler();

        try {
            Request request = buildRequest(requestInfo, "get", responseInfo);

            okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e
                ) {
                    responseInfo.code = OkHttpConstants.CODE_FAILURE; // 703
                    responseInfo.errorMsg = "okHttp IOException" + e;
                    postFetchCallback(finalHandler, responseInfo, httpWrapper);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (fetchCallback != null) {
                        assignmentResult(responseInfo, response);
                        checkResultByPolicy(responseInfo);
                        postFetchCallback(finalHandler, responseInfo, httpWrapper);
                    }
                }
            });
        } catch (Exception e) {
            responseInfo.code = OkHttpConstants.CODE_HTTP_URL_BUILD_ERROR; // 705
            responseInfo.errorMsg = "okHttp IOException" + e;
            postFetchCallback(finalHandler, responseInfo, httpWrapper);
        }


        return httpWrapper;
    }

    @Override
    public HttpWrapper<FetchCallback> asyncPost(IRequestInfo requestInfo, final FetchCallback fetchCallback) {

        final HttpWrapper<FetchCallback> httpWrapper = new HttpWrapper<>(fetchCallback);

        final OkResponseInfo responseInfo = new OkResponseInfo();

        final Handler finalHandler = requestInfo.getHandler() == null ? defaultMainHandler : requestInfo.getHandler();

        try {
            Request request = buildRequest(requestInfo, "post", responseInfo);

            okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    responseInfo.code = OkHttpConstants.CODE_FAILURE; // 703
                    responseInfo.errorMsg = "okHttp IOException";

                    postFetchCallback(finalHandler, responseInfo, httpWrapper);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (fetchCallback != null) {
                        assignmentResult(responseInfo, response);
                        checkResultByPolicy(responseInfo);
                        postFetchCallback(finalHandler, responseInfo, httpWrapper);
                    }
                }
            });
        } catch (Exception e) {
            responseInfo.code = OkHttpConstants.CODE_HTTP_URL_BUILD_ERROR; // 705
            responseInfo.errorMsg = "okHttp IOException" + e;
            postFetchCallback(finalHandler, responseInfo, httpWrapper);
        }

        return httpWrapper;
    }

    private void postFetchCallback(Handler callbackHandler, final OkResponseInfo responseInfo, final HttpWrapper<FetchCallback> httpWrapper) {
        if (httpWrapper == null) {
            return;
        }

        if (callbackHandler != null && callbackHandler.getLooper().getThread() != Thread.currentThread()) {
            callbackHandler.post(() -> {
                if (httpWrapper.getCallback() != null) {
                    httpWrapper.getCallback().onFetch(responseInfo);
                }
            });
        } else {
            if (httpWrapper.getCallback() != null) {
                httpWrapper.getCallback().onFetch(responseInfo);
            }
        }
    }

    @Override
    public void download(final IDownloadInfo iDownloadInfo, final DownloadListener downloadListener) {
        innerDownload(iDownloadInfo, null, new HttpWrapper<>(downloadListener));
    }

    @Override
    public HttpWrapper<DownloadListener> asyncDownload(final IDownloadInfo iDownloadInfo, final Handler handler, final DownloadListener downloadListener) {
        final HttpWrapper<DownloadListener> httpWrapper = new HttpWrapper<>(downloadListener);
        ThreadPool.exec(() -> innerDownload(iDownloadInfo, handler == null ? defaultMainHandler : handler, httpWrapper));
        return httpWrapper;
    }

    private void innerDownload(final IDownloadInfo iDownloadInfo, Handler handler, HttpWrapper<DownloadListener> httpWrapper) {

        final CallbackInfo callbackInfo = new CallbackInfo(iDownloadInfo, handler, httpWrapper);

        if (TextUtils.isEmpty(iDownloadInfo.getUrl())) {
            callbackInfo.msg = "Url is null";
            callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_PARAM_ERROR;
            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
            return;
        }

        if (TextUtils.isEmpty(iDownloadInfo.getPath())) {
            callbackInfo.msg = "cacheFilePath is null";
            callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_PARAM_ERROR;
            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
            return;
        }

        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;

        long startPos;
        final long totalLength;

        try {
            startPos = Math.min(iDownloadInfo.getStartPos(),
                    OkUtils.getFileLength(iDownloadInfo.getPath()));

            File saveFile =  new File(iDownloadInfo.getPath());
            randomAccessFile = new RandomAccessFile(saveFile, "rwd");

            if (startPos > saveFile.length()) {
                startPos = 0;
                randomAccessFile.seek(0);
            } else {
                randomAccessFile.seek(startPos);
            }

            Request request = new Request.Builder()
                    .url(iDownloadInfo.getUrl())
                    .header("RANGE", "bytes=" + startPos + "-")
                    .header("Accept-Encoding", "identity")
                    .build();

            Response execute = okHttpClient.newCall(request).execute();
            ResponseBody body = execute.body();

            if (body != null) {
                totalLength = body.contentLength() + startPos;

                callbackInfo.startPos = startPos;
                callbackInfo.totalLength = totalLength;
                postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_START, callbackInfo);

                if (body instanceof ProgressResponseBody) {
                    final long finalStartPos = startPos;
                    ((ProgressResponseBody) body).setProgressListener((alreadyRead, contentLength, done) -> {

                        callbackInfo.currentPos = alreadyRead + finalStartPos;
                        callbackInfo.totalLength = totalLength;
                        postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_PROGRESS, callbackInfo);

                        if (KwHttpMgr.isDebug()) {
                            LogMgr.i(OkHttpConstants.TAG, String.format(Locale.CHINA,"bytesRead: %d  contentLength: %d  down: %s", alreadyRead, contentLength, done + ""));
                        }
                    });
                }

                inputStream = body.byteStream();
                byte[] bytes = new byte[1024 * 64];
                int len;
                while (true) {
                    if (httpWrapper != null && httpWrapper.isCancel()) {
                        break;
                    }
                    try {
                        len = inputStream.read(bytes);
                        if (len == -1) {
                            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_COMPLETE, callbackInfo);
                            break;
                        } else {
                            randomAccessFile.write(bytes, 0, len);
                        }
                    } catch (OutOfMemoryError error) {
                        callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_OOM_ERROR;
                        callbackInfo.msg = error.getMessage();
                        postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
                    }
                }
            } else {
                callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_EMPTY_BODY;
                callbackInfo.msg = "body is null";
                postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
            }


        } catch (FileNotFoundException fe) {
            callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_FILE_NOT_FOUND;
            callbackInfo.msg = fe.getMessage() + "";
            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
        } catch (IOException ex) {
            callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_FILE_IO_ERROR;
            callbackInfo.msg = ex.getMessage();
            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
        } catch (Exception e) {
            callbackInfo.code = OkHttpConstants.DOWNLOAD_CODE_EXCEPTION_ERROR;
            callbackInfo.msg = e.getMessage() + "";
            postDownCallback(OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR, callbackInfo);
        }finally {
            OkUtils.closeQuietly(randomAccessFile);
            OkUtils.closeQuietly(inputStream);
        }
    }

    private void postDownCallback(@OkHttpConstants.CALLBACK_TYPE final int callbackType, @NonNull final CallbackInfo callbackInfo) {

        if (callbackInfo.httpWrapper.getCallback() == null) {
            return;
        }

        if (callbackInfo.handler != null && callbackInfo.handler.getLooper().getThread() != Thread.currentThread()) {
            callbackInfo.handler.post(() -> postDownCallbackInCorrectThread(callbackType, callbackInfo));
        } else {
            postDownCallbackInCorrectThread(callbackType, callbackInfo);
        }
    }

    @SuppressLint("SwitchIntDef")
    private void postDownCallbackInCorrectThread(@OkHttpConstants.CALLBACK_TYPE int callbackType, @NonNull CallbackInfo callbackInfo) {

        DownloadListener callback = callbackInfo.httpWrapper.getCallback();
        if (callback == null) {
            return;
        }

        if (callbackInfo.httpWrapper.isCancel()) {
            callback.onCancel(callbackInfo.httpWrapper);
            return;
        }

        if (callbackType == OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_PROGRESS) {
            callback.onProgress(callbackInfo.currentPos, callbackInfo.totalLength, callbackInfo.httpWrapper);
            return;
        }

        switch (callbackType) {
            case OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_COMPLETE:
                callback.onComplete(callbackInfo.httpWrapper);
                break;
            case OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_ERROR:
                callback.onError(callbackInfo.code, callbackInfo.msg, callbackInfo.httpWrapper);
                break;
            case OkHttpConstants.CALLBACK_TYPE.CALLBACK_TYPE_START:
                callback.onStart(callbackInfo.startPos, callbackInfo.totalLength, callbackInfo.httpWrapper);
                break;
        }
    }

    @Override
    public String getFrameName() {
        return OkHttpConstants.FRAME_NAME;
    }


    private Request buildRequest(IRequestInfo httpSession, String method, OkResponseInfo responseInfo) {

        Request.Builder builder = new Request.Builder();
        final String finalUrl = addCommParams(httpSession.getUrl(), responseInfo);
        builder.url(finalUrl);
        Headers headers = mergeHeaders(finalUrl, httpSession.getHeaders(), responseInfo);
        builder.headers(headers);

        if ("post".equals(method)) {
            String contentType = headers.get("Content-Type");
            if (contentType == null) {
            //application/x-www-form-urlencoded 默认
                contentType = "application/x-www-form-urlencoded";
            }
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse(contentType), httpSession.getBody() == null ? new byte[0] : httpSession.getBody());
            builder.post(requestBody);
        }

        if(httpSession instanceof RequestInfoDelete){
            builder.delete();
        }

        responseInfo.finalRequestUrl = finalUrl;

        return builder.build();
    }

    private String addCommParams(String url, OkResponseInfo responseInfo) {
        try {
            Uri uri = Uri.parse(url);
            Uri.Builder builder = uri.buildUpon();
            CommonParam commonParam = kwHttpConfig.getCommonQueryParams();
            Map<String, String> params = commonParam.getParams();
            responseInfo.requestParamOperatorPath = commonParam.getOperatorPath();
            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                builder.appendQueryParameter(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            return builder.build().toString();
        } catch (Exception e) {
            return url;
        }
    }

    @NonNull
    private Headers mergeHeaders(String finalUrl, Map<String, String> onceHeaders, OkResponseInfo responseInfo) {

        CommonParam commonParam = kwHttpConfig.getCommonHeaders();

        //global
        Map<String, String> finalHeaders = new HashMap<>(commonParam.getParams());
        responseInfo.requestHeaderOperatorPath = commonParam.getOperatorPath();

        //once
        if (onceHeaders != null) {
            finalHeaders.putAll(onceHeaders);
        }
        //sign final put
        finalHeaders.put("X-AUTH-SIGN", LRSign.sign(finalUrl, null));
        return Headers.of(finalHeaders);
    }

    private void assignmentResult(@NonNull OkResponseInfo responseInfo, Response response) {
        try {
            responseInfo.code = response.code();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
//                    String data = body.string();
                    try {
                        responseInfo.data = body.bytes();
                        responseInfo.responseHeaders = response.headers().toMultimap();
                    } catch (OutOfMemoryError error) {
                        responseInfo.code = OkHttpConstants.CODE_OOM;  //704
                        responseInfo.errorMsg = "oom error";
                    }
                } else {
                    responseInfo.code = OkHttpConstants.CODE_EMPTY_BODY;  //700
                    responseInfo.errorMsg = "okHttp body null";
                }
            } else {
                responseInfo.errorMsg = "okHttp response code error ";
            }
        } catch (IOException ioe) {
            responseInfo.code = OkHttpConstants.CODE_IO_EXCEPTION; //701
            responseInfo.errorMsg = "okHttp IOException";
        } catch (Exception e) {
            responseInfo.code = OkHttpConstants.CODE_EXCEPTION; //702
            responseInfo.errorMsg = "okHttp Exception";
        }
    }

    private void checkResultByPolicy(OkResponseInfo responseInfo){
        List<IHttpResultCheckPolicy> resultCheckPolicy = kwHttpConfig.getResultCheckPolicies();
        try {
            for (int i = resultCheckPolicy.size() - 1; i >= 0; i--) {
                IHttpResultCheckPolicy policy = resultCheckPolicy.get(i);
                if(null != policy && policy.isCanUse(responseInfo)){
                    policy.onResult(responseInfo);
                }
            }
        }catch (Exception ignore){}
    }
}
