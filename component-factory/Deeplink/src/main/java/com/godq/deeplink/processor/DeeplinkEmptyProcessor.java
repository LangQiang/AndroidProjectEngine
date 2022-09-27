package com.godq.deeplink.processor;

import android.net.Uri;

import com.godq.deeplink.DeeplinkResult;
import com.godq.deeplink.OnResultCallback;

public class DeeplinkEmptyProcessor implements IProcessor {

    private final Uri uri;

    public DeeplinkEmptyProcessor(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void process(OnResultCallback onResultCallback) {
        if (onResultCallback != null) {
            onResultCallback.onResult(new DeeplinkResult(uri.toString(), DeeplinkResult.ERR_HOST_NOT_SUPPORT, "host not support"));
        }
    }

}
