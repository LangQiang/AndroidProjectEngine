package com.lazylite.mod.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;

/**
 * @author DongJr
 * @date 2020/10/23
 */
public class KwLottieAnimationView extends LottieAnimationView {

    public KwLottieAnimationView(Context context) {
        super(context);
    }

    public KwLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KwLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isShown() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void setComposition(@NonNull LottieComposition composition) {
        try {
            super.setComposition(composition);
        } catch (Exception e){}
    }

}
