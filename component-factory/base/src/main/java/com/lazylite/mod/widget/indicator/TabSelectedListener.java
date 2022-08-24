package com.lazylite.mod.widget.indicator;

/**
 * @author DongJr
 *
 * @date 2018/5/28.
 */
public interface TabSelectedListener {

    void onTabSelected(int position);

    void onTabUnselected(int position);

    void onTabReselected(int position);

}
