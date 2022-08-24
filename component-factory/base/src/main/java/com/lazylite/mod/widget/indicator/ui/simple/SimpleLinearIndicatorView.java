package com.lazylite.mod.widget.indicator.ui.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.model.LocationModel;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

import java.util.List;


/**
 * @author DongJr
 *
 * @date 2018/5/28.
 */
@SuppressLint("ViewConstructor")
public class SimpleLinearIndicatorView extends View implements IPagerIndicator {

    private Paint mIndicatorPaint;
    protected RectF mLineRect = new RectF();
    protected List<LocationModel> mLocationDatas;
    protected IndicatorParameter mParameter;
    protected Matrix mParameterShaderMatrix;
    /**
     * 高度一般不会变，初始化一次就好
     */
    protected boolean initedTopAndBottom;

    public SimpleLinearIndicatorView(Context context, @NonNull IndicatorParameter parameter) {
        super(context);
        this.mParameter = parameter;
        init();
    }

    private void init() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);
        if( mParameter.indicatorColorRid <= 0){
            mParameter.indicatorColorRid = R.color.ts_lite_common_fa452b;
        }
        mIndicatorPaint.setColor(getContext().getResources().getColor(mParameter.indicatorColorRid));
        if (mParameter.shader != null) {
            mParameterShaderMatrix = new Matrix();
            mIndicatorPaint.setShader(mParameter.shader);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(null != mParameter.shader){
            mParameterShaderMatrix.setTranslate(mLineRect.left,0);
            mParameter.shader.setLocalMatrix(mParameterShaderMatrix);
        }
        canvas.drawRoundRect(mLineRect, mParameter.radius, mParameter.radius, mIndicatorPaint);
    }

    protected void drawMe(Canvas canvas){
        canvas.drawRoundRect(mLineRect, mParameter.radius, mParameter.radius, mIndicatorPaint);
    }

    @Override
    public int getIndicatorColor() {
        if (mParameter == null){
            return 0;
        }
        return mParameter.indicatorColorRid;
    }

    @Override
    public void onSkinChanged() {
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

        mLineRect.left = leftX + (nextLeftX - leftX) * mParameter.startInterpolator.getInterpolation(positionOffset) + mParameter.lRPadding;
        mLineRect.right = rightX + (nextRightX - rightX) * mParameter.endInterpolator.getInterpolation(positionOffset) - mParameter.lRPadding;

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
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProvideLocation(List<LocationModel> locationModels) {
        mLocationDatas = locationModels;
    }
}
