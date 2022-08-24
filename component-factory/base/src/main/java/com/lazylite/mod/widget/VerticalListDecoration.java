package com.lazylite.mod.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lazylite.mod.utils.ScreenUtility;

/**
 * Created by lzf on 2022/1/4 6:06 下午
 */
public class VerticalListDecoration extends RecyclerView.ItemDecoration {
    private final int verPadding;
    private final int horPadding;
    private final boolean withTopAndBottom;

    public VerticalListDecoration(float verticalSpaceDp, float horizontalSpaceDp, boolean withTopAndBottom) {
        this.verPadding = ScreenUtility.dip2px(verticalSpaceDp) / 2;
        this.horPadding = ScreenUtility.dip2px(horizontalSpaceDp);
        this.withTopAndBottom = withTopAndBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        outRect.left = horPadding;
        outRect.right = horPadding;

        if (!withTopAndBottom) {


            int position = parent.getChildAdapterPosition(view);

            if (position == 0) {
                outRect.top = 0;
                outRect.bottom = verPadding;
                return;
            }

            if (parent.getAdapter() != null && position == parent.getAdapter().getItemCount() - 1) {
                outRect.top = verPadding;
                outRect.bottom = 0;
                return;
            }

        }

        outRect.top = verPadding;
        outRect.bottom = verPadding;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
