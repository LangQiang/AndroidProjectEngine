package com.lazylite.mod.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.lazylite.mod.utils.ScreenUtility;

public class SingleVerItemDecoration extends RecyclerView.ItemDecoration {

    private int outSide;
    private int inSide;


    /**
     *
     * @param outSide
     * @param inSide
     */
    public SingleVerItemDecoration(int outSide, int inSide) {
        this.outSide = ScreenUtility.dip2px(outSide);
        this.inSide = ScreenUtility.dip2px(inSide);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        try {
            int currentPosition = parent.getChildAdapterPosition(view);
            int totalCount = parent.getAdapter().getItemCount();
            if (currentPosition == 0) {
                outRect.top = outSide;
                outRect.bottom = inSide;
            } else if (currentPosition == totalCount -1) {
                outRect.bottom = outSide;
                outRect.top = inSide;
            } else {
                outRect.top = inSide;
                outRect.bottom = inSide;
            }
        } catch (Exception e) {

        }

    }
}
