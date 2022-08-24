package com.lazylite.mod.widget.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.widget.CircularGroup;

public class BlurBgDialog extends Dialog {
    private Activity activity;
    private SnapShotBlurView snapShotBlurView;
    private boolean isShowBlurBg = false;

    public BlurBgDialog(Activity context) {
        this(context, R.style.LRLiteBase_FullWidthDialogTheme);
    }

    public BlurBgDialog(Activity context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected BlurBgDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void showBlurBg (boolean isShow) {
        this.isShowBlurBg = isShow;
    }

    private void init(Activity context) {
        this.activity = context;
    }

    @Override
    public void show() {
        if (activity == null) {
            return;
        }
        if (activity.isFinishing()) {
            return;
        }
        super.show();
        if (getWindow() == null) {
            return;
        }
        getWindow().setWindowAnimations(R.style.LRLiteBase_AnimBottom);
        getWindow().setBackgroundDrawableResource(R.color.LRLiteBase_transparent);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        getWindow().setDimAmount(0.4f);
        if (isShowBlurBg) {
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (snapShotBlurView == null) {
                        snapShotBlurView = new SnapShotBlurView(getContext(), null);
                        snapShotBlurView.setLayoutParams(new ViewGroup.LayoutParams(contentView.getWidth(), contentView.getHeight()));
                        contentView.addView(snapShotBlurView, 0);
                    }
                    snapShotBlurView.updateBlur();
                    snapShotBlurView.setAlpha(0f);
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            snapShotBlurView.setAlpha((Float) animation.getAnimatedValue());
                        }
                    });
                    animator.setStartDelay(200);
                    animator.setDuration(100);
                    animator.start();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private CircularGroup contentView;
    @Override
    public void setContentView(@NonNull View view) {
        contentView = new CircularGroup(getContext());
        contentView.setCorners(42,42,0,0);
        contentView.addView(view);
        if (view.getBackground() == null) {
            if (isShowBlurBg) {
                view.setBackgroundColor(0xcc000000);
            } else {
                view.setBackgroundColor(0xff292929);
            }
        }
        super.setContentView(contentView);
    }

    @Override
    public void dismiss() {
        if (snapShotBlurView != null) {
            snapShotBlurView.setAlpha(0);
            //下一帧dismiss
            snapShotBlurView.post(new Runnable() {
                @Override
                public void run() {
                    BlurBgDialog.super.dismiss();
                }
            });
        } else {
            super.dismiss();
        }
    }

    public void dismissFix(final DismissFixCallback dismissFixCallback) {
        if (snapShotBlurView != null) {
            snapShotBlurView.setAlpha(0);
            //下一帧dismiss
            snapShotBlurView.post(new Runnable() {
                @Override
                public void run() {
                    dismissFixCallback.onDismiss();
                    BlurBgDialog.super.dismiss();
                }
            });
        } else {
            dismissFixCallback.onDismiss();
            super.dismiss();
        }
    }

    public interface DismissFixCallback {
        void onDismiss();
    }
}
