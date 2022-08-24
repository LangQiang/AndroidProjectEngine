package com.lazylite.mod.widget.indicator.ui.extsimple;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.base.CommonContainer;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.ui.simple.SimpleContainer;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

import java.util.List;


/**
 * 非对称左右padding也能对齐。
 *
 * Created by lzf on 2019/4/4 2:56 PM
 */
public class FreePaddingSimpleContainer extends SimpleContainer {
    float mSelectedTextSize = 0f;

    public FreePaddingSimpleContainer(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setTextSize(float textSize){
        mTextSize = textSize;
        if (0f == mSelectedTextSize) {
            mSelectedTextSize = textSize;
        }
    }

    public void setSelectedTextSize(float selectedTextSize){
        mSelectedTextSize = selectedTextSize;
    }

    @Override
    protected void calculateScrollableMode(List<IPagerTitle> titleView) {
        //不要让其强制横向充满屏幕
    }

    @Override
    public IPagerTitle getTitleView(Context context, int index) {
        FreePaddingSimpleTitleView simplePagerTitleView = new FreePaddingSimpleTitleView(context);
        simplePagerTitleView.setNormalColorRid(R.color.black80);
        simplePagerTitleView.setSelectedColorRid(R.color.rgbFFFF5400);
        if(mTextSize == 0){
            setTextSize(16f);
        }
        simplePagerTitleView.setNormalTextSize(mTextSize);
        simplePagerTitleView.setSelectedTextSize(mSelectedTextSize);

        //这里支持设置title集合，也支持单个设置
        if (mTitles != null && mTitles.size() > index){
            simplePagerTitleView.setText(mTitles.get(index));
        } else {
            simplePagerTitleView.setText(provideIndicatorTitle(index));
        }
        if (getTabMode() == CommonContainer.MODE_SCROLLABLE){
            int titleSize = null == mTitles ? -1 : mTitles.size();
            if(index == titleSize -1){
                simplePagerTitleView.setPadding(IndicatorHelper.dip2px(15), 0, IndicatorHelper.dip2px(15), 0);
            } else {
                simplePagerTitleView.setPadding(IndicatorHelper.dip2px(15), 0, IndicatorHelper.dip2px(10), 0);
            }
        }
        return simplePagerTitleView;
    }
}
