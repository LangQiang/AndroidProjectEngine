package com.lazylite.mod.widget.indicator.ui.home;

import android.content.Context;
import android.widget.TextView;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.ui.simple.SimplePagerTitleView;


/**
 * @author DongJr
 *
 * @date 2018/5/28.
 */
public class HomePagerTitleView extends SimplePagerTitleView {
    protected int mNormalColor;
    protected int mSelectedColor;

    public HomePagerTitleView(Context context) {
        super(context);
        initColor();
    }

    @Override
    public void onSkinChanged() {
        initColor();
    }

    @Override
    public void onSelected(int index, int totalCount) {
        getPaint().setFakeBoldText(true);
        setTextColor(mSelectedColor);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        getPaint().setFakeBoldText(false);
        setTextColor(mNormalColor);
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        super.setContentDescription(text);
    }

    private void initColor() {
        mNormalColor =  getContext().getResources().getColor(R.color.black80);
        mSelectedColor = getContext().getResources().getColor(R.color.rgbFFFF5400);
    }

}
