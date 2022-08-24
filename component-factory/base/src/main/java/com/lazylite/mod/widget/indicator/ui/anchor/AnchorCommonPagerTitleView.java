package com.lazylite.mod.widget.indicator.ui.anchor;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;

import com.example.basemodule.R;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;


/**
 * @author DongJr
 *
 * @date 2018/5/28.
 */
public class AnchorCommonPagerTitleView extends androidx.appcompat.widget.AppCompatTextView implements IPagerTitle {

    private Rect tempRect = new Rect();

    protected int mNormalColorRid = R.color.black80;
    protected int mSelectedColorRid = R.color.rgbFFFF5400;
    protected int mNColor;
    protected int mSColor;

    public AnchorCommonPagerTitleView(Context context) {
        super(context);
        mNColor = context.getResources().getColor(mNormalColorRid);
        mSColor = context.getResources().getColor(mSelectedColorRid);
        setGravity(Gravity.CENTER);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void onSkinChanged() {

    }

    @Override
    public void onSelected(int index, int totalCount) {
        getPaint().setFakeBoldText(true);
        setTextColor(mSColor);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        getPaint().setFakeBoldText(false);
        setTextColor(mNColor);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }


    @Override
    public int getContentLeft() {
        tempRect.setEmpty();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), tempRect);
        return getLeft() + getWidth() / 2 - tempRect.width() / 2;
    }

    @Override
    public int getContentRight() {
        tempRect.setEmpty();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), tempRect);
        return getRight() - getWidth() / 2 + tempRect.width() / 2;
    }

    @Override
    public int getContentTop() {
        tempRect.setEmpty();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), tempRect);
        return tempRect.top;
    }

    @Override
    public int getContentBottom() {
        tempRect.setEmpty();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), tempRect);
        return tempRect.bottom;
    }

    @Override
    public void setNormalColorRid(int colorRid) {
        mNormalColorRid = colorRid;
        mNColor = getContext().getResources().getColor(mNormalColorRid);
    }

    @Override
    public void setSelectedColorRid(int colorRid) {
        mSelectedColorRid = colorRid;
        mSColor = getContext().getResources().getColor(mSelectedColorRid);
    }


    public int getNormalColor() {
        return mNColor;
    }

    public int getSelectedColor() {
        return mSColor;
    }
}
