package com.lazylite.mod.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazylite.mod.utils.ScreenUtility;

/**
 *
 * 针对 GridLayoutManager(Context, int, RecyclerView.HORIZONTAL, boolean) 这样的，RecyclerView.VERTICAL类型的没实现。
 *
 * Created by lzf on 2021/10/13 2:15 下午
 */
public class GridStyleItemDecoration extends RecyclerView.ItemDecoration {
    private final int hOutSide;
    private final int hInSide;
    private final int vOutSide;
    private final int vInSide;

    public GridStyleItemDecoration(int hOutSide, int hInSide,int vOutSide, int vInSide) {
        this.hOutSide = ScreenUtility.dip2px(hOutSide);
        this.hInSide = ScreenUtility.dip2px(hInSide);
        this.vOutSide = ScreenUtility.dip2px(vOutSide);
        this.vInSide = ScreenUtility.dip2px(vInSide);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRow(int itemPosition, RecyclerView parent) {
        if (parent.getAdapter() == null) {
            return false;
        }
        int spanCount = getSpanCount(parent);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        //有多少列
        if (layoutManager instanceof GridLayoutManager) {
            boolean isVertical = ((GridLayoutManager)layoutManager).getOrientation() == RecyclerView.VERTICAL;
            if(isVertical){
                int childCount = parent.getAdapter().getItemCount();

                double count = Math.ceil((double) childCount / (double) spanCount);//总行数
                double currentCount = Math.ceil((double) (itemPosition + 1) / spanCount);//当前行数

                //最后当前数量小于总的
                if (currentCount < count) {
                    return false;
                }
            } else {
                return (itemPosition+1)%spanCount == 0;
            }
        }
        return true;
    }

    private boolean isFirstRow(int itemPosition, RecyclerView parent){
        if (parent.getAdapter() == null) {
            return true;
        }
        int spanCount = getSpanCount(parent);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            boolean isVertical = ((GridLayoutManager)layoutManager).getOrientation() == RecyclerView.VERTICAL;
            if(isVertical){
                double currentCount = Math.ceil((double) (itemPosition + 1) / spanCount);//当前行数
                return currentCount == 1;
            }else {
                return (itemPosition+1)%spanCount == 1;
            }
        }
        return true;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        getHorItemOffsets(outRect,view,parent);
        getVerItemOffsets(outRect, view, parent);
    }


    private void getHorItemOffsets(Rect outRect, View view, RecyclerView parent) {
        int currentPosition = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();
        if (currentPosition == 0 || currentPosition == 1) {
            outRect.left = hOutSide;
            outRect.right = hInSide;
        } else if (currentPosition == totalCount - 1 || currentPosition == totalCount - 2) {
            outRect.right = hOutSide;
            outRect.left = hInSide;
        } else {
            outRect.left = hInSide;
            outRect.right = hInSide;
        }
    }

    private void getVerItemOffsets(Rect outRect, View view, RecyclerView parent){
        int currentPosition = parent.getChildAdapterPosition(view);
        if(isFirstRow(currentPosition,parent)){
            outRect.top = vOutSide;
            outRect.bottom = vInSide;
        }else if(isLastRow(currentPosition, parent)){
            outRect.top = vInSide;
            outRect.bottom = vOutSide;
        }else {
            outRect.top = vInSide;
            outRect.bottom = vInSide;
        }
    }
}
