package com.godq.deeplink;

import android.net.Uri;
import android.text.TextUtils;


public class DeepLinkLog {
    private static final String INVOKER = "invoker";

    private static final String INVOKER_PUSH = "push";
    public static final String INVOKER_PUSH_PARAM_ID = "push_id";
    public static final String INVOKER_PUSH_PARAM_TRANS = "push_trans"; //用于客户端标记通知消息是否是透传弹出
    public static void sendLog(Uri scheme) {
        if (scheme == null) {
            return;
        }
        String invoker = scheme.getQueryParameter(INVOKER);
        if (TextUtils.isEmpty(invoker)) {
            return;
        }
        if (INVOKER_PUSH.equals(invoker)) {
            String pushId = scheme.getQueryParameter(INVOKER_PUSH_PARAM_ID);

            String pushTrans = scheme.getQueryParameter(INVOKER_PUSH_PARAM_TRANS);
//            TsPushManager.getInstance().sendLog(pushId, NtsLog.PUSH_EVENT_CLICK, pushTrans);
        }
    }
}
