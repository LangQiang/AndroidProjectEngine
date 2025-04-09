package com.lazylite.mod.global;

import java.util.ArrayList;

import okhttp3.Interceptor;

public class BaseConfig {
    public boolean allowProxy = false;
    public String deepLinkScheme = "debug";

    public int connectTimeout = 30;
    public int readTimeout = 60;
    public int writeTimeout = 60;
    public ArrayList<Interceptor> interceptors = new ArrayList<>();
    public ArrayList<Interceptor> netWorkInterceptors = new ArrayList<>();
}
