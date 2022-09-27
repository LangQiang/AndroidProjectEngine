package com.lazylite.processorlib;

import com.google.gson.Gson;

public class DeepLinkClassInfo {

    private String fullName;

    private String path;

    public DeepLinkClassInfo() {
        clear();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void clear() {
        this.fullName = "";
        this.path = "/";
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
