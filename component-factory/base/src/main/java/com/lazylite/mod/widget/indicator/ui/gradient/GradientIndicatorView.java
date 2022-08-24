package com.lazylite.mod.widget.indicator.ui.gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.model.LocationModel;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

import java.util.List;

public class GradientIndicatorView extends View implements IPagerIndicator {

    private Context context;
    private IndicatorParameter mParameter;

    public GradientIndicatorView(Context context, IndicatorParameter parameter) {
        super(context);
        this.context = context;
        this.mParameter = parameter;

        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);
        if (mParameter.indicatorColorRid != 0) {
            mIndicatorPaint.setColor(ContextCompat.getColor(context, mParameter.indicatorColorRid));
        }
        if (mParameter.shader != null) {
            mIndicatorPaint.setShader(mParameter.shader);
        }
    }

    private Paint mIndicatorPaint = new Paint();
    private RectF mLineRect = new RectF();
    private List<LocationModel> mLocationDatas = null;
    private boolean initTopAndBottom = false;



    private RectF drawRect = new RectF();

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mLineRect.left + (mLineRect.width() - mParameter.fixedWidth) / 2f, 0f);
        canvas.drawRoundRect(
                drawRect,
                mParameter.radius,
                mParameter.radius,
                mIndicatorPaint
        );
    }

    @Override
    public int getIndicatorColor() {
        return mParameter.indicatorColorRid;
    }

    @Override
    public void onSkinChanged() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        //positionOffset 右滑从0-1，左滑从1-0
        if (mLocationDatas == null || mLocationDatas.size() == 0) {
            return;
        }
        LocationModel current = IndicatorHelper.getCorrectLocation(mLocationDatas, position);
        LocationModel next = IndicatorHelper.getCorrectLocation(mLocationDatas, position + 1);


        float leftX = current.contentLeft;
        float nextLeftX = next.contentLeft;
        float rightX = current.contentRight;
        float nextRightX = next.contentRight;

        float space = (getHeight() - current.getContentHeight()) / 2f;
        if (!initTopAndBottom) {
            mLineRect.top = (space - mParameter.tBPadding);
            mLineRect.bottom = (getHeight() - space + mParameter.tBPadding);
            initTopAndBottom = true;
            if (mParameter.gravity == Gravity.BOTTOM) {
                drawRect.top = getHeight() - mParameter.verticalSpace - mParameter.indicatorHeight;
                drawRect.bottom = drawRect.top + mParameter.indicatorHeight;
            } else {
                drawRect.top =  mParameter.verticalSpace;
                drawRect.bottom = drawRect.top + mParameter.indicatorHeight;
            }
            drawRect.left = 0f;
            drawRect.right = mParameter.fixedWidth;
        }

        mLineRect.left =
                leftX + (nextLeftX - leftX) * mParameter.startInterpolator.getInterpolation(
                        positionOffset
                );
        mLineRect.right =
                rightX + (nextRightX - rightX) * mParameter.endInterpolator.getInterpolation(
                        positionOffset
                );
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (mLocationDatas == null || mLocationDatas.size() == 0) {
            return;
        }
        LocationModel next = IndicatorHelper.getCorrectLocation(mLocationDatas, position);

        float leftX = next.contentLeft;
        float rightX = next.contentRight;

        float space = (getHeight() - next.getContentHeight()) / 2f;
        if (!initTopAndBottom) {
            initTopAndBottom = true;
            mLineRect.top = (space - mParameter.tBPadding);
            mLineRect.bottom = (getHeight() - space + mParameter.tBPadding);
            if (mParameter.gravity == Gravity.BOTTOM) {
                drawRect.top = getHeight() - mParameter.verticalSpace - mParameter.indicatorHeight;
                drawRect.bottom = drawRect.top + mParameter.indicatorHeight;
            } else {
                drawRect.top =  mParameter.verticalSpace;
                drawRect.bottom = drawRect.top + mParameter.indicatorHeight;
            }
            drawRect.left = 0f;
            drawRect.right = mParameter.fixedWidth;
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
