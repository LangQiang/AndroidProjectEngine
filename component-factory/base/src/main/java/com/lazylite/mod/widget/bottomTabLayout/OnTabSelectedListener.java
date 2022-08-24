package com.lazylite.mod.widget.bottomTabLayout;

import android.view.View;

/**
 * Created by tc :)
 */
public interface OnTabSelectedListener {

    void onTabSelected(int position, int prePosition);

    void onTabReselected(int position);

    void onCenterViewClicked(View centerView);

    void click(int index);
}