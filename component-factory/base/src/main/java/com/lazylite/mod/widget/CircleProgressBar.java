package com.lazylite.mod.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.basemodule.R;


/**
 * @author qyh
 * @date 2022/1/4
 * describe:
 */
public class CircleProgressBar extends View {

    // 画圆环的画笔
    private Paint mRingPaint;
    // 画字体的画笔
    private Paint mTextPaint;
    //背景圆环画笔
    private Paint mBackPaint;
    //字体颜色
    private int mTxtColor;
    private float mTxtSize;
    // 圆环颜色
    private int mRingColor;
    //背景颜色
    private int mBackColor;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 字的长度
    private float mTxtWidth;
    // 字的高度
    private float mTxtHeight;
    // 总进度
    private int mTotalProgress = 100;
    // 当前进度
    private int mProgress;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.base_CircleProgressBar, 0, 0));
        initVariable();
    }

    private void initAttrs(TypedArray typeArray) {
        if (typeArray != null) {
            try {
                mRadius = typeArray.getDimension(R.styleable.base_CircleProgressBar_base_radius, 40);
                mStrokeWidth = typeArray.getDimension(R.styleable.base_CircleProgressBar_base_strokeWidth, 30);
                mTxtSize = typeArray.getDimension(R.styleable.base_CircleProgressBar_base_txtSize, 30);
                mTxtColor = typeArray.getColor(R.styleable.base_CircleProgressBar_base_txtColor, 0xFF9C00);
                mRingColor = typeArray.getColor(R.styleable.base_CircleProgressBar_base_ringColor, 0xFFFFFFFF);
                mBackColor = typeArray.getColor(R.styleable.base_CircleProgressBar_base_backColor, 0xFFFFFFFF);
                mRingRadius = mRadius + mStrokeWidth / 2;
            } finally {
                typeArray.recycle();
            }
        }
    }

    private void initVariable() {
        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(mBackColor);
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(mStrokeWidth);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTxtColor);
        mTextPaint.setTextSize(mTxtSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mRadius / 2);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        @SuppressLint("DrawAllocation") RectF ovalbg = new RectF();
        ovalbg.left = (mXCenter - mRingRadius);
        ovalbg.top = (mYCenter - mRingRadius);
        ovalbg.right = mRingRadius * 2 + (mXCenter - mRingRadius);
        ovalbg.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
        canvas.drawArc(ovalbg, -90, 360, false, mBackPaint);

        @SuppressLint("DrawAllocation") RectF oval = new RectF();
        oval.left = (mXCenter - mRingRadius);
        oval.top = (mYCenter - mRingRadius);
        oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
        oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
        canvas.drawArc(oval, -90, ((float) mProgress / mTotalProgress) * 360, false, mRingPaint);
        String txt = mProgress + "%";
        mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
        canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }

}
