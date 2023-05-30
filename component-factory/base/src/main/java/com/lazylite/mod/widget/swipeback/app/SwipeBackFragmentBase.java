package com.lazylite.mod.widget.swipeback.app;


import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.lazylite.mod.widget.swipeback.SwipeBackLayout;

/**
 * @author Yrom
 */
public interface SwipeBackFragmentBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();

    View getFragmentView();

    Context getContext();

    Activity getHostActivity();

    boolean isResumed();

    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();
    
    void close();

}
