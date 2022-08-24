package com.lazylite.mod.widget.indicator.ui.titles;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import com.lazylite.mod.widget.indicator.ui.simple.SimplePagerTitleView;


/**
 * @author DongJr
 *
 * @date 2018/5/31.
 *
 * 大小渐变标题
 */
public class MainTabTitleView extends SimplePagerTitleView {
    private long maxTime = 300;

    private float mMaxScale = 1.15f;

    private float mTextSize;
    ValueAnimator animator;



    boolean canDoAnimation = true;
    public MainTabTitleView(Context context) {
        super(context);
        initAnimation();
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
//        setTextSize((1.0f + (mMaxScale - 1.0f) * enterPercent) * mTextSize);
    }

    private void initAnimation() {
        animator = new ValueAnimator().ofFloat(mMaxScale - 1);
        animator.setDuration(maxTime);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setTextSize(mTextSize * (value + 1));
            }
        });

    }

    public void setMaxScale(float minScale) {
        mMaxScale = minScale;
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        canDoAnimation = true;
        animator.cancel();
        setTextSize(mTextSize);
    }

    @Override
    public void onSelected(final int index, int totalCount) {
        super.onSelected(index, totalCount);
        if(canDoAnimation){
            canDoAnimation = false;
            animator.start();
        }
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        if (mTextSize == 0){
            mTextSize = size;
        }
    }
}
