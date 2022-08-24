package com.lazylite.mod.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.basemodule.R;

/**
 * Created by lxh on 2015/9/9.
 */
public class KwTipView extends LinearLayout implements View.OnClickListener {
    public static final String JUMP_BTN_DES_OFFLINE_ALL = "查看更多精彩内容";

    private boolean isSkinEnable = true;
    private ImageView imageTip;
    private TextView topTextTip;
    private TextView jumpButton;

    private View loadingView;
    private View tipRootView;

    private Context mContext;

    private OnButtonClickListener listener;
    private OnTipButtonClickListener mListener;

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(v);
        }
    }

    public boolean isSkinEnable() {
        return isSkinEnable;
    }

    public void setSkinEnable(boolean skinEnable) {
        isSkinEnable = skinEnable;
    }

    public interface OnButtonClickListener {
        void onTopButtonClick(View v);

        void onBottomButtonClick(View v);
    }

    public interface OnTipButtonClickListener {
        void onClick(View v);
    }

    public KwTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.kw_tip_view, this, true);
        setGravity(Gravity.CENTER);
        imageTip = (ImageView) getRootView().findViewById(R.id.image_tip);
        topTextTip = (TextView) getRootView().findViewById(R.id.top_text_tip);
        jumpButton = getRootView().findViewById(R.id.jump_button);
        loadingView = getRootView().findViewById(R.id.loading_view);
        tipRootView = getRootView().findViewById(R.id.tip_root_view);
        jumpButton.setOnClickListener(this);
    }

    /**
     * 设置顶部的提示文本
     *
     * @param res
     */
    public void setTipImage(int res) {
        showTip();
        if (res == -1) {
            imageTip.setVisibility(View.GONE);
        } else {
            imageTip.setVisibility(View.VISIBLE);
            imageTip.setImageResource(res);
        }
    }

    /**
     * 设置底部的提示文本
     *
     * @param res
     */
    public void setTopTextTip(int res) {
        if (res == -1) {
            topTextTip.setVisibility(View.GONE);
        } else {
            topTextTip.setVisibility(View.VISIBLE);
            topTextTip.setText(mContext.getResources().getString(res));
        }
    }

    public void setTopTextTip(String text) {
        if (TextUtils.isEmpty(text)) {
            topTextTip.setVisibility(View.GONE);
        } else {
            topTextTip.setVisibility(View.VISIBLE);
            topTextTip.setText(text);
        }
    }

    /**
     * 按钮设置监听器
     *
     * @param listener
     */
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public void setOnTipButtonClickListener(OnTipButtonClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置刷新监听
     *
     * @param listener
     */
    public void setOnRefreshListener(OnClickListener listener) {
        this.setOnClickListener(listener);
    }


    /**
     * 隐藏提示view
     * 隐藏的时候会把说有的view都gone掉，防止缓存
     */
    public void hideTip() {
        setVisibility(View.GONE);
        goneAllView();
    }


    /**
     * 显示提示view
     * 显示之前把所有的view在隐藏一边防止有的view还是缓存状态
     */
    private void showTip() {
        setVisibility(View.VISIBLE);
        goneAllView();
    }


    /**
     * 隐藏掉所有的提示
     */
    public void goneAllView() {
        imageTip.setVisibility(View.GONE);
        topTextTip.setVisibility(View.GONE);
        jumpButton.setVisibility(View.GONE);
    }

    /**
     * 提示View的类型
     */
    public enum TipType {
        NO_NET, NO_WIFI, NO_CONNECT, NO_CONTENT, HIDE
    }

    private void showTip(int imgRes, int topTipRes, int jumpStrResId) {
        setVisibility(View.VISIBLE);
        if (imgRes == -1) {
            imageTip.setVisibility(View.GONE);
        } else {
            imageTip.setVisibility(View.VISIBLE);
            imageTip.setImageResource(imgRes);
        }

        if (topTipRes == -1) {
            topTextTip.setVisibility(View.GONE);
        } else {
            topTextTip.setVisibility(View.VISIBLE);
            topTextTip.setText(mContext.getResources().getString(topTipRes));
        }

        if (jumpStrResId == -1) {
            jumpButton.setVisibility(View.GONE);
        } else {
            jumpButton.setVisibility(View.VISIBLE);
            jumpButton.setText(mContext.getResources().getString(jumpStrResId));
        }

    }
    public void setTransparent() {
        getRootView().findViewById(R.id.root_ll).setBackgroundResource(R.color.transparent);
        if(topTextTip!=null){
            topTextTip.setBackgroundResource(R.drawable.common_btn_stroke_on_wite_background_selector);
        }
    }


    public void setJumpButtonClick(final OnClickListener listener){
        if(null == listener){
            return;
        }
        jumpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
    }

    public void setTopTextTipColor(int color){
        if (topTextTip != null){
            topTextTip.setTextColor(getResources().getColor(color));
        }
    }

    public TextView getTopTextTip(){
        return topTextTip;
    }

    /**
     * 只有这一个方法
     *
     * 0表示根据类型展示默认 -1表示隐藏 有效resId会显示该部分提示
     */
    public void showTip(TipType showTipType, int imgRes, int tipRes, int jumpStrResId) {
        showTip();
        switch (showTipType) {
            case NO_WIFI:
                showTip(imgRes == 0 ? R.drawable.base_list_error : imgRes, tipRes == 0 ? R.string.list_onlywifi : tipRes, jumpStrResId == 0 ? -1 : jumpStrResId);
                break;

            case NO_NET:
                showTip(imgRes == 0 ? R.drawable.base_list_error : imgRes, tipRes == 0 ? R.string.search_result_search_nonet_tip : tipRes, jumpStrResId == 0 ? -1 : jumpStrResId);
                break;
            case NO_CONNECT:
                showTip(imgRes == 0 ? R.drawable.base_list_error : imgRes, tipRes == 0 ? R.string.search_result_search_noconnect_tip : tipRes, jumpStrResId == 0 ? -1 : jumpStrResId);
                break;
            case NO_CONTENT:
                showTip(imgRes == 0 ? R.drawable.base_list_empty : imgRes, tipRes == 0 ? R.string.search_result_search_nocontent_tip : tipRes, jumpStrResId == 0 ? -1 : jumpStrResId);
                break;
            case HIDE:
                hideTip();
                break;
            default:
                break;
        }
    }

    public void setUnChangeTheme() {
        if(isSkinEnable){
            imageTip.clearColorFilter();
            topTextTip.setTextColor(mContext.getResources().getColor(R.color.skin_tip_color));
            jumpButton.setTextColor(mContext.getResources().getColor(R.color.skin_title_important_color));
        }
    }

    /**
     * 为了在有Header的fragment中显示居中在上面加了marginbottom,
     * 那么在没有Header的fragment中就要使用这个方法去掉讨厌的Margin
     *
     */
    public void layoutInCenterVertical() {
        View child = getChildAt(0);
        if (child != null) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.bottomMargin = 0;
            lp.topMargin = 0;
            child.setLayoutParams(lp);
        }
    }

    public void setForceWhiteBackground() {
        imageTip.clearColorFilter();
        topTextTip.setTextColor(mContext.getResources().getColor(R.color.LRLiteBase_cl_black_99));
        jumpButton.setTextColor(mContext.getResources().getColor(R.color.LRLiteBase_cl_black_33));
    }

    public void setForceBlurBackground(){
        imageTip.clearColorFilter();
        int whiteColor = mContext.getResources().getColor(R.color.LRLiteBase_cl_white_alpha_60);
        topTextTip.setTextColor(whiteColor);

        jumpButton.setTextColor(whiteColor);
        jumpButton.setBackgroundResource(R.drawable.btn_tip_view_jump_white_selector);
    }

    public ImageView getImageTip(){
        return imageTip;
    }

    public View getTipRootView() {
        return tipRootView;
    }

    private int loadingViewOptId = 0;

    public void setLoadingViewGone(boolean isGone, long delay) {
        if (loadingView == null) {
            return;
        }
        int currentOpt = ++loadingViewOptId;
        if (delay <= 0) {
            loadingView.setVisibility(isGone ? View.GONE : View.VISIBLE);
        } else {
            loadingView.postDelayed(() -> {
                if (loadingView == null || currentOpt != loadingViewOptId) {
                    return;
                }
                loadingView.setVisibility(isGone ? View.GONE : View.VISIBLE);
            }, delay);
        }
    }

}
