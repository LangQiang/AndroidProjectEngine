package com.godq.deeplink.processor;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.godq.deeplink.DeepLinkConstants;
import com.godq.deeplink.intercept.IIntercept;

import java.util.List;

public class DeeplinkProcessorFactory {
    private static IPlayProcessor sRecentProcessor;

    private static IPlayProcessor createPlayProcessor(Uri uri, ProcessorParam processorParam, List<IIntercept> globalIntercept) {
        if (sRecentProcessor != null && sRecentProcessor.isRunning()) {
            sRecentProcessor.terminate();
        }
        sRecentProcessor = new DeeplinkPlayProcessor(uri, processorParam, globalIntercept);
        return sRecentProcessor;
    }

    private static IProcessor createOpenProcessor(Uri uri, ProcessorParam processorParam, List<IIntercept> globalIntercept) {
        return new DeeplinkOpenProcessor(uri, processorParam, globalIntercept);
    }

    @NonNull
    public static IProcessor createProcessorByHost(Uri uri, ProcessorParam processorParam, List<IIntercept> globalIntercept) {
        String host = uri.getHost();
        if (DeepLinkConstants.HOST_OPEN.equals(host)) {
            return createOpenProcessor(uri, processorParam, globalIntercept);
        } else if (DeepLinkConstants.HOST_PLAY.equals(host)) {
            return createPlayProcessor(uri, processorParam, globalIntercept);
        } else {
            return new DeeplinkEmptyProcessor(uri);
        }
    }
}
