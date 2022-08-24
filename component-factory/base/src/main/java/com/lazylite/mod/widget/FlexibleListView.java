package com.lazylite.mod.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;


/**
 * listview拉到最下面可以继续向上滑动，然后回弹
 * Created by wangna on 2016/11/1.
 */

public class FlexibleListView extends ListView {

    private static final int MAX_OVERSCROLL_Y = 80;
    private Context mContext;
    private int newMaxOverScrollY;

    public FlexibleListView(Context context ) {
        super(context);
        this.mContext = context;
        init();
    }

    public FlexibleListView(Context context, AttributeSet attrs ) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public FlexibleListView(Context context, AttributeSet attrs, int defStyleAttr ) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        if (mContext == null) {
            return;
        }
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float density = metrics.density;
        newMaxOverScrollY = (int) (density * MAX_OVERSCROLL_Y);
    }
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

}
