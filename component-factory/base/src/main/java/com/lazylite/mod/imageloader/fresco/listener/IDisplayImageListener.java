package com.lazylite.mod.imageloader.fresco.listener;

import android.graphics.drawable.Animatable;

/**
 * Created by tiancheng on 2016/11/14
 */
public interface IDisplayImageListener<T> {

    void onSuccess(T result, Animatable animatable);

    void onFailure(Throwable throwable);
}
