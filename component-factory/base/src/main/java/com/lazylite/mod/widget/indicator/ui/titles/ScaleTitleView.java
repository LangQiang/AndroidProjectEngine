package com.lazylite.mod.widget.indicator.ui.titles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.basemodule.R;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;

/**
 * @author DongJr
 * @date 2020/3/30
 */
public class ScaleTitleView extends View implements IPagerTitle {

    private String mText;
    private float mTextSize;
    private int mNormalColorRid = R.color.black80;
    private int mSelectedColorRid = R.color.rgbFFFF5400;
    private int mNColor;
    private int mSColor;
    private float mMaxScale = 1.1f;
    private float mRate;
    private float mWidth;

    private Paint mPaint;
    private Paint mNormalPaint;
    private Rect tempRect = new Rect();

    public ScaleTitleView(Context context) {
        super(context);
    }

    public ScaleTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSkinChanged() {}

    @Override
    public void onSelected(int index, int totalCount) {
        getPaint().setColor(mNColor);
        getPaint().setFakeBoldText(true);
        invalidate();
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        getPaint().setColor(mSColor);
        getPaint().setFakeBoldText(false);
        invalidate();
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        mRate = mMaxScale + (1.0f - mMaxScale) * leavePercent;
        getPaint().setTextSize(ScreenUtility.dip2px(mTextSize * mRate));
        requestLayout();
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        mRate = 1.0f + (mMaxScale - 1.0f) * enterPercent;
        getPaint().setTextSize(ScreenUtility.dip2px(mTextSize * mRate));
        requestLayout();
    }

    @Override
    public int getContentLeft() {
        tempRect.setEmpty();
        getNormalPaint().getTextBounds(getText(), 0, getText().length(), tempRect);
        return getLeft() + getWidth() / 2 - tempRect.width() / 2;
    }

    @Override
    public int getContentRight() {
        tempRect.setEmpty();
        getNormalPaint().getTextBounds(getText(), 0, getText().length(), tempRect);
        return getRight() - getWidth() / 2 + tempRect.width() / 2;
    }

    @Override
    public int getContentTop() {
        tempRect.setEmpty();
        getNormalPaint().getTextBounds(getText(), 0, getText().length(), tempRect);
        return tempRect.top;
    }

    @Override
    public int getContentBottom() {
        tempRect.setEmpty();
        getNormalPaint().getTextBounds(getText(), 0, getText().length(), tempRect);
        return tempRect.bottom;
    }

    @Override
    public void setNormalColorRid(int colorRid) {
        mNormalColorRid = colorRid;
        mNColor = getContext().getResources().getColor(mNormalColorRid);
        invalidate();
    }

    @Override
    public void setSelectedColorRid(int colorRid) {
        mSelectedColorRid = colorRid;
        mSColor = getContext().getResources().getColor(mSelectedColorRid);
        invalidate();
    }

    public void setTextColor(int color){
        getPaint().setColor(color);
        invalidate();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setMaxScale(float minScale) {
        mMaxScale = minScale;
    }

    private Paint getPaint(){
        if (mPaint == null){
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(ScreenUtility.dip2px(mTextSize));
        }
        return mPaint;
    }

    private Paint getNormalPaint(){
        if (mNormalPaint == null){
            mNormalPaint = new Paint();
            mNormalPaint.setTextSize(ScreenUtility.dip2px(mTextSize));
        }
        return mNormalPaint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth == 0){
            mWidth = getContentRight() - getContentLeft();
        }
        int size = (int) (mWidth * mRate + 0.5f) + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(size, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = getNormalPaint().getFontMetrics();
        float distance=(fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText(mText, getWidth() / 2, getHeight() - distance, getPaint());
    }
}
