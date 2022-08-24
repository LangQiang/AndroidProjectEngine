package com.lazylite.mod.widget.indicator.ui.home;

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
public class HomeContainer extends CommonContainer {

    protected List<String> mTitles;
    protected float mTextSize;
    private float mLRPadding;//和KwIndicator无关变量，本类自己用来设置titleView的padding值的

    public HomeContainer(@NonNull Context context) {
        super(context);
    }

    public void setTitles(List<String> titles){
        mTitles = titles;
    }

    public void setTextSize(float textSize){
        mTextSize = textSize;
    }

    public void setLRPadding(float padding) {
        this.mLRPadding = padding;
    }

    @Override
    public IPagerTitle getTitleView(Context context, int index) {
        HomePagerTitleView simplePagerTitleView = new HomePagerTitleView(context);
        if (mTextSize > 0){
            simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        } else {
            simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
        }
        //这里支持设置title集合，也支持单个设置
        if (mTitles != null && mTitles.size() > index) {
            simplePagerTitleView.setText(mTitles.get(index));
        } else {
            simplePagerTitleView.setText(provideIndicatorTitle(index));
        }
        if (getTabMode() == CommonContainer.MODE_SCROLLABLE) {
            if (mLRPadding > 0) {
                simplePagerTitleView.setPadding(IndicatorHelper.dip2px(mLRPadding), 0, IndicatorHelper.dip2px(mLRPadding), 0);
            } else {
                simplePagerTitleView.setPadding(IndicatorHelper.dip2px(15), 0, IndicatorHelper.dip2px(15), 0);
            }
        }
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return new HomeLinearIndicatorView(context, provideIndicatorParameter());
    }

    protected CharSequence provideIndicatorTitle(int index){
        return "";
    }

    protected IndicatorParameter provideIndicatorParameter(){
        // 明星主题高亮色，官方黄===》黑指示器，官方白===》高亮色，其他===》白指示器
        return new IndicatorParameter.Builder()
                .withIndicatorHeight(IndicatorHelper.dip2px(2.5))
//                .withLRPadding(IndicatorHelper.dip2px(8))//这个是设置指示条的左右padding，不要和本类中的 mLRPadding 混淆了
                .withUseHighColor(false)
                .withIndicatorColorRid(R.color.rgbFFFF5400)
                .withVerticalSpace(IndicatorHelper.dip2px(0))
                .withShowMode(IndicatorParameter.MODE_FIXED_TITLE)
                .withRadius(IndicatorHelper.dip2px(2))
                .withStartInterpolator(new AccelerateDecelerateInterpolator())
                .withEndInterpolator(new DecelerateInterpolator())
                .build();
    }
}
