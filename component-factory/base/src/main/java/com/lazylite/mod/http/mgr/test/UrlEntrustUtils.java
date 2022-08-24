package com.lazylite.mod.http.mgr.test;

import android.text.TextUtils;

import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 可能包含scheme 路径版本
 *
 * */
public class UrlEntrustUtils {

    private final static Map<String,UrlTestChangeInfo> hostMap = new HashMap<>();

    // host1#testHost1#version1|host2#testHost2#version2
    static {
        String entrust_host = ConfMgr.getStringValue(IConfDef.SEC_APP, IConfDef.KEY_ENTRUST_HOST, "");
        if (!TextUtils.isEmpty(entrust_host)) {
            String[] hostKV = entrust_host.split("\\|");
            for (String s : hostKV) {
                String[] split = s.split("#");
                if (split.length == 2) {
                    hostMap.put(split[0], new UrlTestChangeInfo(split[1], ""));
                }
            }
        }
    }

    public static String entrustHost(String key, String defaultHost) {
        if (hostMap.containsKey(key)) {
            UrlTestChangeInfo value = hostMap.get(key);
            if (value != null) {
                return value.host;
            }
        }

        return defaultHost;
    }

    public static String entrustVersion(String key, String defaultVersion) {
        if (hostMap.containsKey(key)) {
            UrlTestChangeInfo value = hostMap.get(key);
            if (value != null) {
                return value.version;
            }
        }

        return defaultVersion;
    }
}
