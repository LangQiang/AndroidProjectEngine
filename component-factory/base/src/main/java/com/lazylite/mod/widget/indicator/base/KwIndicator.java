package com.lazylite.mod.widget.indicator.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.lazylite.mod.widget.indicator.TabSelectedListener;
import com.lazylite.mod.widget.indicator.base.wrapper.IPagerAdapterWrapper;
import com.lazylite.mod.widget.indicator.base.wrapper.IViewPagerWrapper;
import com.lazylite.mod.widget.indicator.base.wrapper.ViewPageWrapper;


/**
 * @author DongJr
 *
 * @date 2018/5/25.
 *
 * 样式和滑动逻辑完全解耦，并且标题和指示器样式可以随意组合随意定制
 * 再不怕换ui了
 */
public class KwIndicator extends FrameLayout implements PageChangeDelegate{

    private IPagerContainer mPagerContainer;
    private TabSelectedListener mTabSelectedListener;
    private IViewPagerWrapper mViewPager;
    private IPagerAdapterWrapper mPagerAdapter;
    private int mLastPosition = -1;
    private int mCurrentPosition = -1;

    public KwIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KwIndicator(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(IViewPagerWrapper viewPageWrapper) {
        if (mPagerContainer == null){
            throw new IllegalArgumentException("Indicator does not have a container set!");
        }
        IPagerAdapterWrapper adapter = viewPageWrapper.getAdapter();
        if (adapter == null){
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set!");
        }
        mViewPager = viewPageWrapper;
        mPagerAdapter = adapter;
        mViewPager.addOnPageChangeListener(this);
        mPagerContainer.setOnTabSelectedListener(mTabSelectedListener);
        mPagerContainer.setViewPager(mViewPager);
        mPagerContainer.onAttachToIndicator();
        if (adapter.getCount() > 0){
            int curItem = mViewPager.getCurrentItem();
            selectedTab(curItem);
        }
    }

    public void bind(@NonNull ViewPager viewPager){
        bind(new ViewPageWrapper(viewPager));
    }

    public void onDataChanged(){
        if (mViewPager != null && mPagerAdapter != null && mPagerAdapter.getCount() > 0){
            mPagerContainer.onAttachToIndicator();
            selectedTab(mViewPager.getCurrentItem(), false);
        }
    }

    public IPagerContainer getContainer() {
        return mPagerContainer;
    }

    public void setContainer(IPagerContainer container) {
        if (mPagerContainer == container) {
            return;
        }
        if (mPagerContainer != null) {
            mPagerContainer.onDetachFromIndicator();
        }
        mPagerContainer = container;
        removeAllViews();
        if (mPagerContainer instanceof View) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView((View) mPagerContainer, lp);
        }
    }

    public void setOnTabSelectedListener(TabSelectedListener listener){
        this.mTabSelectedListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPagerContainer != null){
            mPagerContainer.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mLastPosition = mCurrentPosition;
        selectedTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mPagerContainer != null){
            mPagerContainer.onPageScrollStateChanged(state);
        }
    }

    private void selectedTab(int position){
        selectedTab(position, true);
    }

    private void selectedTab(int position, boolean shouldCallback){
        if (mLastPosition == position){
            if (mTabSelectedListener != null && shouldCallback) {
                mTabSelectedListener.onTabReselected(position);
            }
        } else {
            mCurrentPosition = position;
            if (mPagerContainer != null){
                mPagerContainer.onPageSelected(position);
            }
            if (mTabSelectedListener != null && shouldCallback){
                mTabSelectedListener.onTabSelected(position);
            }
            if (mLastPosition >= 0 && mTabSelectedListener != null && shouldCallback){
                mTabSelectedListener.onTabUnselected(mLastPosition);
            }
        }
    }

}
