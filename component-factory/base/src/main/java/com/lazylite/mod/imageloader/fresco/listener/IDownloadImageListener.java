package com.lazylite.mod.imageloader.fresco.listener;

/**
 * Created by tiancheng on 2016/11/14
 */
public interface IDownloadImageListener<T> {

    void onSuccess(T result);

    void onFailure(Throwable throwable);

    void onProgress(float progress);
}
