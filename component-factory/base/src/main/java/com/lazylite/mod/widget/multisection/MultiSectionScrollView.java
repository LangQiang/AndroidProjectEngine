package com.lazylite.mod.widget.multisection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import java.lang.reflect.Method;

public class MultiSectionScrollView extends RelativeLayout implements NestedScrollingParent2 {

    private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    private static final boolean debuggable = false;

    private float mTotalTranslationY;

    private boolean isInDrag;

    private boolean isEntered;

    private ValueAnimator animator;

    private float initHeightPercent = 1f;

    private int currentScrollDirection = -1;  //0 top  1 middle

    private int height;

    private OnQuitListener onQuitListener;

    public MultiSectionScrollView(@NonNull Context context) {
        this(context, null);
    }

    public MultiSectionScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiSectionScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (debuggable) {
            Log.e("test", " w:" + w + "  h:" + h + " ow:" + oldw + " oh:" + oldh);
        }
        height = h;
        mTotalTranslationY = height * (1 - initHeightPercent);
        startEnterAnim();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (debuggable) {
            Log.e("parent", "onStartNestedScroll" + "  axes:" + axes + " target:" + target.getClass().getSimpleName());
        }
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
        if (debuggable) {
            Log.e("parent", "onNestedScrollAccepted" + "  axes:" + axes + " target:" + target.getClass().getSimpleName());
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
        if (debuggable) {
            Log.e("parent", "onStopNestedScroll" + " type：" + type + " target:" + target.getClass().getSimpleName());
        }
        releaseSelf();
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (debuggable) {
            Log.e("parent", "onNestedPreScroll");
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (debuggable) {
            Log.e("parent", "onNestedPreScroll" + "  dy:" + dy + " type:" + type + " total:" + mTotalTranslationY + "   get:" + getTranslationY());
        }
        //上滑
        if (dy > 0) {
            //未到顶部
            if (isShowFull()) {
                return;
            }
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                safeStopFling(target, type);
                consumed[1] = dy;
            } else {
                mTotalTranslationY -= dy;
                setTranslationY(mTotalTranslationY);
                consumed[1] = dy;
                currentScrollDirection = 0;
                isInDrag = true;
            }
        } else {
            //下滑
            if (target.canScrollVertically(-1)) {
                return;
            }
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                safeStopFling(target, type);
                consumed[1] = dy;
            } else {
                mTotalTranslationY -= dy;
                setTranslationY(mTotalTranslationY);
                consumed[1] = dy;
                currentScrollDirection = 1;
                isInDrag = true;
            }
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void setTranslationY(float translationY) {
        if (translationY < 0 ) {
            translationY = 0;
            mTotalTranslationY = 0;
        }
        super.setTranslationY(translationY);
    }

    private void startEnterAnim() {
        if (isEntered) {
            return;
        }
        isEntered = true;
        runAnim(height, mTotalTranslationY);
    }

    private void releaseSelf() {
        if (!isInDrag) {
            return;
        }
        resetView();
        mTotalTranslationY = 0;
        isInDrag = false;
    }

    private void resetView() {
        if (exit()) {
            return;
        }
        float from = mTotalTranslationY, to = mTotalTranslationY;
        if (currentScrollDirection == 0) {
            from = mTotalTranslationY;
            to = 100;
        } else if (currentScrollDirection == 1) {
            from = mTotalTranslationY;
            to = (1 - initHeightPercent) * height;
        }
        runAnim(from, to);
    }

    private void runAnim(float from, float to) {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(from, to);
            animator.setDuration(200);
            animator.addUpdateListener(animation -> {
                mTotalTranslationY = (float) animation.getAnimatedValue();
                setTranslationY(mTotalTranslationY);
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });
        } else {
            animator.setFloatValues(from, to);
        }
        animator.start();
    }

    private boolean exit() {
        if (currentScrollDirection == 1
                && mTotalTranslationY > (1 - initHeightPercent) * height && onQuitListener != null) {
            ObjectAnimator quitAnim = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, mTotalTranslationY, height);
            quitAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onQuitListener.onQuit();
                }
            });
            quitAnim.setDuration(200);
            quitAnim.start();
            return true;
        }
        return false;
    }

    public void closeMe() {
        onQuitListener.onBeginQuit();
        ObjectAnimator quitAnim = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, mTotalTranslationY, height);
        quitAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onQuitListener.onQuit();
            }
        });
        quitAnim.setDuration(200);
        quitAnim.start();
    }

    private void safeStopFling(View target, int type) {
        if (target instanceof NestedScrollView) {
            try {
                Method abortAnimatedScroll = NestedScrollView.class.getDeclaredMethod("abortAnimatedScroll");
                abortAnimatedScroll.setAccessible(true);
                abortAnimatedScroll.invoke(target);
            } catch (Exception e) {
                ViewCompat.stopNestedScroll(target, type);
            }
        }
    }

    private boolean isShowFull() {
        return mTotalTranslationY <= 100;
    }

    public void setEnterHeight(float screenPercent) {
        this.initHeightPercent = screenPercent;
        if (height != 0) {
            runAnim(mTotalTranslationY, (1 - initHeightPercent) * height);
        }
    }

    public void setOnQuitListener(OnQuitListener onQuitListener) {
        this.onQuitListener = onQuitListener;
    }

    public interface OnQuitListener {
        void onBeginQuit();

        void onQuit();
    }
}
