package com.lazylite.mod.widget.indicator.base;

public interface PageChangeDelegate {

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

}
