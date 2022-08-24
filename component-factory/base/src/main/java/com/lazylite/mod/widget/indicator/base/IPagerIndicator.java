package com.lazylite.mod.widget.indicator.base;

import com.lazylite.mod.widget.indicator.model.LocationModel;

import java.util.List;



/**
 * @author DongJr
 *
 * @date 2018/5/25.
 */
public interface IPagerIndicator {

    int getIndicatorColor();

    void onSkinChanged();

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onProvideLocation(List<LocationModel> locationModels);

}
