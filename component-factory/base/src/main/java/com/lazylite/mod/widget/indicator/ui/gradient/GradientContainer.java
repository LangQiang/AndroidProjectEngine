package com.lazylite.mod.widget.indicator.ui.gradient;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.basemodule.R;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.indicator.base.CommonContainer;
import com.lazylite.mod.widget.indicator.base.IPagerIndicator;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.ui.simple.SimplePagerTitleView;
import com.lazylite.mod.widget.indicator.utils.IndicatorHelper;

public abstract class GradientContainer extends CommonContainer {

    public GradientContainer(@NonNull Context context) {
        super(context);
    }

    @Override
    protected IPagerTitle getTitleView(Context context, int index) {
        SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
        simplePagerTitleView.setNormalColorRid(R.color.black40);
        simplePagerTitleView.setSelectedColorRid(R.color.black);
        simplePagerTitleView.setTextSize(16);
        simplePagerTitleView.setPadding(ScreenUtility.dip2px(12), 0, ScreenUtility.dip2px(12), 0);
        simplePagerTitleView.setText(getTextStr(index));
        return simplePagerTitleView;
    }

    public abstract String getTextStr(int position);

    @Override
    protected IPagerIndicator getIndicator(Context context) {
        return new GradientIndicatorView(context, provideIndicatorParameter().build());
    }

    private IndicatorParameter.Builder provideIndicatorParameter() {
        IndicatorParameter.Builder builder = new IndicatorParameter.Builder();
        builder.withIndicatorHeight(IndicatorHelper.dip2px(3.5));
        builder.withRadius(0);
        builder.withGravity(Gravity.BOTTOM);
        builder.withStartInterpolator(new AccelerateDecelerateInterpolator());
        builder.withEndInterpolator(new DecelerateInterpolator());
        builder.withFixedWidth(ScreenUtility.dip2px(36f));
        builder.withShader(new LinearGradient(0f, 0f, ScreenUtility.dip2px(36f),0f,
                        new int[]{0xff4CFDBF, 0xff2FCAFF, 0xffD702FF}, null,
                        Shader.TileMode.MIRROR)
        );
        return builder;
    }
}
