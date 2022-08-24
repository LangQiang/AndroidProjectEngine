package com.lazylite.mod.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatImageView;

public class SimpleLoading extends AppCompatImageView {
    public static final int LOADING_DURATION = 650;
    private ObjectAnimator loadingAnim;

    public SimpleLoading(Context context) {
        super(context);
    }

    public SimpleLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    private void startAnim() {
        if (loadingAnim == null) {
            loadingAnim = ObjectAnimator.ofFloat(this, View.ROTATION, 0, 360f);
            loadingAnim.setDuration(LOADING_DURATION);
            loadingAnim.setInterpolator(new LinearInterpolator());
            loadingAnim.setRepeatCount(Animation.INFINITE);
            loadingAnim.setRepeatMode(ObjectAnimator.RESTART);
        }
        loadingAnim.start();
    }

    private void stopAnim() {
        if (loadingAnim != null) {
            loadingAnim.end();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
