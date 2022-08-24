package com.lazylite.mod.http.mgr;

import java.util.Map;

/**
 * Created by lzf on 2022/3/21 14:03
 */
public interface ICommHeaderPolicy {
    boolean isCanUse();

    boolean configParams(Map<String, String> oldParams);
}
