package com.godq.deeplink;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.godq.deeplink.inject.IExecutor;
import com.godq.deeplink.intercept.IIntercept;
import com.godq.deeplink.processor.DeeplinkProcessorFactory;

import timber.log.Timber;

/**
 * 解析新版scheme
 *
 * sample:  tmeatool://open/weex?page=index.js
 *
 * 新增scheme:
 *      open类型参考 WeexPageRouter
 *      ignore：「play类型参考 PlayMusicRouter」
 *
 *      step1: 继承 {@link com.godq.deeplink.route.AbsRouter}，使用@DeepLink注解修饰
 *
 *      step2: 若有耗时操作如网络请求可以重写AbsRouter中的hasBackgroundTask返回true，在runInBackground中来执行耗时操作(非必须)
 *
 *
 */
public class DeepLinkUtils {

    public static void init(@Nullable DeepLinkConfig config) {
        if (config == null) return;
        if (!TextUtils.isEmpty(config.schemeName)) {
            DeepLinkConstants.SCHEME = config.schemeName;
        }
        if (config.iExecutor != null) {
            DeepLinkConstants.iExecutor = config.iExecutor;
        }
    }

    @NonNull
    @UiThread
    public static com.godq.deeplink.DeepLinkReq load(String scheme) {
        Timber.d(scheme);
        return new com.godq.deeplink.DeepLinkReq(scheme);
    }

    @UiThread
    static void dispatchDeepLink(com.godq.deeplink.DeepLinkReq req) {

        //跳转全局捕获异常
        try {
            //补全协议
            String schemeUrl = makeUpScheme(req.originScheme);

            if (TextUtils.isEmpty(schemeUrl)) {
                if (req.onResultCallback != null) {
                    req.onResultCallback.onResult(new DeeplinkResult(req.originScheme, DeeplinkResult.ERR_SCHEME_NULL, "scheme is null"));
                }
                return;
            }

            Uri uri = Uri.parse(schemeUrl);

            buildSchemeWithParam(uri, req.extra, req.isFromAppStart);

            String scheme = uri.getScheme();

            //判断协议头
            if (!com.godq.deeplink.DeepLinkConstants.SCHEME.equals(scheme)) {
                if (req.onResultCallback != null) {
                    req.onResultCallback.onResult(new DeeplinkResult(req.originScheme, DeeplinkResult.ERR_SCHEME_NOT_SUPPORT, "scheme not support"));
                }
                return;
            }

            DeeplinkProcessorFactory.createProcessorByHost(uri, req.processorParam, req.globalIntercept).process(req.onResultCallback);
        } catch (Exception e) {
            if (req.onResultCallback != null) {
                req.onResultCallback.onResult(new DeeplinkResult(req.originScheme, DeeplinkResult.ERR_EXCEPTION, e.getMessage() + ""));
            }
        }
    }


    private static String makeUpScheme(String scheme) {
        if (scheme != null && scheme.startsWith("//")) {
            return com.godq.deeplink.DeepLinkConstants.SCHEME + ":" + scheme;
        } else {
            return scheme;
        }
    }

    /**
     * 将PsrcInfo等信息拼接到scheme中
     * */
    private static void buildSchemeWithParam(@NonNull Uri schemeUri, String extra, boolean fromAppStart) {
        if (extra == null) {
            return ;
        }
        if (schemeUri.getQueryParameter("DL_EXTRA") != null){
            return ;
        }
        schemeUri.buildUpon().appendQueryParameter("DL_EXTRA", extra)
                .appendQueryParameter("fas", String.valueOf(fromAppStart));
    }

    public static void addGlobalIntercept(IIntercept intercept) {
        com.godq.deeplink.DeepLinkConstants.GLOBAL_INTERCEPT.add(intercept);
    }


}
