package com.lazylite.mod.widget;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DongJr
 * @date 2020/2/25
 */
public class DetailPagerAdapter extends FragmentPagerAdapter{

    private List<CharSequence> mTabNames = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    public DetailPagerAdapter(FragmentManager fm, LinkedHashMap<CharSequence, Fragment> datas) {
        super(fm);
        for (Map.Entry<CharSequence, Fragment> entry : datas.entrySet()) {
            mTabNames.add(entry.getKey());
            mFragments.add(entry.getValue());
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabNames.get(position);
    }

    public void setTabName(int position, CharSequence name){
        if (position >= 0 &&position < getCount()){
            mTabNames.set(position, name);
        }
    }

}
