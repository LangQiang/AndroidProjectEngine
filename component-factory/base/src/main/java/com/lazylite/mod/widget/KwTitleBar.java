package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.example.basemodule.R;
import com.lazylite.mod.App;

/**
 * CommonHeaderFragment is deprecated ,Use this class build titlebar
 * Created by LiTiancheng on 2015/7/24.
 */
public class KwTitleBar extends RelativeLayout {

    private TextView mRightIconFontBtn;

    public View getMainTitleTextView() {
        return mMainTitle;
    }

    public interface OnBackClickListener {
        void onBackStack();
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    private Context mContext;

    private TextView mMainTitle;
    private TextView mSubTitle;
    private TextView mCancel;
    private TextView mComplete;
    private TextView mLeftCloseTV;

    private ImageView mLeftBtn;
    private ImageView mRightBtn;
    private ImageView mExtendBtn;
    private View mRootView;
    private View mRightBtnTip;
    private View mExtendBtnTip;
    private View mLeftPanel;
    private View mRightPanel;
    private View mExtendPanel;
    private LinearLayout mTitlePanel;
    private CheckBox mCheckBox;
    private RelativeLayout mRightContainor;//右边加一人布局, 可以在里面放view
    private RelativeLayout mRightBtnPanel;

    private boolean isWhiteTheme = false;

    public KwTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KwTitleBar(Context context) {
        super(context);
    }

    public KwTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        isWhiteTheme = true;
        initView();
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_KwTitleBar);
        int bkgColorId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_bgColor, -1);
        int mainTitleStrId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_ktbTitle, -1);
        int leftBackStrId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_leftBackStr,-1);
        int leftCancelStrId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_leftCancelStr, -1);
        int rightStrId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_rightStr, -1);
        int extendIconId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_extentIconId, -1);
        int leftIconId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_leftIconId, -1);
        int rightIconId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_rightIconId, -1);
        int checkBoxBkgId = array.getResourceId(R.styleable.LRLiteBase_KwTitleBar_checkBoxBkg, -1);
        boolean showCheckBox = array.getBoolean(R.styleable.LRLiteBase_KwTitleBar_showCheckBox, false);
        array.recycle();
        if (bkgColorId != -1) {
            mRootView.setBackgroundResource(bkgColorId);
        }
        if (mainTitleStrId != -1) {
            setMainTitle(mainTitleStrId);
        }
        if (extendIconId != -1) {
            setExtendIcon(extendIconId);
        }
        if (rightIconId != -1) {
            setRightIcon(rightIconId);
        }
        if (leftIconId != -1) {
            setLeftIcon(leftIconId);
        }
        if (leftCancelStrId != -1) {
            mLeftPanel.setVisibility(View.VISIBLE);
            mLeftBtn.setVisibility(View.GONE);
            mCancel.setText(leftCancelStrId);
            mCancel.setVisibility(View.VISIBLE);
        }
        if(leftBackStrId != -1){
            mLeftPanel.setVisibility(View.VISIBLE);
            mLeftBtn.setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);
            mLeftCloseTV.setVisibility(View.VISIBLE);
            mLeftCloseTV.setText(leftBackStrId);
        }

        if (rightStrId != -1) {
            setRightTextBtn(rightStrId);
        }
        if (showCheckBox) {
            showCheckBox();
            if (checkBoxBkgId != -1) {
                mCheckBox.setBackgroundResource(checkBoxBkgId);
            } else {
                mCheckBox.setBackgroundResource(R.drawable.lrlite_base_checkbox_style);
            }
        }
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.lrlite_base_titlebar, this, true);
        mRootView = getRootView();

        mLeftPanel = findViewById(R.id.back_panel);
        mLeftBtn = findViewById(R.id.btn_back);
        mCancel =  findViewById(R.id.btn_cancel);
        mLeftCloseTV = findViewById(R.id.btn_back_icon_font);

        mTitlePanel =  findViewById(R.id.title_panel);
        mMainTitle = findViewById(R.id.main_title);
        mSubTitle = findViewById(R.id.sub_title);

        mRightPanel = findViewById(R.id.right_panel);
        mRightBtn = findViewById(R.id.btn_settings);
        mRightIconFontBtn = findViewById(R.id.right_icon_view);
        mRightBtnPanel =  findViewById(R.id.settings_panel);
        mRightBtnTip = findViewById(R.id.btn_settings_tips);
        mComplete =  findViewById(R.id.btn_complete);
        mCheckBox =  findViewById(R.id.check_all);

        mExtendPanel = findViewById(R.id.extend_panel);
        mExtendBtn =  findViewById(R.id.btn_extend);
        mExtendBtnTip = findViewById(R.id.btn_extend_tips);

        mRightContainor =  findViewById(R.id.title_right_container);

        setStyleByThemeType(isWhiteTheme);

        setGravity(Gravity.CENTER_VERTICAL);
    }

    public KwTitleBar setWhiteBar(){
        setStyleByThemeType(true);
        if (mRootView!=null){
            mRootView.setBackgroundResource(R.color.white);
        }
        return this;
    }

    public KwTitleBar setStyleByThemeType(boolean isWhiteTheme){
        int titleColor;
        int descColor;
        this.isWhiteTheme = isWhiteTheme;
          mLeftBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.lrlite_base_back_black));

        if (isWhiteTheme) {
            //跨进程，Skinmanager异步初始化，拿不到正常的资源，这里都写死
            titleColor = Color.parseColor("#212121");
            descColor = Color.parseColor("#707070");
            mLeftBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.lrlite_base_back_black));
        } else {
            titleColor = mContext.getResources().getColor(R.color.white);
            descColor = mContext.getResources().getColor(R.color.white);
            mLeftBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.lrlite_base_back_white));
        }

        mCancel.setTextColor(titleColor);
        mMainTitle.setTextColor(titleColor);
