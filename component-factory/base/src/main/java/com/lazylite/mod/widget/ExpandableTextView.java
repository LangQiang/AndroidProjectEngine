package com.lazylite.mod.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.basemodule.R;
import com.lazylite.mod.utils.ScreenUtility;

/**
 * Created by zhouchong on 2020/3/18.
 * 为了ui nb的需求特意定制的n个TextView组合控件
 */
public class ExpandableTextView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {
    private static final int MAX_LINES = 3;
    private static final int BUTTON_TEXT_COLOR = Color.parseColor("#ff000000");
    private int dp_4;
    private int dp_12;
    private TextView mTvContent;
    private TextView mTvLastLine;
    private TextView mTvMore;

    private CharSequence mText;
    private Runnable mLayoutTextRunnable;
    private boolean isExpand;
    private Drawable mUpArrowDrawable;
    private OnExpandChangedListener mOnExpandChangedListener;

    public interface OnExpandChangedListener {
        void onExpandChanged(boolean isExpand);
    }

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_expandable_text_view, this);
        mTvContent = findViewById(R.id.tv_content);
        mTvLastLine = findViewById(R.id.tv_last_line);
        mTvMore = findViewById(R.id.tv_more);
        mUpArrowDrawable = getArrowUpDrawable();
        dp_4 = ScreenUtility.dip2px(4);
        dp_12 = ScreenUtility.dip2px(12);
    }

    public void setOnExpandChangedListener(OnExpandChangedListener listener) {
        mOnExpandChangedListener = listener;
    }

    public void setText(CharSequence text) {
        mText = text;
        mTvContent.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        mTvContent.setText(mText);
        mLayoutTextRunnable = new LayoutTextRunnable(mTvContent, String.valueOf(mText));
        mTvContent.post(mLayoutTextRunnable);
        mTvContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private class LayoutTextRunnable implements Runnable {
        private TextView mTarget;
        private String mContent;

        LayoutTextRunnable(TextView textView, String content) {
            mTarget = textView;
            mContent = content;
        }

        @Override
        public void run() {
            if (mTarget != null && !TextUtils.isEmpty(mContent)) {
                layoutText();
                if (mOnExpandChangedListener != null) {
                    mOnExpandChangedListener.onExpandChanged(isExpand);
                }
            }
        }

        private void layoutText() {
            Layout layout = mTarget.getLayout();
            int lineCount = mTarget.getLineCount();
            try {
                if (isExpand) {
                    mTvContent.setText(mText);
                    mTvContent.setMaxLines(Integer.MAX_VALUE);
                    mTvContent.setEllipsize(null);
                    mTvLastLine.setText("收起全部");
                    mTvLastLine.setCompoundDrawables(null, null, mUpArrowDrawable, null);
                    mTvLastLine.setTextColor(BUTTON_TEXT_COLOR);
                    mTvLastLine.setPadding(200, 0, 200, 0);
                    LayoutParams lp = (LayoutParams) mTvLastLine.getLayoutParams();
                    lp.width = LayoutParams.WRAP_CONTENT;
                    lp.topMargin = dp_12;
                    mTvLastLine.setVisibility(VISIBLE);
                    mTvLastLine.setGravity(Gravity.CENTER);
                    mTvMore.setVisibility(GONE);
                } else {
                    if (lineCount > MAX_LINES) {
                        int lastLine = MAX_LINES - 1;
                        String lastLineText = mContent.substring(layout.getLineStart(lastLine), layout.getLineEnd(lastLine));
                        mTvLastLine.setText(lastLineText + "...");
                        mTvLastLine.setVisibility(VISIBLE);
                        mTvLastLine.setCompoundDrawables(null, null, null, null);
                        mTvLastLine.setTextColor(getResources().getColor(R.color.black60));
                        mTvLastLine.setPadding(0, 0, 0, 0);
                        LayoutParams lp = (LayoutParams) mTvLastLine.getLayoutParams();
                        lp.width = LayoutParams.MATCH_PARENT;
                        lp.topMargin = dp_4;
                        mTvLastLine.setGravity(Gravity.START);
                        mTvMore.setVisibility(VISIBLE);
                        mTvContent.setMaxLines(MAX_LINES - 1);
                        mTvContent.setOnClickListener(ExpandableTextView.this);
                        mTvMore.setOnClickListener(ExpandableTextView.this);
                        mTvLastLine.setOnClickListener(ExpandableTextView.this);
                    } else {
                        mTvMore.setVisibility(GONE);
                        mTvLastLine.setVisibility(GONE);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private Drawable getArrowUpDrawable() {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(R.drawable.ic_arrow_up, null);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_arrow_up);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable.setColorFilter(new PorterDuffColorFilter(BUTTON_TEXT_COLOR, PorterDuff.Mode.SRC_ATOP));
        return drawable;
    }

    @Override
    public void onClick(View v) {
        isExpand = !isExpand;
        mTvContent.post(mLayoutTextRunnable);
    }
}
