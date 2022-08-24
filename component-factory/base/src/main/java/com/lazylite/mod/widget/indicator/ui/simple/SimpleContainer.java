package com.lazylite.mod.widget.indicator.ui.simple;

import android.content.Context;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.base.CommonContainer;
import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

import java.util.List;


/**
 * @author DongJr
 *
 * @date 2018/5/28.
 *
 * 原则上适用于所有indicator显示，
 * 如果不满足需求，请重写相关方法或重写此类.
 * 禁止用变量控制！！！
 */
public class SimpleContainer extends CommonContainer {

    protected List<String> mTitles;
    protected float mTextSize = 16f;
    private int mTextPadding = IndicatorHelper.dip2px(20);

    public SimpleContainer(@NonNull Context context) {
        super(context);
    }

    public void setTitles(List<String> titles){
        mTitles = titles;
    }

    public void setTextSize(float textSize){
        mTextSize = textSize;
    }

    public void setTextPadding(int padding){
        mTextPadding = IndicatorHelper.dip2px(padding);
    }

    @Override
    public IPagerTitle getTitleView(Context context, int index) {
        SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
        simplePagerTitleView.setNormalColorRid(R.color.black80);
        simplePagerTitleView.setSelectedColorRid(R.color.rgbFFFF5400);
        simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);

        //这里支持设置title集合，也支持单个设置
        if (mTitles != null && mTitles.size() > index){
            simplePagerTitleView.setText(mTitles.get(index));
        } else {
            simplePagerTitleView.setText(provideIndicatorTitle(index));
        }

        if (getTabMode() == CommonContainer.MODE_SCROLLABLE || getTabMode() == CommonContainer.MODE_FIXED_SPACE){
            simplePagerTitleView.setPadding(mTextPadding, 0, mTextPadding, 0);
        }
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return new SimpleLinearIndicatorView(context, provideIndicatorParameter().build());
    }

    protected CharSequence provideIndicatorTitle(int index){
        return "";
    }

    protected IndicatorParameter.Builder provideIndicatorParameter(){
        return new IndicatorParameter.Builder()
                .withIndicatorHeight(IndicatorHelper.dip2px(2.5))
                .withUseHighColor(true)
                .withLRPadding(IndicatorHelper.dip2px(6))
                .withVerticalSpace(IndicatorHelper.dip2px(0))
                .withShowMode(IndicatorParameter.MODE_FIXED_TITLE)
                .withRadius(IndicatorHelper.dip2px(2))
                .withStartInterpolator(new AccelerateDecelerateInterpolator())
                .withEndInterpolator(new DecelerateInterpolator());
    }
}
