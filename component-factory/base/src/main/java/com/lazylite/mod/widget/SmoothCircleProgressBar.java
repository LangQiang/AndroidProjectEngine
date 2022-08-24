package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.basemodule.R;

public class SmoothCircleProgressBar extends View {
    private Paint mPaint;
    private float mCurProgress;
    private float mFinalProgress;
    private boolean isWithAnim;
    private int mColor;
    private float mStrokeWidth;
    private float velocity = 8;
    private RectF rectF;

    private boolean isDirty = false;
    private int radius;
    private int mProgressSlotColor;
    // 是否需要进度槽
    private boolean mNeedProessSlot;

    public SmoothCircleProgressBar(Context context) {
        this(context, null);
    }

    public SmoothCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_SmoothCircleProgressBar);
        mStrokeWidth = typedArray.getDimension(R.styleable.LRLiteBase_SmoothCircleProgressBar_sc_strokeWidth, 0);
        mColor = typedArray.getColor(R.styleable.LRLiteBase_SmoothCircleProgressBar_sc_strokeColor, 0xfffa452b);
        mProgressSlotColor = typedArray.getColor(R.styleable.LRLiteBase_SmoothCircleProgressBar_sc_slotColor,
                Color.WHITE);
        mNeedProessSlot = typedArray.getBoolean(R.styleable.LRLiteBase_SmoothCircleProgressBar_sc_needSlot,
                false);
        typedArray.recycle();
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mColor);
        mFinalProgress = 0;
        mCurProgress = 0;
        rectF = new RectF();

    }

    public void setStrokeWidth(float strokeWidthDp) {
        this.mStrokeWidth = strokeWidthDp;
        isDirty = true;
        invalidate();
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(mColor);
        invalidate();
    }

    public void setVelocity(int velocity) {
        if (velocity <= 0) {
            this.velocity = 1;
        } else {
            this.velocity = velocity;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        radius = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setRectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStrokeWidth == 0) {
            return;
        }
        if (isDirty) {
            setRectF();
            isDirty = false;
        }

        // 绘制进度槽
        if (mNeedProessSlot) {
            mPaint.setColor(mProgressSlotColor);
            canvas.drawArc(rectF, 0F, 360F, false, mPaint);
        }

        if (!isWithAnim) {
            mCurProgress = mFinalProgress;
        }
        mPaint.setColor(mColor);
        if (mFinalProgress == mCurProgress) {
            canvas.drawArc(rectF, -90, mCurProgress, false, mPaint);
        } else {
            if (Math.abs(mCurProgress - mFinalProgress) <= velocity) {
                mCurProgress = mFinalProgress;
            } else {
                if (mCurProgress > mFinalProgress) {
                    mCurProgress -= velocity;
                } else {
                    mCurProgress += velocity;
                }
            }

            canvas.drawArc(rectF, -90, mCurProgress, false, mPaint);
            postInvalidateOnAnimation();
        }

    }

    public void setProgress(float progress) {
        if (progress >= 1) {
            progress = 1;
        } else if (progress < 0) {
            progress = 0;
        }
        isWithAnim = false;
        this.mFinalProgress = 360f * progress;
        invalidate();
//        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//            invalidate();
//        } else {
//            postInvalidate();
//        }
    }

    public void setProgressWithAnim(float progress) {
        if (progress > 1) {
            progress = 1;
        } else if (progress < 0) {
            progress = 0;
        }
        isWithAnim = true;
        this.mFinalProgress = 360f * progress;
        invalidate();
//        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//            invalidate();
//        } else {
//            postInvalidate();
//        }
    }

    public void setNeedProessSlot(boolean needProessSlot){
        mNeedProessSlot = needProessSlot;
        invalidate();
    }

    private void setRectF() {

        float halfStrokeWidth = mStrokeWidth / 2;
        if (rectF == null) {
            rectF = new RectF(halfStrokeWidth, halfStrokeWidth, radius - halfStrokeWidth, radius - halfStrokeWidth);
        } else {
            rectF.left = halfStrokeWidth;
            rectF.top = halfStrokeWidth;
            rectF.right = radius - halfStrokeWidth;
            rectF.bottom = radius - halfStrokeWidth;
        }
    }
}
