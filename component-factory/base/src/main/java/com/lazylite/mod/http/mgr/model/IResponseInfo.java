package com.lazylite.mod.http.mgr.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public interface IResponseInfo {

    int getCode();

    boolean isSuccessful();

    byte[] getData();

    String getErrorMsg();

    @NonNull
    String dataToString();

    Map<String, List<String>> getResponseHeaders();

    boolean isServeSuccess();

    String getServeMsg();

    String getFinalRequestUrl();
}
