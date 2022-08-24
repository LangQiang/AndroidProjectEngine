package com.lazylite.mod.http.mgr.model;

import android.os.Handler;

import java.util.Map;

public interface IRequestInfo {

    String getUrl();

    byte[] getBody();

    Map<String, String> getHeaders();

    Handler getHandler();

}
