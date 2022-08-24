package com.lazylite.mod.widget.indicator.ui.titles;

import android.content.Context;

/**
 * @author DongJr
 *
 * @date 2018/5/31.
 *
 * 大小和颜色渐变标题
 */
public class ScaleAndColorTitleView extends ColorChangeTitleView {

    private float mMaxScale = 1.15f;

    public ScaleAndColorTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        setScaleX(mMaxScale + (1.0f - mMaxScale) * leavePercent);
        setScaleY(mMaxScale + (1.0f - mMaxScale) * leavePercent);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        setScaleX(1.0f + (mMaxScale - 1.0f) * enterPercent);
        setScaleY(1.0f + (mMaxScale - 1.0f) * enterPercent);
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(float minScale) {
        mMaxScale = minScale;
    }
}
