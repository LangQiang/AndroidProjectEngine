package com.godq.deeplink.route;

import android.net.Uri;

import com.godq.deeplink.DeepLinkConstants;
import com.godq.deeplink.DeeplinkResult;
import com.godq.deeplink.OnLostCallback;
import com.godq.deeplink.OnResultCallback;
import com.godq.deeplink.inject.IExecutor;
import com.lazylite.bridge.router.deeplink.route.RouteMapping;

public class DeeplinkRouteDispatcher {

    private final Uri uri;

    private final DeeplinkResult deeplinkResult;

    private final OnLostCallback onLostCallback;

    private final OnResultCallback onResultCallback;

    private AbsRouter currentRoute;

    public DeeplinkRouteDispatcher(Uri uri, DeeplinkResult deeplinkResult, OnLostCallback onLostCallback, OnResultCallback onResultCallback) {
        this.uri = uri;
        this.deeplinkResult = deeplinkResult;
        this.onLostCallback = onLostCallback;
        this.onResultCallback = onResultCallback;
    }

    public void dispatch() {

        String path = uri.getPath();

        int maxMatchCount = DeepLinkConstants.DEGRADE_MAX_COUNT;

        internalDispatch(uri, path, maxMatchCount, deeplinkResult, onLostCallback, onResultCallback);

    }

    private void internalDispatch(Uri uri,final String path, int count,  DeeplinkResult deeplinkResult, OnLostCallback onLostCallback, OnResultCallback onResultCallback) {
        if (path == null || count == 0) {
            if (onResultCallback != null) {
                onResultCallback.onResult(deeplinkResult);
            }
            return;
        }

        currentRoute = RouteMapping.get(path);
        currentRoute.internalParse(uri);

        if (currentRoute.hasBackgroundTask()) {
            Runnable runnable = () -> {
                currentRoute.runInBackground();
                DeepLinkConstants.handler.post(() -> {
                    try {
                        route(currentRoute, uri, path, count, deeplinkResult, onLostCallback, onResultCallback);
                    } catch (Exception e) {
                        if (onResultCallback != null) {
                            onResultCallback.onResult(new DeeplinkResult(uri == null ? "" : uri.toString(), DeeplinkResult.ERR_EXCEPTION, e.getMessage() + ""));
                        }
                    }
                });

            };
            IExecutor executor = DeepLinkConstants.getExecutor();
            if (executor != null) {
                executor.execute(runnable);
            } else {
                new Thread(runnable).start();
            }
        } else {
            route(currentRoute, uri, path, count, deeplinkResult, onLostCallback, onResultCallback);
        }
    }

    private void route(AbsRouter router, Uri uri, String path, int count, DeeplinkResult deeplinkResult, OnLostCallback onLostCallback, OnResultCallback onResultCallback) {
        if (router.route()) {
            deeplinkResult.code = DeeplinkResult.SUC; //200
            deeplinkResult.finalRoutePath = path;
            deeplinkResult.originScheme = uri.toString();
            if (onResultCallback != null) {
                onResultCallback.onResult(deeplinkResult);
            }
            return;
        }

        if (onLostCallback != null && onLostCallback.onLost(uri)) {
            deeplinkResult.code = DeeplinkResult.LOST_TRUE_SUC; //201
            deeplinkResult.finalRoutePath = path;
            deeplinkResult.originScheme = uri.toString();
            if (onResultCallback != null) {
                onResultCallback.onResult(deeplinkResult);
            }
        } else {
            int lastIndex = path.lastIndexOf("/");

            String tempPath;
            if (lastIndex < 0) {
                tempPath = null;
            } else {
                tempPath = path.substring(0, lastIndex);
            }


            deeplinkResult.isDegrade = true;
            internalDispatch(uri, tempPath, count - 1, deeplinkResult, null, onResultCallback);
        }
    }

    public void terminate() {
        if (currentRoute != null) {
            currentRoute.isRunning = false;
        }
    }

    public boolean isRunning() {
        return currentRoute != null && currentRoute.isRunning;
    }
}
