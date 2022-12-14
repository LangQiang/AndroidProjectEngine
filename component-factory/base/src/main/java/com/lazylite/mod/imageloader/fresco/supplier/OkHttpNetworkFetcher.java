package com.lazylite.mod.imageloader.fresco.supplier;

import android.net.Uri;
import android.os.Looper;
import android.os.SystemClock;

import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.lazylite.mod.receiver.network.NetworkStateUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by tiancheng on 2016/11/14
 */
public class OkHttpNetworkFetcher extends BaseNetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

    static class OkHttpNetworkFetchState extends FetchState {
        long submitTime;
        long responseTime;
        long fetchCompleteTime;

        OkHttpNetworkFetchState(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
            super(consumer, producerContext);
        }
    }

    private static final String TAG = "OkHttpNetworkFetchProducer";
    private static final String QUEUE_TIME = "queue_time";
    private static final String FETCH_TIME = "fetch_time";
    private static final String TOTAL_TIME = "total_time";
    private static final String IMAGE_SIZE = "image_size";
    private static OkHttpClient mOkHttpClient;
    private Executor mCancellationExecutor;

    public OkHttpNetworkFetcher() {
        buildOkHttpClient();
        mCancellationExecutor = mOkHttpClient.dispatcher().executorService();
    }

    public static void buildOkHttpClient() {
        // 业务代理，流量包啥的
        mOkHttpClient = new OkHttpClient.Builder()
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    @Override
    public OkHttpNetworkFetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext context) {
        return new OkHttpNetworkFetchState(consumer, context);
    }

    @Override
    public void fetch(final OkHttpNetworkFetchState fetchState, final Callback callback) {
        if (!NetworkStateUtil.isOnlyWifiConnect() && NetworkStateUtil.isAvailable()) {
            fetchState.submitTime = SystemClock.elapsedRealtime();
            final Uri uri = fetchState.getUri();

            final Request.Builder request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder().noStore().build())
                    .url(uri.toString())
                    .get();

            final Call call = mOkHttpClient.newCall(request.build());

            fetchState.getContext().addCallbacks(new BaseProducerContextCallbacks() {

                @Override
                public void onCancellationRequested() {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        call.cancel();
                    } else {
                        mCancellationExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                call.cancel();
                            }
                        });
                    }
                }
            });

            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    fetchState.responseTime = SystemClock.elapsedRealtime();
                    final ResponseBody body = response.body();
                    try {
                        if (!response.isSuccessful()) {
                            handleException(
                                    call,
                                    new IOException("Unexpected HTTP code " + response),
                                    callback);
                            return;
                        }

                        if (body == null) {
                            handleException(
                                    call,
                                    new IOException("body == null " + response),
                                    callback);
                            return;
                        }

                        long contentLength = body.contentLength();
                        if (contentLength < 0) {
                            contentLength = 0;
                        }
                        callback.onResponse(body.byteStream(), (int) contentLength);
                    } catch (Exception e) {
                        handleException(call, e, callback);
                    } finally {
                        try {
                            if (body != null) {
                                body.close();
                            }
                        } catch (Exception e) {
                            FLog.w(TAG, "Exception when closing response body", e);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    handleException(call, e, callback);
                }
            });
        } else {
            callback.onFailure(new RuntimeException(String.format("Image URL %s fetch failed Network is not Avaliable",
                    fetchState.getUri().toString())));
        }

    }

    @Override
    public void onFetchCompletion(OkHttpNetworkFetchState fetchState, int byteSize) {
        fetchState.fetchCompleteTime = SystemClock.elapsedRealtime();
    }

    @Override
    public Map<String, String> getExtraMap(OkHttpNetworkFetchState fetchState, int byteSize) {
        Map<String, String> extraMap = new HashMap<>(4);
        extraMap.put(QUEUE_TIME, Long.toString(fetchState.responseTime - fetchState.submitTime));
        extraMap.put(FETCH_TIME, Long.toString(fetchState.fetchCompleteTime - fetchState.responseTime));
        extraMap.put(TOTAL_TIME, Long.toString(fetchState.fetchCompleteTime - fetchState.submitTime));
        extraMap.put(IMAGE_SIZE, Integer.toString(byteSize));
        return extraMap;
    }

    /**
     * Handles exceptions.
     *
     * <p> OkHttp notifies callers of cancellations via an IOException. If IOException is caught
     * after request cancellation, then the exception is interpreted as successful cancellation
     * and onCancellation is called. Otherwise onFailure is called.
     */
    private void handleException(final Call call, final Exception e, final Callback callback) {
        if (call.isCanceled()) {
            callback.onCancellation();
        } else {
            callback.onFailure(e);
        }
    }
}
