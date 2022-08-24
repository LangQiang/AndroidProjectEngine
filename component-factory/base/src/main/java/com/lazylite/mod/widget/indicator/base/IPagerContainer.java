package com.lazylite.mod.widget.indicator.base;

import com.lazylite.mod.widget.indicator.TabSelectedListener;
import com.lazylite.mod.widget.indicator.base.wrapper.IViewPagerWrapper;


/**
 * @author DongJr
 *
 * @date 2018/5/25.
 */
public interface IPagerContainer {

    void setOnTabSelectedListener(TabSelectedListener listener);

    void setViewPager(IViewPagerWrapper viewPager);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onDetachFromIndicator();

    void onAttachToIndicator();

    void scrollToPosition(int position);
}
