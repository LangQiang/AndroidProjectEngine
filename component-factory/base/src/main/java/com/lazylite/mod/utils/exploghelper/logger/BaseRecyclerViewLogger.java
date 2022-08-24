package com.lazylite.mod.utils.exploghelper.logger;


import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazylite.mod.utils.psrc.PsrcInfo;

import java.util.List;


/**
 * 抽象出针对RecyclerView控件的滚动露出统计日志类
 */
public abstract class BaseRecyclerViewLogger<T> extends BaseShowLogger<T> {

    protected RecyclerView mListView;
    protected List<T> mData;
    protected RecyclerView.OnScrollListener mOnScrollListener;

    public BaseRecyclerViewLogger(RecyclerView listView, List data, PsrcInfo psrcInfo) {
        super(psrcInfo);
        mListView = listView;
        mData = data;
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                BaseRecyclerViewLogger.this.onScrollStateChanged(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (null == layoutManager) {
                    return;
                }
                if (layoutManager instanceof LinearLayoutManager) {
                    mFirstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    mLastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                    if (isFirst && mData != null && mData.size() > 0) {
                        addItem2List();
                        isFirst = false;
                    }
                }
            }

        });
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        mOnScrollListener = scrollListener;
    }

    public void setData(List<T> data) {
        mData = data;
    }

    public void addData(List<T> datas) {
        if (mData != null) {
            mData.addAll(datas);
        }
    }

    /**
     * 有些地方不会触发scrollListener
     */
    public void addCurScreen() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                final RecyclerView.LayoutManager layoutManager = mListView.getLayoutManager();
                if (null == layoutManager) {
                    return;
                }
                if (layoutManager instanceof LinearLayoutManager) {
                    mFirstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    mLastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                    addItem2List();
                    sendLog();
                }
            }
        });
    }

    @Override
    public void addItem2List() {
        try {
            if (mData == null) {
                return;
            }
            for (int i = mFirstVisibleItem; i <= mLastVisibleItem; i++) {
                if (i < mData.size()) {
                    T item = mData.get(i);
                    addItem(item);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected View getView() {
        return mListView;
    }
}
