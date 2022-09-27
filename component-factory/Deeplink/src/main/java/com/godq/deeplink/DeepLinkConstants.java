package com.godq.deeplink;

import android.os.Handler;
import android.os.Looper;

import com.godq.deeplink.inject.IExecutor;
import com.godq.deeplink.intercept.IIntercept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeepLinkConstants {

    public static Handler handler = new Handler(Looper.getMainLooper());

    static IExecutor iExecutor = null;

    public static IExecutor getExecutor() {
        return iExecutor;
    }

    static final List<IIntercept> GLOBAL_INTERCEPT = Collections.synchronizedList(new ArrayList<>());

    public static final int DEGRADE_MAX_COUNT = 5;

    static String SCHEME = "metarare";

    public static final String HOST_PLAY = "play";

    public static final String HOST_OPEN = "open";



}
