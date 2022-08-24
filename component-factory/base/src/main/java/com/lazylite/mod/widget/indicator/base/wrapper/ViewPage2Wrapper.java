package com.lazylite.mod.widget.indicator.base.wrapper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.lazylite.mod.widget.indicator.base.PageChangeDelegate;

public class ViewPage2Wrapper implements IViewPagerWrapper {

    public final ViewPager2 viewPager2;

    public ViewPage2Wrapper(@NonNull ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @Override
    public IPagerAdapterWrapper getAdapter() {
        RecyclerView.Adapter adapter = viewPager2.getAdapter();
        if (adapter != null) {
            return new Pager2AdapterWrapper(adapter);
        }
        return null;
    }

    @Override
    public int getCurrentItem() {
        return viewPager2.getCurrentItem();
    }

    @Override
    public void addOnPageChangeListener(PageChangeDelegate pageChangeDelegate) {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (pageChangeDelegate != null) {
                    pageChangeDelegate.onPageScrollStateChanged(state);
                }
            }
        });
    }

    @Override
    public void setCurrentItem(int i, boolean withPageChangeAnim) {
        viewPager2.setCurrentItem(i, withPageChangeAnim);
    }

    public static class Pager2AdapterWrapper implements IPagerAdapterWrapper {

        private final RecyclerView.Adapter pagerAdapter;

        public Pager2AdapterWrapper(RecyclerView.Adapter pagerAdapter) {
            this.pagerAdapter = pagerAdapter;
        }

        public int getCount() {
            return pagerAdapter.getItemCount();
        }
    }
}
