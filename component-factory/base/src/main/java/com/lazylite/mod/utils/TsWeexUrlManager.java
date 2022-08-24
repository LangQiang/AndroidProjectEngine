package com.lazylite.mod.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * @author yjsf216
 * @date 2018/12/10
 */
public class TsWeexUrlManager {
    private static final String TAG = "TsWeexUrlManager";

    public static final String HOST = "webapi.kuwo.cn";
    /**Weex的测试host*/
//    public static final String HOST = "webapi.kuwo-inc.com";

    public static final String HOME_URL_KEY = "home_url_key";
    public static final String MINE_URL_KEY = "mine_url_key";

    /**
     * 是否使用离线包
     */
    public static final String WEEX_PACKAGE = "weex_package";

    public static final String WEEX_DEBUG = "weex_debug";

    private static String WX_PACK_URL = KuwoUrl.UrlDef.WX_PACK_URL.getSafeUrl();

    public static String getWxServerConfig(String version, String appName, String jsVersion) {
        StringBuilder builder = new StringBuilder(WX_PACK_URL);
        builder.append("&android=" + version);
        builder.append("&appName=" + appName);
        builder.append("&jsVersion=" + jsVersion);
        builder.append("&isDiff=true");
        String url = builder.toString();
        return url;
    }


    public static String wxVipUrl() {
        return "welfare.js";
    }

    public static String taskListUrl() {
        return "dialogtaskList.js";
    }

    public static String minePageUrl() {
        return "mine.js";
    }

    public static String mainPageUrl() {
        return "index.js";
    }


    private static String getWxAssertPath(String appId, String page) {
        if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(page)) {
            String assertPath = "wx/" + appId + File.separator + page;
            if (KwFileUtils.assertExist(assertPath)) {
                return  assertPath;
            }
        }
        return "";
    }

    /**
     * 网络错误本地连接为了连接空wifi报-1001错误
     * @return
     */
    public static String errorNetErrorAssertUrl(){
        return getWxAssertPath("500000", "network.js");
    }

    /**
     * 榜单页，参数：currentTabId 顶部的id,currentTagId 左边的id
     * @return
     */
    public static String rankPageUrl() {
        return "rank.js";
    }
}
