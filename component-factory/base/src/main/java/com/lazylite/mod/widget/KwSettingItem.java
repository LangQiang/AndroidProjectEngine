package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.basemodule.R;
import com.lazylite.mod.log.LogMgr;


public class KwSettingItem extends RelativeLayout {

    private final static String TAG = "KwSettingItem";

    private Context mContext;
    private TextView mTitleTv;
    private TextView mSubTitleTv;
    private TextView mSubTitleCenterTv;
    private View mSettingIcon;
    private KuwoSwitch mSwitch;
    private ImageView mChooseIcon;
    private ImageView mTextImage;

    private String mTitleStr;
    private String mSubTitleStr;
    private String mTitleCenter;
    private TextView rightText;
    private int type;
    private static final int SET_NONE = 0;
    private static final int SET_IMAGE = 1;
    private static final int SET_KW_SWITCH = 2;
    private static final int SET_CHOOSE = 3;
    private View mSpliteView;
    private boolean isStar;
    private boolean isLine;
    private boolean isTitleBold;

    public KwSettingItem(Context context) {
        this(context, null);
    }

    public KwSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public KwSettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_KwSetting);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.lrlite_base_settingitem, this);
        mTitleStr = array.getString(R.styleable.LRLiteBase_KwSetting_set_title);
        mTitleCenter = array.getString(R.styleable.LRLiteBase_KwSetting_set_center);
        mSubTitleStr = array.getString(R.styleable.LRLiteBase_KwSetting_set_subtitle);
        type = array.getInt(R.styleable.LRLiteBase_KwSetting_set_icon, SET_NONE);
        isStar = array.getBoolean(R.styleable.LRLiteBase_KwSetting_set_star, false);
        isLine = array.getBoolean(R.styleable.LRLiteBase_KwSetting_set_line, true);
        isTitleBold = array.getBoolean(R.styleable.LRLiteBase_KwSetting_set_title_bold, false);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleTv = (TextView) this.findViewById(R.id.tv_set_title);
        if (mTitleTv != null) {
            setTitle(mTitleStr);
            if(isTitleBold){
                mTitleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }

        if (mSubTitleStr != null) {
            mSubTitleTv = (TextView) this.findViewById(R.id.tv_set_sub_title);
            setSubTitle(mSubTitleStr);
        }

        mSubTitleCenterTv = (TextView) this.findViewById(R.id.tv_set_sub_center);
        setSubTitleCenter(mTitleCenter);

        mSettingIcon =  this.findViewById(R.id.iv_set_icon);
        mSwitch = (KuwoSwitch) this.findViewById(R.id.cb_set_switch);
        mChooseIcon = (ImageView) this.findViewById(R.id.iv_listen_auto_select_selected);
        rightText = (TextView) this.findViewById(R.id.tv_set_sub_center);
        mTextImage = (ImageView) this.findViewById(R.id.img_set_item_tag);
        mSpliteView = this.findViewById(R.id.setting_splite);
        LogMgr.i(TAG, "type:" + type);
        setIcon(type);
        if(isStar){
            findViewById(R.id.tv_star).setVisibility(VISIBLE);
        }

        mSpliteView.setVisibility(isLine ? View.VISIBLE : View.INVISIBLE);
    }

    private void setIcon(int type) {
        switch (type) {
            case SET_IMAGE:
                setSettingIcon();
                break;
            case SET_CHOOSE:
                setChoosedIcon();
                break;
            case SET_KW_SWITCH:
//				setSwitch();
                break;
            case SET_NONE:
                break;
            default:
                break;
        }
    }

    public void setExternalClickListener(OnClickListener listener) {
        this.setOnClickListener(listener);
    }

    public void setExternalClickListener(final OnCheckedChangeListenerForID listener) {
        mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckedChanged(getId(), isChecked);
                }
            }
        });
        mSwitch.setTag(getId());
    }

    public void setSwitchClickListener(OnClickListener onClickListener) {
        mSwitch.setOnClickListener(onClickListener);
    }

    //简单实现了中间文字点击事件
    public void setExternalCenterClickListener(final OnClickListenerForCenterID listener) {
        mSubTitleCenterTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickListener(getId());
                }
            }
        });
    }

    public void setSubTitleCenter(String subTitleCenter) {
        if (TextUtils.isEmpty(subTitleCenter)) {
            mSubTitleCenterTv.setVisibility(GONE);
        } else {
            mSubTitleCenterTv.setVisibility(VISIBLE);
            mSubTitleCenterTv.setText(subTitleCenter);
        }
    }

    public void setSubTitleCenterColor(int color) {
        mSubTitleCenterTv.setTextColor(color);
    }

    public void setVilsibleTitleCenter(int visible) {
        mSubTitleCenterTv.setVisibility(visible);
    }

    public interface OnCheckedChangeListenerForID {
        void onCheckedChanged(int itemId, boolean isChecked);
    }

    public interface OnClickListenerForCenterID {
        void onClickListener(int itemId);
    }

    public void setTitle(int resId) {
        setTitle(mContext.getString(resId));
    }

    public void setTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    public void setTitleImage(int resID) {
        mTextImage.setVisibility(View.VISIBLE);
        mTextImage.setImageResource(resID);
    }

    public void setSubTitle(int resId) {
        setSubTitle(mContext.getString(resId));
    }

    public void setSubTitle(CharSequence subTitle) {
        mSubTitleTv.setText(subTitle);
        mSubTitleTv.setVisibility(View.VISIBLE);
    }

    public void setRightTitle(String rightTitle) {
        rightText.setText(rightTitle);
        rightText.setVisibility(View.VISIBLE);
    }

    public void setSettingIcon() {
        mSwitch.setVisibility(View.GONE);
        mSettingIcon.setVisibility(View.VISIBLE);
    }

    public void goneSettingIcon(){
        if(mSettingIcon != null){
            mSettingIcon.setVisibility(View.GONE);
        }
    }

    public void setSwitch() {
        mSwitch.setVisibility(View.VISIBLE);
    }

    public void setChoosedIcon() {
        mSwitch.setVisibility(View.GONE);
    }

    public void setChecked(boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    public boolean getCheckedStatus() {
        return mSwitch.isChecked();
    }

    public void setChoosedIconVisible() {
        if (mChooseIcon.getVisibility() != View.VISIBLE)
            mChooseIcon.setVisibility(View.VISIBLE);
    }

    public void setChoosedIconInvisible() {
        if (mChooseIcon.getVisibility() != View.GONE)
            mChooseIcon.setVisibility(View.GONE);
    }

    public void setSpliteShow(boolean isShow) {
        if (mSpliteView != null) {
            mSpliteView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setSwitchRes(int idOn, int idOff) {
        if (mSwitch != null) {
            mSwitch.setTrackDrawableRes(idOn, idOff);
        }
    }

}


