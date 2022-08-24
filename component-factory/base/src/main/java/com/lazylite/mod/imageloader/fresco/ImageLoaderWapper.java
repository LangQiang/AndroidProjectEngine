package com.lazylite.mod.imageloader.fresco;


import com.facebook.drawee.view.SimpleDraweeView;
import com.lazylite.mod.imageloader.fresco.load.Loader;
import com.lazylite.mod.imageloader.fresco.load.impl.FrescoImageLoader;

/**
 * 我不想再替换第二次了:)
 * Created by tiancheng on 2016/11/21
 */

public class ImageLoaderWapper {

    public static Loader<SimpleDraweeView> getInstance() {
        return FrescoImageLoader.getInstance();
    }

}
