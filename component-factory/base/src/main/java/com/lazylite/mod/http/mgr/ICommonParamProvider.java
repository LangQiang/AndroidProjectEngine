package com.lazylite.mod.http.mgr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public interface ICommonParamProvider {
    @Nullable
    ConcurrentHashMap<String, String> getCommonHeads();
    @Nullable
    ConcurrentHashMap<String, String> getCommonQueryParams();
    @NonNull
    String providerName();
}
