package com.godq.deeplink.processor;

import android.net.Uri;

import com.godq.deeplink.DeeplinkResult;
import com.godq.deeplink.OnResultCallback;
import com.godq.deeplink.intercept.IIntercept;
import com.godq.deeplink.route.DeeplinkRouteDispatcher;

import java.util.List;

public class DeeplinkPlayProcessor implements IPlayProcessor {

    private DeeplinkRouteDispatcher deeplinkRouteDispatcher;

    private final ProcessorParam processorParam;

    private final List<IIntercept> globalIntercept;

    private Uri uri;

    DeeplinkPlayProcessor(Uri uri, ProcessorParam processorParam, List<IIntercept> globalIntercept) {
        this.uri = uri;
        this.processorParam = processorParam;
        this.globalIntercept = globalIntercept;
    }


    @Override
    public void process(OnResultCallback onResultCallback) {

        //beforeRoute intercept

        //global
        if (globalIntercept != null) {
            for (IIntercept intercept : globalIntercept) {
                uri = intercept.beforeRoute(uri);
            }
        }


        //single
        if (processorParam.iIntercept != null) {
            uri = processorParam.iIntercept.beforeRoute(uri);
        }

        DeeplinkResult result = new DeeplinkResult();
        result.type = DeeplinkResult.TYPE_PLAY;

        deeplinkRouteDispatcher = new DeeplinkRouteDispatcher(uri, result, processorParam.onLostCallback, deeplinkResult -> {

            //afterRoute intercept

            if (processorParam.iIntercept != null) {
                deeplinkResult = processorParam.iIntercept.afterRoute(deeplinkResult, uri);
            }

            //global
            if (globalIntercept != null) {
                for (IIntercept intercept : globalIntercept) {
                    deeplinkResult = intercept.afterRoute(deeplinkResult, uri);
                }
            }
            if (onResultCallback != null) {
                onResultCallback.onResult(deeplinkResult);
            }
        });
        deeplinkRouteDispatcher.dispatch();

    }

    @Override
    public void terminate() {
        if (deeplinkRouteDispatcher != null) {
            deeplinkRouteDispatcher.terminate();
        }
    }

    @Override
    public boolean isRunning() {
        return deeplinkRouteDispatcher != null && deeplinkRouteDispatcher.isRunning();
    }

}
