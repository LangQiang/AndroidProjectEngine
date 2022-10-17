package com.lazylite.mod.http.mgr.model;

import androidx.annotation.NonNull;

import java.util.Map;

public class CommonParam {

    private final Map<String, String> params;

    private final String operatorPath;

    public CommonParam(@NonNull Map<String, String> params, @NonNull String operatorPath) {
        this.params = params;
        this.operatorPath = operatorPath;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getOperatorPath() {
        return operatorPath;
    }
}
