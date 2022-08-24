package com.lazylite.mod.imageloader.fresco.load.impl;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.facebook.imagepipeline.image.ImageInfo;
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig;
import com.lazylite.mod.imageloader.fresco.listener.IDisplayImageListener;
import com.lazylite.mod.imageloader.fresco.listener.IDownloadImageListener;
import com.lazylite.mod.imageloader.fresco.load.Loader;
import com.lazylite.mod.imageloader.fresco.util.FrescoUtil;

/**
 * Created by tiancheng on 2016/11/14
 */
public abstract class BaseImageLoader<T extends ImageView> implements Loader<T> {

    @Override
    public void load(T view, String url) {
        load(view, FrescoUtil.parseUri(url), null, null);
    }

    @Override
    public void load(T view, String url, IDisplayImageListener<ImageInfo> listener) {
        load(view, FrescoUtil.parseUri(url), null, listener);
    }

    @Override
    public void load(T view, String url, ImageLoadConfig config) {
        load(view, FrescoUtil.parseUri(url), config, null);
    }

    @Override
    public void load(T view, String url, ImageLoadConfig config, IDisplayImageListener<ImageInfo> listener) {
        load(view, FrescoUtil.parseUri(url), config, listener);
    }

    @Override
    public void load(T view, int resId) {
        load(view, FrescoUtil.parseUriFromResId(resId), null, null);
    }

    @Override
    public void load(T view, int resId, ImageLoadConfig config) {
        load(view, FrescoUtil.parseUriFromResId(resId), config, null);
    }

    @Override
    public void load(String url, IDownloadImageListener<Bitmap> listeren) {
        load(FrescoUtil.parseUri(url), 0, 0, listeren);
    }

    @Override
    public void load(String url, int w, int h, IDownloadImageListener<Bitmap> listeren) {
        load(FrescoUtil.parseUri(url), w, h, listeren);
    }

}
