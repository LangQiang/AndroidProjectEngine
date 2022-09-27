package com.godq.deeplink;

import com.godq.deeplink.intercept.IIntercept;
import com.godq.deeplink.processor.ProcessorParam;

import java.util.ArrayList;
import java.util.List;

public class DeepLinkReq {

    List<IIntercept> globalIntercept = new ArrayList<>();

    String originScheme;

    String extra;

    boolean isFromAppStart;

    ProcessorParam processorParam;

    OnResultCallback onResultCallback;

    DeepLinkReq(String scheme) {
        globalIntercept.addAll(DeepLinkConstants.GLOBAL_INTERCEPT);
        processorParam = new ProcessorParam();
        this.originScheme = scheme;
    }

    public DeepLinkReq withExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public DeepLinkReq isFromAppStart() {
        this.isFromAppStart = true;
        return this;
    }

    public DeepLinkReq setIntercept(IIntercept intercept) {
        processorParam.setIntercept(intercept);
        return this;
    }

    public DeepLinkReq setOnLostCallback(OnLostCallback onLostCallback) {
        processorParam.setOnLostCallback(onLostCallback);
        return this;
    }

    public void execute(OnResultCallback onResultCallback) {
        this.onResultCallback = onResultCallback;
        DeepLinkUtils.dispatchDeepLink(this);
    }

    public void execute() {
        execute(null);
    }

}