//        mComplete.setTextColor(titleColor);
        mSubTitle.setTextColor(descColor);
        return this;
    }

    public KwTitleBar setMainTitle(CharSequence title) {
        mMainTitle.setText(title);
        mMainTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public String getMainTitle() {
        return mMainTitle.getText().toString();
    }

    public KwTitleBar setMainTitle(int titleResId) {
        mMainTitle.setText(titleResId);
        mMainTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setSubTitle(CharSequence title) {
        mSubTitle.setText(title);
        mSubTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setSubTitle(int titleResId) {
        mSubTitle.setText(titleResId);
        mSubTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public void setSubTitleVisibility(boolean visibility) {
        mSubTitle.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    public KwTitleBar setExtendIcon(int logoResId) {
        mExtendPanel.setVisibility(View.VISIBLE);
        if (isWhiteTheme) {
            mExtendBtn.setBackgroundDrawable(App.getInstance().getResources().getDrawable(logoResId));
        } else {
            mExtendBtn.setBackgroundResource(logoResId);
        }
        mExtendBtn.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setExtendBtnListener(OnClickListener listener) {
        mExtendPanel.setVisibility(View.VISIBLE);
        mExtendBtn.setVisibility(View.VISIBLE);
        mExtendBtn.setOnClickListener(listener);
        return this;
    }

    public KwTitleBar setExtendButton(int imageResId, OnClickListener listener) {
        if (imageResId > 0) {
            setExtendIcon(imageResId);
            if (listener != null) {
                mExtendBtn.setOnClickListener(listener);
            }
        } else {
            mExtendPanel.setVisibility(View.GONE);
            mExtendBtn.setVisibility(View.GONE);
        }
        return this;
    }

    public KwTitleBar setRightIcon(int rightIconId) {
        return setRightIcon(rightIconId, isWhiteTheme);
    }

    public KwTitleBar setRightIconFont(int iconFontResId) {
        mRightPanel.setVisibility(View.VISIBLE);
        mRightIconFontBtn.setText(App.getInstance().getString(iconFontResId));
        mRightIconFontBtn.setVisibility(VISIBLE);
        mRightBtn.setVisibility(View.GONE);
        mCheckBox.setVisibility(View.GONE);
        mComplete.setVisibility(View.GONE);
        return this;
    }

    public KwTitleBar setRightIcon(int rightIconId, boolean isWhiteTheme) {
        mRightPanel.setVisibility(View.VISIBLE);
        if (isWhiteTheme) {
            mRightBtn.setImageDrawable(App.getInstance().getResources().getDrawable(rightIconId));
        } else {
            mRightBtn.setImageResource(rightIconId);
        }
        mRightBtn.setVisibility(View.VISIBLE);
        mCheckBox.setVisibility(View.GONE);
        mComplete.setVisibility(View.GONE);
        return this;
    }

    public KwTitleBar setRightIconMargin(int marginRight){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRightBtnPanel.getLayoutParams();
        params.rightMargin = marginRight;
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        mRightBtnPanel.setLayoutParams(params);
        return this;
    }


    public void setRightIconVisible(boolean visible) {
        if (visible) {
            mRightBtn.setVisibility(VISIBLE);
        } else {
            mRightBtn.setVisibility(GONE);
        }
    }

    public KwTitleBar showCheckBox() {
        mRightPanel.setVisibility(View.VISIBLE);
        mCheckBox.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
        mComplete.setVisibility(View.GONE);
        return this;
    }

    public KwTitleBar setRightTextBtn(int resId) {
        mRightPanel.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
        mCheckBox.setVisibility(View.GONE);
        mComplete.setText(resId);
        mComplete.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setRightTextBtn(CharSequence str) {
        mRightPanel.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
        mCheckBox.setVisibility(View.GONE);
        mComplete.setText(str);
        mComplete.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setRightTextBtnSize(int typeValue, int size) {
        mComplete.setTextSize(typeValue, size);
        return this;
    }

    public KwTitleBar setRightTextBtnDrawable(int rightResource) {
        if (rightResource == 0){
            return this;
        }
        Drawable drawable;
        if (isWhiteTheme){
            drawable = App.getInstance().getResources().getDrawable(rightResource);
        } else {
            drawable = getResources().getDrawable(rightResource);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mComplete.setCompoundDrawables(null, null, drawable, null);
        return this;
    }

    public KwTitleBar setRightListener(final OnRightClickListener l) {
        if (l != null) {
            mRightPanel.setVisibility(View.VISIBLE);
            mRightPanel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onRightClick();
                }
            });
            mRightPanel.setFocusable(true);
        }
        return this;
    }

    public KwTitleBar setLeftIcon(int leftIconId) {
        mLeftPanel.setVisibility(View.VISIBLE);
        if (isWhiteTheme){
            mLeftBtn.setBackgroundDrawable(App.getInstance().getResources().getDrawable(leftIconId));
        } else {
            mLeftBtn.setImageResource(leftIconId);
        }
        mLeftBtn.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 设置左边btn的图, 这个<b>不会</b>从另一个包里拿资源
     * @param leftIconId 资源id
     */
    public KwTitleBar setSimpleLeftIcon(int leftIconId) {
        mLeftPanel.setVisibility(View.VISIBLE);
        mLeftBtn.setBackgroundResource(leftIconId);
        mLeftBtn.setVisibility(View.VISIBLE);
        return this;
    }

    public KwTitleBar setBackListener(final OnBackClickListener l) {
        if (l != null) {
            mLeftPanel.setVisibility(View.VISIBLE);
            mLeftPanel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onBackStack();
                }
            });
            mLeftPanel.setFocusable(true);
        }
        return this;
    }

    public KwTitleBar setBackListener(final OnBackClickListener l, boolean leftVisiable) {
        if (l != null) {
            if (leftVisiable) {
                mLeftPanel.setVisibility(View.VISIBLE);
            } else {
                mLeftPanel.setVisibility(View.INVISIBLE);
            }
            mLeftPanel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onBackStack();
                }
            });
            mLeftPanel.setFocusable(true);
        }
        return this;
    }


    public KwTitleBar showSettingsTips(boolean show) {
        mRightBtnTip.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public KwTitleBar showExtendButtonTips(boolean show) {
        mExtendBtnTip.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public KwTitleBar setTitleLeftAlign() {
        mTitlePanel.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        return this;
    }

    public KwTitleBar setRightColor(int colorRes) {
        mComplete.setTextColor(colorRes);
        return this;
    }

    public KwTitleBar setRightStrVisible(int visibility) {
        mComplete.setVisibility(visibility);
        return this;
    }

    /**
     * 设置扩展的可见性
     *
     * @param visibility
     * @return
     */
    public KwTitleBar setExtendPanelVisibility(int visibility) {
        mExtendPanel.setVisibility(visibility);
        return this;
    }

    /**
     * 设置右侧按钮可见性
     *
     * @param visibility
     * @return
     */
    public KwTitleBar setRightPanelVisibility(int visibility) {
        mRightPanel.setVisibility(visibility);
        return this;
    }

    /**
     * 将返回设置为"取消"
     *
     * @return
     */
    public KwTitleBar setCancelText() {
        mLeftPanel.setVisibility(View.VISIBLE);
        mLeftBtn.setVisibility(View.GONE);
        mCancel.setVisibility(VISIBLE);
        mCancel.setText("取消");
        return this;
    }

    public KwTitleBar setCancelText(String cancelText) {
        mLeftPanel.setVisibility(View.VISIBLE);
        mLeftBtn.setVisibility(View.GONE);
        mCancel.setVisibility(VISIBLE);
        mCancel.setText(cancelText);
        return this;
    }

    public RelativeLayout getRightContainer() {
        return mRightContainor;
    }

    /**
     * 在右边放一个view
     * @param view
     * @return
     */
    public KwTitleBar setRightContainerView(View view) {
        mRightContainor.setVisibility(VISIBLE);
        mRightContainor.removeAllViews();
        mRightContainor.addView(view);
        return this;
    }

    public View getTitleView() {
        return mTitlePanel;
    }

    /**
     * 返回右边的布局
     * 有时候文字过多 导致距离右边没有边距
     */
    public View getRightPanel() {
        return mRightPanel;
    }

    /**
     * 设置主title文字颜色
     * @param color 颜色
     */
    public void setMainTitleColor(int color) {
        if (mMainTitle != null) {
            mMainTitle.setTextColor(color);
        }
    }

    /**
     * Right 文字
     * @return TextView
     */
    public TextView getComplete(){
        return mComplete;
    }

    /**
     * 显示隐藏 Right 文字
     * @param isVisibility true显示
     */
    public void setCompleteVisibility(boolean isVisibility) {
        if (mComplete != null) {
            mComplete.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        }
    }

    public ImageView getRightIcoBtn() {
        return mRightBtn;
    }

    public ImageView getLeftIcoBtn() {
        return mLeftBtn;
    }


    /**
     * 整体内容颜色切换
     */
    public void setContentTextColor(int textColor) {
        mMainTitle.setTextColor(textColor);
        mSubTitle.setTextColor(textColor);
    }

    public void setRightTitle(@StringRes int text){
        mComplete.setVisibility(VISIBLE);
        mComplete.setText(text);
    }

    public View getBackView(){
        return mLeftPanel ;
    }

    public View getRightView(){
        return mRightPanel;
    }
}
