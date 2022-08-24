package com.lazylite.mod.widget.indicator.ui.extsimple;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;

import com.lazylite.mod.widget.indicator.ui.simple.SimplePagerTitleView;


/**
 * 非对称左右padding也能对齐。
 *
 * Created by lzf on 2019/4/4 2:57 PM
 */
public class FreePaddingSimpleTitleView extends SimplePagerTitleView {
    private float mNormalSize;
    private float mSelectedSize;

    public FreePaddingSimpleTitleView(Context context) {
        super(context);
    }

    @Override
    public int getContentLeft() {
        Rect rect = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), rect);
        return getLeft() + getPaddingLeft();
    }

    @Override
    public int getContentRight() {
        Rect rect = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), rect);
        return getRight() - getPaddingRight();
    }

    @Override
    public void onSelected(int index, int totalCount) {
        getPaint().setFakeBoldText(true);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, mSelectedSize);
        setTextColor(mSColor);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        getPaint().setFakeBoldText(false);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, mNormalSize);
        setTextColor(mNColor);
    }

    /**
     * @param textSize 单位sp
     * */
    public void setSelectedTextSize(float textSize){
        mSelectedSize = textSize;
    }

    /**
     * @param textSize 单位sp
     * */
    public void setNormalTextSize(float textSize){
        mNormalSize = textSize;
    }
}
