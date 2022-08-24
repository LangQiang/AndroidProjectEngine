package com.lazylite.mod.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.widget.pulltorefresh.internal.LoadingLayout;
import com.lazylite.mod.widget.pulltorefresh.internal.MainHomeLoadingLayout;

public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    static final String TAG = "PullToRefreshRecyclerView";
    public PullToRefreshRecyclerView(Context context) {
        super(context);
        setDragLonely(true);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDragLonely(true);
    }

    public PullToRefreshRecyclerView(Context context, int mode) {
        super(context, mode);
        setDragLonely(true);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(View.generateViewId());
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible() && (allowRefreshCallback == null || allowRefreshCallback.isAllow());
    }

    @Override
    protected boolean isReadyForPullUp() {
        return false;
    }

    @Override
    protected LoadingLayout initHeaderLayout() {
        return new MainHomeLoadingLayout(getContext(), isWhite);
    }

    public boolean isFirstItemVisible() {
        final Adapter<?> adapter = getRefreshableView().getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            LogMgr.d(TAG, "isFirstItemVisible. Empty View.");
            return true;

        } else {
             /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (getFirstVisiblePosition() == 0) {
                final View firstVisibleChild = refreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= refreshableView.getTop();
                }
            }
        }

        return false;
    }

    public boolean isLastItemVisible() {
        final Adapter<?> adapter = getRefreshableView().getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            LogMgr.d(TAG, "isLastItemVisible. Empty View.");
            return true;
        } else {
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition >= refreshableView.getAdapter().getItemCount() - 1) {
                return refreshableView.getChildAt(
                        refreshableView.getChildCount() - 1).getBottom() <= refreshableView
                        .getBottom();
            }
        }
        return false;
    }

    /**
     * @Description: 获取第一个可见子View的位置下标
     *
     */
    private int getFirstVisiblePosition() {
        View firstVisibleChild = refreshableView.getChildAt(0);
        return firstVisibleChild != null ? refreshableView
                .getChildAdapterPosition(firstVisibleChild) : -1;
    }

    /**
     * @Description: 获取最后一个可见子View的位置下标
     *
     */
    private int getLastVisiblePosition() {
        View lastVisibleChild = refreshableView.getChildAt(refreshableView
                .getChildCount() - 1);
        return lastVisibleChild != null ? refreshableView
                .getChildAdapterPosition(lastVisibleChild) : -1;
    }

    private AllowRefreshCallback allowRefreshCallback;

    public void setAllowRefreshCallback(AllowRefreshCallback allowRefreshCallback) {
        this.allowRefreshCallback = allowRefreshCallback;
    }

    public interface AllowRefreshCallback {
        boolean isAllow();
    }

}