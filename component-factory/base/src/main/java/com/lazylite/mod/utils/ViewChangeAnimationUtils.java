package com.lazylite.mod.utils;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * 处理View变化的动画
 * Created by qianger on 2016/12/9.
 */

public class ViewChangeAnimationUtils {

    /**
     * 使用渐现动画到显示
     * @param view 要处理View
     */
    public static void animationShow(@NonNull final View view) {

        if (view == null || View.VISIBLE == view.getVisibility()) {//可见不处理
            return;
        }
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setAlpha(valueAnimator.getAnimatedFraction());
            }
        });
        anim.start();
    }

    /**
     * 使用渐隐动画到消失
     * @param view 要处理的View
     * @return 正在做的动画
     */
    public static ValueAnimator animationHide(final View view) {
        return animationHide(view, 200);
    }

    /**
     * 一个动画对多个View进行隐藏操作
     * @param duration 进行的时长
     * @param views 要隐藏的一些view
     * @return 隐藏这些View的动画
     */
    public static ValueAnimator  animationHideViews(int duration, final View... views) {
        if (views == null || views.length <= 0) {
            return null;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                for (View view : views) {
                    if (view == null || view.getVisibility() == View.GONE) {
                        continue;
                    }
                    view.setAlpha(1 - valueAnimator.getAnimatedFraction());
                    if (valueAnimator.getAnimatedFraction() >= 1.0f) {
                        view.setAlpha(1);
                        view.setVisibility(View.GONE);
                    }
                }
            }
        });
        anim.start();
        return anim;
    }
    /**
     * 使用渐隐动画到消失
     * @param view 要处理的View
     * @param duration 时长
     * @return 正在做的动画
     */
    public static ValueAnimator animationHide(final View view, int duration) {
        if (view == null || view.getVisibility() == View.GONE) {
            return null;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setAlpha(1 - valueAnimator.getAnimatedFraction());
                if (valueAnimator.getAnimatedFraction() >= 1.0f) {
                    view.setAlpha(1);
                    view.setVisibility(View.GONE);
                }
            }
        });
        anim.start();
        return anim;
    }

    /**
     * 使用动画交换两个View, 从一个到另一个
     * @param show 要显示的View
     * @param hide 要隐藏的View
     * @param setGone 是否要gone掉hide的View
     */
    public static ValueAnimator animationSwitch(@NonNull final View show, @NonNull final View hide, final boolean setGone) {
        if (show == null || hide == null) {
            return null;
        }
        final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(120);
        show.setAlpha(0);
        show.setVisibility(View.VISIBLE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                show.setAlpha(fraction);
                hide.setAlpha(1 - fraction);
                if (fraction >= 1.0f) {
                    if (setGone) {
                        hide.setVisibility(View.GONE);
                    } else {
                        hide.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        anim.start();
        return anim;
    }

    /**
     * 将ImageView设置为另一个Drawable, 在设置完成后执行一个动画
     * @param image 要变化的ImageView
     * @param drawable 要设置的Drawable
     */
    public static ValueAnimator animationChangeImageDrawable(@NonNull final ImageView image, final Drawable drawable) {
        if (image == null) {
            return null;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        image.setImageDrawable(drawable);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                image.setAlpha(fraction);
            }
        });
        anim.start();
        return anim;
    }

    private ViewChangeAnimationUtils() {
        throw new IllegalStateException("Do not instance Utils!!");
    }
}
