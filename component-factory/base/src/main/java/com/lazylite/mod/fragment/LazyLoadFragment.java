package com.lazylite.mod.fragment;

import android.os.Bundle;

import com.lazylite.mod.widget.BaseFragment;

/**
 * Created by tiancheng:)
 */
public abstract class LazyLoadFragment extends BaseFragment {

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareLoadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareLoadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.isVisibleToUser = !hidden;
        prepareLoadData();
    }

    public boolean prepareLoadData() {
        return prepareLoadData(false);
    }

    public boolean prepareLoadData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (forceUpdate || !isDataInitiated)) {
            lazyLoadData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    public abstract void lazyLoadData();
}