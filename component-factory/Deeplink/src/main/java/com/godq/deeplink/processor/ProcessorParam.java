package com.godq.deeplink.processor;

import com.godq.deeplink.OnLostCallback;
import com.godq.deeplink.intercept.IIntercept;

public class ProcessorParam {

    IIntercept iIntercept;

    OnLostCallback onLostCallback;

    public void setIntercept(IIntercept intercept) {
        this.iIntercept = intercept;
    }

    public void setOnLostCallback(OnLostCallback onLostCallback) {
        this.onLostCallback = onLostCallback;
    }
}
