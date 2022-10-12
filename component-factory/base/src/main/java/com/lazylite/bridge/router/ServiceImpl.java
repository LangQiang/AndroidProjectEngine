package com.lazylite.bridge.router;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class ServiceImpl implements IService {

    private HashMap<String, Object> services = new HashMap<>();

    @NonNull
    public static IService getInstance() {
        return Inner.SERVICE_IMPL;
    }

    public void register(String serviceName, Object service) {

        if (serviceName == null || service == null) {
            return;
        }

        services.put(serviceName, service);
    }

    public Object getService(String serviceName) {
        if (serviceName == null) {
            return null;
        }
        return services.get(serviceName);
    }

    private static class Inner {
        private static final ServiceImpl SERVICE_IMPL = new ServiceImpl();
    }
}
