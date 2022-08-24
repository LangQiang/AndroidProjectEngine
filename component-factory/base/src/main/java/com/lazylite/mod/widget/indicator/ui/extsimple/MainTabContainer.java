package com.lazylite.mod.widget.indicator.ui.extsimple;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.ui.home.HomeLinearIndicatorView;
import com.lazylite.mod.widget.indicator.ui.simple.SimpleContainer;
import com.lazylite.mod.widget.indicator.ui.titles.MainTabTitleView;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

import java.util.ArrayList;
import java.util.List;


public class MainTabContainer extends SimpleContainer {
    private List<IPagerTitle> textList = new ArrayList<>();
    public MainTabContainer(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setTextSize(float textSize){
        mTextSize = textSize;
    }


    @Override
    public IPagerTitle getTitleView(Context context, int index) {
        String titleName = provideIndicatorTitle(index).toString();
        IPagerTitle simplePagerTitleView;
        if(titleName.startsWith("http")){
             simplePagerTitleView = new MainTabImageTitleView(context);
            ((MainTabImageTitleView)simplePagerTitleView).setPadding(IndicatorHelper.dip2px(10), 0, IndicatorHelper.dip2px(10), 0);
            ((MainTabImageTitleView) simplePagerTitleView).loadImage(titleName);
        }else{
            simplePagerTitleView = new MainTabTitleView(context);
            simplePagerTitleView.setNormalColorRid(R.color.black60);
            simplePagerTitleView.setSelectedColorRid(R.color.black80);

            ((MainTabTitleView) simplePagerTitleView).setMaxScale(1.15f);
            ((MainTabTitleView) simplePagerTitleView).setTextSize(mTextSize);
            ((MainTabTitleView) simplePagerTitleView).setText(titleName);
            ((MainTabTitleView)simplePagerTitleView).setPadding(IndicatorHelper.dip2px(10), 0, IndicatorHelper.dip2px(10), IndicatorHelper.dip2px(3));
        }
        textList.add(simplePagerTitleView);
        return simplePagerTitleView;
    }

    @Override
    protected void beforeInitTitleView() {
        textList.clear();
    }

    @Override
    protected IndicatorParameter.Builder provideIndicatorParameter() {
        return new IndicatorParameter.Builder()
                .withUseHighColor(true)
                .withIndicatorHeight(IndicatorHelper.dip2px(3.5))
                .withTBPadding(IndicatorHelper.dip2px(4))
                .withGravity(Gravity.CENTER)
                .withShowMode(IndicatorParameter.MODE_FIXED_TITLE)
                .withStartInterpolator(new AccelerateDecelerateInterpolator())
                .withIndicatorColorRid(R.color.ts_now_play_common_theme_color)
                .withRadius(IndicatorHelper.dip2px(2))
                .withEndInterpolator(new DecelerateInterpolator());
    }


    @Override
    public IPagerIndicator getIndicator(Context context) {
        HomeLinearIndicatorView homeLinearIndicatorView = new HomeLinearIndicatorView(context, provideIndicatorParameter().build());
        return homeLinearIndicatorView;
    }
}
