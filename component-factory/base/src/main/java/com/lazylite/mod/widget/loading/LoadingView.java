package com.lazylite.mod.widget.loading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.basemodule.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lxh on 2018/4/9.
 */

public class LoadingView extends View {

    private boolean isRunning;

    private int mStartDelay; //延时执行的间隔
    private int mDuration;  //动画周期

    private int mCount; //线条数量
    private int mStrokeWidth; //线的宽度
    private int mMaxHeight;  //最大高度
    private int mMinHeight;  //最小高度
    private int mSpace; //线条间距
    private int mLineColor;
    private boolean mSpecial;// 是否用在了特殊背景的地方，比如推荐页下拉
    private Paint mPaint;

    private List<Line> mLines = new ArrayList<>();

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_LoadingView);
        mStartDelay = typedArray.getInt(R.styleable.LRLiteBase_LoadingView_delay, 200);
        mDuration = typedArray.getInt(R.styleable.LRLiteBase_LoadingView_duration, 1000);
        mSpecial = typedArray.getBoolean(R.styleable.LRLiteBase_LoadingView_special, false);
        mCount = typedArray.getInt(R.styleable.LRLiteBase_LoadingView_count, 5);
        mStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.LRLiteBase_LoadingView_lineStrokeWidth, 10);
        mMaxHeight = typedArray.getDimensionPixelOffset(R.styleable.LRLiteBase_LoadingView_maxLineHeight, 100);
        mMinHeight = typedArray.getDimensionPixelOffset(R.styleable.LRLiteBase_LoadingView_minLineHeight, 20);
        mSpace = typedArray.getDimensionPixelOffset(R.styleable.LRLiteBase_LoadingView_space, 20);
        mLineColor = typedArray.getColor(R.styleable.LRLiteBase_LoadingView_lineColor, context.getResources().getColor(R.color.app_theme_color));
        typedArray.recycle();
        initPaint();
        // 开始就添加line, 不然放onMeasure或者onSizeChanged还得post之后才能启动动画
        for (int i = 0; i < mCount; i++) {
            mLines.add(new Line(i));
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        for (Line line : mLines) {
            line.onParentSizeChanged(w, h);
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        for (int i = 0; i < mCount; i++) {
//            mLines.add(new Line(i, height, width));
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Line line : mLines) {
            canvas.drawLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), mPaint);
        }
    }

    public void setLineColor(int color) {
        mPaint.setColor(color);
    }

    public void startAnimation() {
        for (Line line : mLines) {
            line.startAnimation();
            isRunning = true;
        }
    }

    public void endAnimation() {
        for (Line line : mLines) {
            line.endAnimation();
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    class Line implements ValueAnimator.AnimatorUpdateListener {
        private float mStartX;
        private float mStartY;
        private float mEndX;
        private float mEndY;

        private int mIndex;
        private int mViewHeight;
        private int mViewWidth;

        private ValueAnimator mAnimator;

        public Line(int index) {
            mIndex = index;
//            mViewHeight = viewHeight;
//            mViewWidth = viewWidth;
//            setLineXY(mMinHeight);
            initAnimator();
        }

        private void initAnimator() {
            mAnimator = ValueAnimator.ofFloat(mMinHeight, mMaxHeight, mMinHeight);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(mDuration);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mAnimator.addUpdateListener(this);
            mAnimator.setStartDelay(mIndex * mStartDelay);
        }

        public void startAnimation() {
            mAnimator.start();
        }

        public void endAnimation() {
            mAnimator.end();
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float height = (float) animation.getAnimatedValue();
            setLineXY(height);
            invalidate();
        }

        private void setLineXY(float height) {
            mStartX = (mSpace + mStrokeWidth) * mIndex + (mViewWidth - (mCount - 1) * mSpace - mCount * mStrokeWidth) / 2;
            mEndX = mStartX;
            mStartY = (mViewHeight - height) / 2;
            mEndY = mStartY + height;
//            invalidate();
        }

        public void onParentSizeChanged(int w, int h) {
            mViewHeight = h;
            mViewWidth = w;
            setLineXY(mMinHeight);
        }

        public float getStartX() {
            return mStartX;
        }

        public float getStartY() {
            return mStartY;
        }

        public float getEndX() {
            return mEndX;
        }

        public float getEndY() {
            return mEndY;
        }
    }


}
