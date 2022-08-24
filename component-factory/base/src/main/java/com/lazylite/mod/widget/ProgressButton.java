package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.example.basemodule.R;

/**
 * @author qyh
 * @date 2022/2/12
 * describe:
 */
public class ProgressButton extends androidx.appcompat.widget.AppCompatTextView {

    private boolean mFinish;

    private int mProgress;
    private int mMaxProgress = 100;
    private int mMinProgress = 0;

    private GradientDrawable mDrawableButton;
    private GradientDrawable mDrawableProgressBackground;
    private GradientDrawable mDrawableProgress;

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mDrawableProgressBackground = new GradientDrawable();
        mDrawableProgress = new GradientDrawable();
        mDrawableButton = new GradientDrawable();

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);

        try {
            int buttonColor = attr.getColor(R.styleable.ProgressButton_buttonColor, 0);
            mDrawableButton.setColor(buttonColor);
            int progressBackColor = attr.getColor(R.styleable.ProgressButton_progressBackColor, 0);
            mDrawableProgressBackground.setColor(progressBackColor);
            int progressColor = attr.getColor(R.styleable.ProgressButton_progressColor, 0);
            mDrawableProgress.setColor(progressColor);
            mProgress = attr.getInteger(R.styleable.ProgressButton_progress, mProgress);

        } finally {
            attr.recycle();
        }

        setBackgroundDrawable(mDrawableButton);

        mFinish = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mProgress > mMinProgress && mProgress <= mMaxProgress && !mFinish) {
            float progressWidth =
                    (float) getMeasuredWidth() * ((float) (mProgress - mMinProgress) / mMaxProgress - mMinProgress);
            mDrawableProgress.setBounds(0, 0, (int) progressWidth, getMeasuredHeight());
            mDrawableProgress.draw(canvas);
//            if (mProgress == mMaxProgress) {
//                setBackgroundDrawable(mDrawableButton);
//                mFinish = true;
//            }
        }
        super.onDraw(canvas);
    }


    public void setProgress(int progress) {
        if (!mFinish) {
            mProgress = progress;
            setBackgroundDrawable(mDrawableProgressBackground);
            invalidate();
        }
    }

    public void setBackColor(int color) {
        mDrawableProgressBackground.setColor(color);
        invalidate();
    }

    public void reset() {
        mFinish = false;
        mProgress = mMinProgress;
    }
}
