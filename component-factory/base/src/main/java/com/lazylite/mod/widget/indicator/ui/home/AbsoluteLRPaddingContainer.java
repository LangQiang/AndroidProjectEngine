package com.lazylite.mod.widget.indicator.ui.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.lazylite.mod.widget.indicator.base.IPagerTitle;

import java.util.List;


/**
 * tab数 > 2：居中对齐，最左边tab的paddingLeft == mLRPadding ；最右边tab的paddingRight == mLRPadding。
 * tab数 <= 2：居中对齐，两个tab平分总宽度。
 * <p>
 * Created by lzf on 2020/3/2 3:11 PM
 */
public class AbsoluteLRPaddingContainer extends HomeContainer {
    public AbsoluteLRPaddingContainer(@NonNull Context context) {
        super(context);
    }

    /**
     * 如果MODE_SCROLLABLE模式没有充满屏幕，则让其充满屏幕
     */
    protected void calculateScrollableMode(List<IPagerTitle> titleView) {
        if (mMode == MODE_SCROLLABLE && !titleView.isEmpty()) {
            //第一个和最后一个的padding值不能变，宽度也不能变
            int spaceViewSize = titleView.size();
            if (spaceViewSize > 2) {//第一个和最后一个精确 自己的padding
                //tab的总宽度
                int totalTabWidth = 0;
                //text的总宽度
                int totalTextWidth = 0;
                //第一个View的宽度
                int firstTabWidth = 0;
                //最后一个View的宽度
                int lastTabWidth = 0;
                final int size = titleView.size();
                for (int i = 0; i < size; ++i) {
                    IPagerTitle pagerTitle = titleView.get(i);
                    if (pagerTitle instanceof View) {
                        View view = (View) pagerTitle;
                        int tabWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft() + view.getPaddingLeft() + view.getPaddingRight();
                        totalTabWidth = totalTabWidth + tabWidth;
                        if (0 == i) {
                            firstTabWidth = tabWidth;
                        }
                        if (i == size - 1) {
                            lastTabWidth = tabWidth;
                        }
                        if (0 != i && size - 1 != i) {
                            int textWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft();
                            totalTextWidth = totalTextWidth + textWidth;
                        }
                    }
                }
                if (totalTabWidth < getIndicatorWidth()) {
                    int spacePadding = (getIndicatorWidth() - firstTabWidth - lastTabWidth - totalTextWidth) / (spaceViewSize - 2);
                    for (int i = 0; i < size; ++i) {
                        if (i == 0 || i == size - 1) {
                            continue;
                        }
                        IPagerTitle pagerTitle = titleView.get(i);
                        if (pagerTitle instanceof View) {
                            View view = (View) pagerTitle;
                            view.setPadding(0, view.getPaddingTop(), 0, getPaddingBottom());
                            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                            if (layoutParams != null) {
                                int textWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft();
                                layoutParams.width = textWidth + spacePadding;
                            }
                        }
                    }
                    if (mTitleContainer != null) {
                        mTitleContainer.requestLayout();
                    }
                }
            } else {//计算出剩余间距，平均分配到每个tab上
                super.calculateScrollableMode(titleView);
            }
        }
    }
}
