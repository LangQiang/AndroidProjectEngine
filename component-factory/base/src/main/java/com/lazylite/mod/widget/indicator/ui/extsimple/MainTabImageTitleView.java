package com.lazylite.mod.widget.indicator.ui.extsimple;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.IDownloadImageListener;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;

public class MainTabImageTitleView extends RelativeLayout implements IPagerTitle {

    private int mUnSelectSize = 22;
    private int mSelectSize = 27;
    private float scale = 1;

    private int mBottomSelectSize = 3;
    private int mUnBottomSelectSize = 4;


    public MainTabImageTitleView(Context context) {
        super(context);
    }

    public MainTabImageTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    public MainTabImageTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }


    @Override
    public void onSkinChanged() {
    }

    public void setUnSelectImageSize(int unSelectSize) {
        mUnSelectSize = unSelectSize;
    }

    public void setSelectImageSize(int selectSize) {
        mSelectSize = selectSize;
    }

    @Override
    public void onSelected(int index, int totalCount) {
        LayoutParams layoutParams = (LayoutParams) getImageView().getLayoutParams();
        layoutParams.height = ScreenUtility.dip2px(mSelectSize);
        if (scale != 0) {
            layoutParams.width = (int) (layoutParams.height * scale);
            layoutParams.bottomMargin= ScreenUtility.dip2px(mBottomSelectSize);
        }
        getImageView().setLayoutParams(layoutParams);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        LayoutParams layoutParams = (LayoutParams) getImageView().getLayoutParams();
        layoutParams.height = ScreenUtility.dip2px(mUnSelectSize);
        if (scale != 0) {
            layoutParams.width = (int) (layoutParams.height * scale);
            layoutParams.bottomMargin= ScreenUtility.dip2px(mUnBottomSelectSize);
        }
        getImageView().setLayoutParams(layoutParams);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

    }


    @Override
    public int getContentLeft() {
        return getLeft();
    }

    @Override
    public int getContentRight() {
        return getRight();
    }

    @Override
    public int getContentTop() {
        return 0;
    }

    @Override
    public int getContentBottom() {
        return 0;
    }


    @Override
    public void setNormalColorRid(int colorRid) {

    }

    @Override
    public void setSelectedColorRid(int colorRid) {

    }

    public SimpleDraweeView getImageView() {
        return (SimpleDraweeView) getChildAt(0);
    }

    public void loadImage(String url) {
        SimpleDraweeView imageView = new SimpleDraweeView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams viewGroupLayoutParams = new LayoutParams(ScreenUtility.dip2px(mUnSelectSize), ScreenUtility.dip2px(mUnSelectSize));
        viewGroupLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(imageView, viewGroupLayoutParams);
        ImageLoaderWapper.getInstance().load(url, new IDownloadImageListener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap result) {
                scale = (float) result.getWidth() / (float) result.getHeight();
                LayoutParams layoutParams = (LayoutParams) getImageView().getLayoutParams();
                layoutParams.width = (int) (layoutParams.height * scale);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                getImageView().setImageBitmap(result);
                getImageView().setLayoutParams(layoutParams);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onProgress(float progress) {

            }
        });
    }
}
