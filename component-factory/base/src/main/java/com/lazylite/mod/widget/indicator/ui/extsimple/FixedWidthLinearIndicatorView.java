package com.lazylite.mod.widget.indicator.ui.extsimple;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.model.LocationModel;
import com.lazylite.mod.widget.indicator.ui.simple.SimpleLinearIndicatorView;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

/**
 * Created by lzf on 2019-04-22 14:07
 */
public class FixedWidthLinearIndicatorView extends SimpleLinearIndicatorView {
    private static final int FixedWidth = 26;//单位dp

    /**
     * 高度一般不会变，初始化一次就好
     */
    private boolean initedTopAndBottom;
    private int mFixedWidth ;

    public FixedWidthLinearIndicatorView(Context context, @NonNull IndicatorParameter parameter) {
        super(context,parameter);
        mFixedWidth = ScreenUtility.dip2px(FixedWidth);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //positionOffset 右滑从0-1，左滑从1-0
        if (mLocationDatas == null || mLocationDatas.isEmpty()){
            return;
        }
        LocationModel current = IndicatorHelper.getCorrectLocation(mLocationDatas, position);
        LocationModel next = IndicatorHelper.getCorrectLocation(mLocationDatas, position + 1);

        float leftX, nextLeftX, rightX, nextRightX;
        if (mParameter.showMode == IndicatorParameter.MODE_FIXED_TITLE){
            leftX = current.contentLeft;
            nextLeftX = next.contentLeft;
            rightX = current.contentRight;
            nextRightX = next.contentRight;
        } else if (mParameter.showMode == IndicatorParameter.MODE_CIRCLE){
            leftX = current.contentLeft;
            nextLeftX = next.contentLeft;
            rightX = current.contentRight;
            nextRightX = next.contentRight;
            int space = (getHeight() - current.getContentHeight()) / 2;
            if (!initedTopAndBottom){
                mLineRect.top = space - mParameter.tBPadding;
                mLineRect.bottom = getHeight() - space + mParameter.tBPadding;
                mParameter.radius = (int) ((mLineRect.bottom - mLineRect.top) / 2);
                initedTopAndBottom = true;
            }
        } else {
            leftX = current.left;
            nextLeftX = next.left;
            rightX = current.right;
            nextRightX = next.right;
        }

        mLineRect.left = leftX + (nextLeftX - leftX) * mParameter.startInterpolator.getInterpolation(positionOffset);
        mLineRect.right = rightX + (nextRightX - rightX) * mParameter.endInterpolator.getInterpolation(positionOffset);

        float width =  mLineRect.right - mLineRect.left;
        if(width > mFixedWidth){
            float lef = mLineRect.left + (width - mFixedWidth) / 2;
            float rig =  mLineRect.right - (width - mFixedWidth) / 2;
            mLineRect.left = lef;
            mLineRect.right = rig;
        }

        if (mParameter.showMode != IndicatorParameter.MODE_CIRCLE && !initedTopAndBottom){
            if (mParameter.gravity == Gravity.TOP){
                mLineRect.top = mParameter.verticalSpace;
                mLineRect.bottom = mParameter.indicatorHeight + mParameter.verticalSpace;
            } else if (mParameter.gravity == Gravity.BOTTOM){
                mLineRect.top = getHeight() - mParameter.indicatorHeight - mParameter.verticalSpace;
                mLineRect.bottom = getHeight() - mParameter.verticalSpace;
            }
            initedTopAndBottom = true;
        }
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (mLocationDatas == null || mParameter == null) {
            return;
        }
        LocationModel next = IndicatorHelper.getCorrectLocation(mLocationDatas, position);
        float leftX, rightX;
        if (mParameter.showMode == IndicatorParameter.MODE_FIXED_TITLE){
            leftX = next.contentLeft;
            rightX = next.contentRight;
        } else if (mParameter.showMode == IndicatorParameter.MODE_CIRCLE){
            leftX = next.contentLeft;
            rightX = next.contentRight;
            int space = (getHeight() - next.getContentHeight()) / 2;
            if (!initedTopAndBottom){
                mLineRect.top = space - mParameter.tBPadding;
                mLineRect.bottom = getHeight() - space + mParameter.tBPadding;
                mParameter.radius = (int) ((mLineRect.bottom - mLineRect.top) / 2);
                initedTopAndBottom = true;
            }
        } else {
            leftX = next.left;
            rightX = next.right;
        }
        mLineRect.left = leftX;
        mLineRect.right = rightX;

        float width =  mLineRect.right - mLineRect.left;
        if(width > mFixedWidth){
            float lef = mLineRect.left + (width - mFixedWidth) / 2;
            float rig =  mLineRect.right - (width - mFixedWidth) / 2;
            mLineRect.left = lef;
            mLineRect.right = rig;
        }
        invalidate();
    }

}
