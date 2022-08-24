package com.lazylite.mod.fragment.commtab;

public interface PagerCallBack {
    void pageScroll(int position, float offset, int positionOffsetPixels);

    void pageScrollEnd();

    void pageScrollStart();

    void onPageSelected(int position);
}
