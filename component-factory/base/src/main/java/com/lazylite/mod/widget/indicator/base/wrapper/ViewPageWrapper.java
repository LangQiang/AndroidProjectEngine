package com.lazylite.mod.widget.indicator.base.wrapper;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lazylite.mod.widget.indicator.base.PageChangeDelegate;

public class ViewPageWrapper implements IViewPagerWrapper {

    public final ViewPager viewPager;

    public ViewPageWrapper(@NonNull ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public IPagerAdapterWrapper getAdapter() {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            return new PagerAdapterWrapper(adapter);
        }
        return null;
    }

    @Override
    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void addOnPageChangeListener(PageChangeDelegate pageChangeDelegate) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageScrolled(i, v, i1);
                }
            }

            @Override
            public void onPageSelected(int i) {
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageSelected(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageScrollStateChanged(i);
                }
            }
        });
    }

    @Override
    public void setCurrentItem(int i, boolean withPageChangeAnim) {
        viewPager.setCurrentItem(i, withPageChangeAnim);
    }

    public static class PagerAdapterWrapper implements IPagerAdapterWrapper {

        private final PagerAdapter pagerAdapter;

        public PagerAdapterWrapper(@NonNull PagerAdapter pagerAdapter) {
            this.pagerAdapter = pagerAdapter;
        }

        public int getCount() {
            return pagerAdapter.getCount();
        }
    }
}
