package com.lazylite.mod.widget.indicator.base.wrapper;

import com.lazylite.mod.widget.indicator.base.PageChangeDelegate;

public interface IViewPagerWrapper {

    IPagerAdapterWrapper getAdapter();

    int getCurrentItem();

    void addOnPageChangeListener(PageChangeDelegate pageChangeDelegate);

    void setCurrentItem(int i, boolean withPageChangeAnim);
}
