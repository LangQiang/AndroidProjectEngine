package com.lazylite.mod.widget.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.basemodule.R;


/**
 * 默认使用换肤的图片
 */

public class CommonLoadingView extends RelativeLayout {
    public LottieAnimationView mLoadingView;
    private TextView mMessageTextView;

    public CommonLoadingView(Context context) {
        this(context, null);
    }

    public CommonLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.lrlite_base_item_loading, this);
        mLoadingView = findViewById(R.id.lottie_loading);
        mMessageTextView = findViewById(R.id.tv_message);
        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_CommonLoadingView);
            int textColor = ta.getColor(R.styleable.LRLiteBase_CommonLoadingView_msgTextColor, -1);
            if (textColor != -1) {
                setTextColor(textColor);
            }
            int textSize = ta.getDimensionPixelSize(R.styleable.LRLiteBase_CommonLoadingView_msgTextSize, -1);
            if (textSize != -1) {
                setTextSize(textSize);
            }
            String text = ta.getString(R.styleable.LRLiteBase_CommonLoadingView_msgText);
            setTextMessage(text);
            int textMarginTop = ta.getDimensionPixelOffset(R.styleable.LRLiteBase_CommonLoadingView_msgTextMarginTop, -1);
            if (textMarginTop != -1) {
                setTextMarginTop(textMarginTop);
            }

        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE) {
            startAnimation();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (mLoadingView != null && !mLoadingView.isAnimating()) {
            mLoadingView.playAnimation();
        }
    }

    private void stopAnimation() {
        if (mLoadingView != null && mLoadingView.isAnimating()) {
            mLoadingView.cancelAnimation();
        }
    }

    public void setTextMessage(CharSequence text) {
        if (mMessageTextView == null || TextUtils.isEmpty(text)) {
            return;
        }
        mMessageTextView.setVisibility(View.VISIBLE);
        mMessageTextView.setText(text);
    }

    public void setTextColor(int color) {
        if (mMessageTextView != null) {
            mMessageTextView.setTextColor(color);
        }
    }

    public void setTextSize(int px) {
        if (mMessageTextView != null) {
            mMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        }
    }

    public void setTextMarginTop(int px) {
        if (mMessageTextView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mMessageTextView.getLayoutParams();
            lp.topMargin = px;
        }
    }
}
