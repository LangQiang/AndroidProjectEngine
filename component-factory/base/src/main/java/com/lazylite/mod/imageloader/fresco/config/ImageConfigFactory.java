package com.lazylite.mod.imageloader.fresco.config;

import com.example.basemodule.R;

/**
 * ！！！请严格标注type类型，自己是使用在哪里的图片config需求，以便维护
 * ！！！请严格标注type类型，自己是使用在哪里的图片config需求，以便维护
 * ！！！请严格标注type类型，自己是使用在哪里的图片config需求，以便维护
 *
 * Created by tiancheng on 2016/12/10
 */

public class ImageConfigFactory {

    /**
     *简单的圆形没有边线等，默认图是R.drawable.default_people
     */
    public static final int SIMPLE_CICLE_PEOPLE = 1;

    /**
     * 听书的默认方形图片加载
     */
    public static final int TINGSHU_DEFAULT_SQUARE = 2;

    /**
     * banner
     */
    public static final int BANNER_RECT = 3;

    private final static ImageLoadConfig CONFIG_SQUARE = new ImageLoadConfig.Builder()
            .setLoadingDrawable(R.drawable.base_img_default)
            .setFailureDrawable(R.drawable.base_img_default)
            .create();

    private final static ImageLoadConfig CONFIG_CIRCLE_PEOPLE = new ImageLoadConfig.Builder()
            .setLoadingDrawable(R.drawable.default_people)
            .setFailureDrawable(R.drawable.default_people)
            .circle()
            .create();

    private final static ImageLoadConfig CONFIG_BANNER_RECT = new ImageLoadConfig.Builder()
            .setLoadingDrawable(R.drawable.base_img_banner_default)
            .setFailureDrawable(R.drawable.base_img_banner_default)
            .create();


    public static ImageLoadConfig createFrescoConfig(int type) {
        switch (type) {
            case TINGSHU_DEFAULT_SQUARE:
                return CONFIG_SQUARE;
            case SIMPLE_CICLE_PEOPLE:
                return CONFIG_CIRCLE_PEOPLE;
            case BANNER_RECT:
                return CONFIG_BANNER_RECT;
            default:
                return CONFIG_SQUARE;
        }
    }
}
