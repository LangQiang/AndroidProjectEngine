package com.lazylite.mod.widget.swipeback.app;


import com.lazylite.mod.widget.swipeback.SwipeBackLayout;

/**
 * @author Yrom
 */
public interface SwipeBackFragmentBase {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();

    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();
    
    public abstract void close();

}
