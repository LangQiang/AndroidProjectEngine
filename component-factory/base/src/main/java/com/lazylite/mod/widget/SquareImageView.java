package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.basemodule.R;


public class SquareImageView extends ImageView {

    private float mAspect;

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SquareImageView);
        mAspect = mTypedArray.getFloat(R.styleable.SquareImageView_aspect, 0f);
        mTypedArray.recycle();
    }

    public SquareImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAspect != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * mAspect), MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = widthMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
