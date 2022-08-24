package com.lazylite.mod.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.utils.UIHelper;


/**
 * @author ：qyh
 * date    ： 2021/1/20
 * describe：通用对话框，默认为两个按钮样式；也可以在创建的时候通过setSingleButton()方法设置成单按钮样式
 * ____________________
 * |       标题        |
 * |                  |
 * |     提示内容      |
 * |                  |
 * | 按钮1       按钮2 |
 * |__________________|
 */
public class CommonCenterDialog extends Dialog implements View.OnClickListener {
    private TextView rightButton, leftButton;
    private TextView titleTv;
    private TextView descriptionTv;
    private Activity mContext;
    // 控制显示单双按钮，默认双按钮
    private boolean mShowSingleButton;
    private boolean mShowTwoButton = true;
    // 设置按钮上文字颜色
    private int mRightTextColor;
    private int mLeftTextColor;
    private int mSingleTextColor;
    // 设置按钮上文字
    private String mLeftText;
    private String mRightText;
    private String mSingleText;
    // 设置标题和描述
    private String mTitle;
    private String mDescription;
    private boolean mIsLeft;
    private View.OnClickListener mRightClickListener;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mSingleClickListener;
    private Button btSingleClick;


    private CommonCenterDialog(Builder builder) {
        this(builder.context);
        this.mTitle = builder.title;
        this.mIsLeft = builder.isLeft;
        this.mContext = builder.context;
        this.mLeftText = builder.leftText;
        this.mRightText = builder.rightText;
        this.mSingleText = builder.singleText;
        this.mDescription = builder.description;
        this.mShowTwoButton = builder.twoButton;
        this.mLeftTextColor = builder.leftTextColor;
        this.mShowSingleButton = builder.singleButton;
        this.mRightTextColor = builder.rightTextColor;
        this.mSingleTextColor = builder.singleTextColor;
        this.mLeftClickListener = builder.leftClickListener;
        this.mRightClickListener = builder.rightClickListener;
        this.mSingleClickListener = builder.singleClickListener;

    }

    private CommonCenterDialog(@NonNull Context context) {
        super(context, R.style.LRLiteBase_AlertDialogWhite);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lrlite_base_dialog_center_view);
        initView();
        setTextStyle();
        setButtonStyle(leftButton, mLeftText, mLeftTextColor);
        setButtonStyle(rightButton, mRightText, mRightTextColor);
        setButtonStyle(btSingleClick, mSingleText, mSingleTextColor);

        setCanceledOnTouchOutside(false);
    }

    /**
     * 设置标题和描述
     */
    private void setTextStyle() {
        if (!TextUtils.isEmpty(mTitle)) {
            titleTv.setText(mTitle);
        }
        setDescriptionText(mDescription);

        if (mIsLeft) {
            descriptionTv.setGravity(Gravity.LEFT);
        }
    }

    public void setDescriptionText(String mDescription) {
        if (!TextUtils.isEmpty(mDescription) && descriptionTv != null) {
            descriptionTv.setText(mDescription);
        }
    }

    /**
     * 设置Button显示文字、颜色
     *
     * @param button
     * @param text
     * @param textColor
     */
    private void setButtonStyle(TextView button, String text, int textColor) {
        if (button != null && button.getVisibility() == View.VISIBLE) {
            if (!TextUtils.isEmpty(text)) {
                button.setText(text);
            }
            if (textColor != 0) {
                button.setTextColor(getContext().getResources().getColor(textColor));
            }
        }
    }


    private void initView() {
        rightButton = findViewById(R.id.yes);
        leftButton = findViewById(R.id.no);
        titleTv = findViewById(R.id.tv_title);
        descriptionTv = findViewById(R.id.tv_description);
        btSingleClick = findViewById(R.id.bt_single_click);
        LinearLayout llTwobuttonRootview = findViewById(R.id.ll_twobutton_rootview);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        btSingleClick.setOnClickListener(this);

        if (mShowSingleButton) {
            llTwobuttonRootview.setVisibility(View.GONE);
            btSingleClick.setVisibility(View.VISIBLE);
        } else {
            llTwobuttonRootview.setVisibility(View.VISIBLE);
            btSingleClick.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.yes) {
            if (mRightClickListener != null) {
                mRightClickListener.onClick(v);
            }
        }

        if (viewId == R.id.no) {
            if (mLeftClickListener != null) {
                mLeftClickListener.onClick(v);
            }
        }

        if (viewId == R.id.bt_single_click) {
            if (mSingleClickListener != null) {
                mSingleClickListener.onClick(v);
            }
        }
        closeDialog();
    }

    public static class Builder {
        private Activity context;
        private boolean singleButton;
        private boolean twoButton = true;
        private boolean isLeft;
        private int rightTextColor;
        private int leftTextColor;
        private int singleTextColor;

        private String leftText;
        private String rightText;
        private String singleText;
        private String title;
        private String description;

        private View.OnClickListener rightClickListener;
        private View.OnClickListener leftClickListener;
        private View.OnClickListener singleClickListener;


        public Builder(Activity context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }


        /**
         * 设置一个点击按钮样式
         *
         * @return
         */
        public Builder setSingleButton() {
            this.singleButton = true;
            this.twoButton = false;
            return this;
        }

        /**
         * 设置一个按钮的
         *
         * @param color
         * @return
         */
        public Builder setSingleTextColor(int color) {
            this.singleTextColor = color;
            return this;
        }

        /**
         * 设置描述文字是否左对齐
         *
         * @param isLeft
         * @return
         */
        public Builder setDescStyle(boolean isLeft) {
            this.isLeft = isLeft;
            return this;
        }

        /**
         * 两个按钮情况，设置右面
         *
         * @param color
         * @return
         */
        public Builder setRightTextColor(int color) {
            this.rightTextColor = color;
            return this;
        }

        /**
         * 两个按钮情况，设置左面
         *
         * @param color
         * @return
         */
        public Builder setLeftTextColor(int color) {
            this.leftTextColor = color;
            return this;
        }

        /**
         * 设置按钮Text
         *
         * @param text
         * @return
         */
        public Builder setSingleText(String text) {
            this.singleText = text;
            return this;
        }


        public Builder setRightText(String text) {
            this.rightText = text;
            return this;
        }


        public Builder setLeftText(String text) {
            this.leftText = text;
            return this;
        }

        public Builder setRightClickListener(View.OnClickListener listener) {
            this.rightClickListener = listener;
            return this;
        }

        public Builder setLeftClickListener(View.OnClickListener listener) {
            this.leftClickListener = listener;
            return this;
        }

        public Builder setSingleClickListener(View.OnClickListener listener) {
            this.singleClickListener = listener;
            return this;
        }

        public CommonCenterDialog build() {
            return new CommonCenterDialog(this);
        }
    }


    public void showDialog() {
        if (mContext != null && !mContext.isFinishing() && !isShowing()) {
            show();
        }
    }

    public void closeDialog() {
        UIHelper.safeDismissDialog(this);
    }
}
