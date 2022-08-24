package com.lazylite.mod.http.mgr;

import com.lazylite.mod.http.mgr.model.IResponseInfo;

/**
 * Created by lzf on 2022/3/28 10:28
 */
public interface IHttpResultCheckPolicy {
    default boolean isCanUse(IResponseInfo responseInfo){
        return false;
    }
    default void onResult(IResponseInfo responseInfo){}
}
