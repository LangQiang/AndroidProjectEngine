package com.lazylite.processorlib;

import com.google.gson.Gson;
import com.lazylite.annotationlib.Constants;

import java.util.HashSet;
import java.util.Set;

public class InitClassInfo {

    private String fullName;

    private String moduleName;

    private Set<String> dependSet = new HashSet<>();

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setDepend(String depend) {
        dependSet.add(depend);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
