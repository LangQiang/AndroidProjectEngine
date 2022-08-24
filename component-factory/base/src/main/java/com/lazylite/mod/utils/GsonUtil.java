package com.lazylite.mod.utils;

import com.google.gson.Gson;

/**
 * @author qyh
 * email：yanhui.qiao@tencentmusic.com
 * @date 2021/9/15.
 * description：
 */
public class GsonUtil {

    private static Gson sGson;

    public static Gson getGson() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }
}
