package com.lazylite.mod.widget.indicator.ui.anchor;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.utils.FontUtils;
import com.lazylite.mod.widget.indicator.base.CommonContainer;
import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.ui.extsimple.FixedWidthLinearIndicatorView;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

public abstract class AnchorCommonContainer extends CommonContainer {

    protected float mTextSize = 15f;

    public AnchorCommonContainer(@NonNull Context context) {
        super(context);
        withPageChangeAnim(false);
    }

    @Override
    protected IPagerTitle getTitleView(Context context, int index) {
        AnchorCommonPagerTitleView titleView = new AnchorCommonPagerTitleView(context);

        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);

        titleView.setText(provideIndicatorTitle(index));

        int leftPadding = IndicatorHelper.dip2px(10);
        int rightPadding = leftPadding;
        /*if (index == 0) {
            leftPadding = IndicatorHelper.dip2px(15);
        } else if (index == getSize() - 1) {
            rightPadding = IndicatorHelper.dip2px(15);
        }*/

        titleView.setPadding(leftPadding, 0, rightPadding, 0);
        titleView.setGravity(Gravity.CENTER);
        titleView.setSelectedColorRid(R.color.skin_high_blue_color);
        titleView.setNormalColorRid(R.color.LRLiteBase_cl_black_alpha_60);
        titleView.setTypeface(FontUtils.getInstance().getDinRegularType());
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return new FixedWidthLinearIndicatorView(context, provideIndicatorParameter().build());
    }

    protected IndicatorParameter.Builder provideIndicatorParameter() {

        IndicatorParameter.Builder builder = new IndicatorParameter.Builder()
                .withUseHighColor(true)
                .withRadius(IndicatorHelper.dip2px(2))
                .withStartInterpolator(new AccelerateDecelerateInterpolator())
                .withEndInterpolator(new DecelerateInterpolator());
        builder.withIndicatorColorRid(R.color.skin_high_blue_color);
        builder.withLRPadding(IndicatorHelper.dip2px(0));
        builder.withShowMode(IndicatorParameter.MODE_FIXED_TITLE);
        builder.withTBPadding(IndicatorHelper.dip2px(3));
        builder.withIndicatorHeight(IndicatorHelper.dip2px(3.5));
        builder.withVerticalSpace(IndicatorHelper.dip2px(8));
        builder.withGravity(Gravity.BOTTOM);
        return builder;
    }

    protected abstract CharSequence provideIndicatorTitle(int index);

    protected abstract int getSize();

}
