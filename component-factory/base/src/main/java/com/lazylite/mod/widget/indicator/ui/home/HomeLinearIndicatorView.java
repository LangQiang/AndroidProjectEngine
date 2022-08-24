package com.lazylite.mod.widget.indicator.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.model.LocationModel;
import com.lazylite.mod.widget.indicator.ui.simple.SimpleLinearIndicatorView;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;


/**
 * @author DongJr
 *
 * @date 2018/5/28.
 */
@SuppressLint("ViewConstructor")
public class HomeLinearIndicatorView extends SimpleLinearIndicatorView {

    public HomeLinearIndicatorView(Context context, @NonNull IndicatorParameter parameter) {
        super(context, parameter);
    }

    //产品要求 指示条的宽度为内容宽度的2/5
    private float per = 3 * 1f / 5;

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
            float curExt = (current.getContentWidth() - current.getContentWidth() * per)/2;
            float nextExt =  (next.getContentWidth() - next.getContentWidth() * per)/2;
            leftX = current.contentLeft + curExt;
            nextLeftX = next.contentLeft + nextExt;
            rightX = current.contentRight - curExt;
            nextRightX = next.contentRight - nextExt;
        } else if (mParameter.showMode == IndicatorParameter.MODE_CIRCLE){
            leftX = current.contentLeft;
            nextLeftX = next.contentLeft ;
            rightX = current.contentRight ;
            nextRightX = next.contentRight ;
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

        mLineRect.left = leftX + (nextLeftX - leftX) * mParameter.startInterpolator.getInterpolation(positionOffset) + mParameter.lRPadding;
        mLineRect.right = rightX + (nextRightX - rightX) * mParameter.endInterpolator.getInterpolation(positionOffset) - mParameter.lRPadding;

        if (mParameter.showMode != IndicatorParameter.MODE_CIRCLE && !initedTopAndBottom){
            if (mParameter.gravity == Gravity.TOP){
                mLineRect.top = mParameter.verticalSpace;
                mLineRect.bottom = mParameter.indicatorHeight + mParameter.verticalSpace;
            } else if (mParameter.gravity == Gravity.BOTTOM){
                mLineRect.top = getHeight() - mParameter.indicatorHeight - mParameter.verticalSpace;
                mLineRect.bottom = getHeight() - mParameter.verticalSpace;
            } else if (mParameter.gravity == Gravity.CENTER){
                int space = (getHeight() - current.getContentHeight()) / 2;
                mLineRect.top = current.getContentHeight() + space + mParameter.tBPadding;
                mLineRect.bottom = current.getContentHeight() + space + mParameter.tBPadding + mParameter.indicatorHeight;
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
            float offset = (next.getContentWidth() - next.getContentWidth() * per) / 2;
            leftX = next.contentLeft + offset;
            rightX = next.contentRight - offset;
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
        invalidate();
    }

}
