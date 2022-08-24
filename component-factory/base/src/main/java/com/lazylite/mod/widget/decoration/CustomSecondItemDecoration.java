package com.lazylite.mod.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.lazylite.mod.utils.ScreenUtility;

/**
 * 仅限用于HORIZONTAL两行Grid中，动态添加左右边距
 *
 * 0 2 4
 * 1 3 5
 */
public class CustomSecondItemDecoration extends RecyclerView.ItemDecoration {

    private int outSide;
    private int inSide;


    /**
     * @param outSide
     * @param inSide
     */
    public CustomSecondItemDecoration(int outSide, int inSide) {
        this.outSide = ScreenUtility.dip2px(outSide);
        this.inSide = ScreenUtility.dip2px(inSide);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int currentPosition = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();
        if (currentPosition == 0 || currentPosition == 1) {
            outRect.left = outSide;
            outRect.right = inSide;
        } else if (currentPosition == totalCount - 1 || currentPosition == totalCount - 2) {
            outRect.right = outSide;
            outRect.left = inSide;
        } else {
            outRect.left = inSide;
            outRect.right = inSide;
        }
    }
}
