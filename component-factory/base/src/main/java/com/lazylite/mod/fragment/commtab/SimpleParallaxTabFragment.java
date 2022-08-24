package com.lazylite.mod.fragment.commtab;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.basemodule.R;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.widget.indicator.base.IPagerContainer;
import com.lazylite.mod.widget.indicator.base.IPagerTitle;
import com.lazylite.mod.widget.indicator.base.IndicatorParameter;
import com.lazylite.mod.widget.indicator.ui.simple.SimpleContainer;
import com.lazylite.mod.widget.vp.ViewPagerCompat;

import com.lazylite.mod.widget.indicator.base.KwIndicator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Created by lzf on 2019-05-16 11:28
 */
public abstract class SimpleParallaxTabFragment extends SimpleScrollWithHeaderFragment {
    protected KwIndicator mIndicator;
    protected BaseTabAdapter mTabAdapter;
    protected ViewPagerCompat mViewPager;
    @Nullable
    private IPagerContainer mPagerContainer;

    public abstract  LinkedHashMap<ViewPagerTabBean, Fragment> giveFragments();

    protected void onPageSelected(int pos) {

    }

    /**
     * 自定义Indicator
     */
    protected IPagerContainer getIndicatorContainer() {
        return new MyIndicatorSimpleContainer(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentV = super.onCreateView(inflater, container, savedInstanceState);//所有View已经创建完毕
        mTabAdapter = getPageAdapter();
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mPagerContainer = getIndicatorContainer();
        mIndicator.setContainer(mPagerContainer);
        mIndicator.bind(mViewPager);
        mViewPager.addOnPageChangeListener(new KwOnScrollListener(mViewPager, new PagerCallBack() {
            @Override
            public void pageScroll(int position, float offset, int positionOffsetPixels) {
                SimpleParallaxTabFragment.this.onPageScrolled(position, offset, positionOffsetPixels);
            }

            @Override
            public void pageScrollEnd() {
                SimpleParallaxTabFragment.this.onPageScrollEnd();

            }

            @Override
            public void pageScrollStart() {
                SimpleParallaxTabFragment.this.onPageScrollStart();
            }

            @Override
            public void onPageSelected(int position) {
                SimpleParallaxTabFragment.this.onPageSelected(position);
            }
        }));
        //ViewPage首次 onPageSelected() 不调用，蛋疼的android SDK
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                onPageSelected(0);
            }
        });
        return parentV;
    }

    protected void onPageScrollEnd() {

    }

    protected void onPageScrollStart() {

    }

    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    protected BaseTabAdapter<?> getPageAdapter() {
        LinkedHashMap<ViewPagerTabBean, Fragment> fragmentLinkedHashMap = giveFragments();
        return new BaseTabAdapter<>(getChildFragmentManager(), fragmentLinkedHashMap);
    }

    @Override
    protected View onCreateStickyView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, boolean attachToRoot) {
        View view = inflater.inflate(R.layout.layout_simple_parallax_tab, parent, attachToRoot);
        mIndicator = view.findViewById(R.id.indicator);
        return view;
    }

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, boolean attachToRoot) {
        View view = inflater.inflate(R.layout.layout_simple_parallax_content, parent, attachToRoot);
        mViewPager = view.findViewById(R.id.pager);
        return view;
    }

    public void addMoreFragments(LinkedHashMap<ViewPagerTabBean, Fragment> moreData) {
        if (null == mTabAdapter) {
            return;
        }
        mPagerContainer = getIndicatorContainer();
        mTabAdapter.addMore(moreData);
        mIndicator.setContainer(mPagerContainer);
        mIndicator.bind(mViewPager);
    }

    protected static class BaseTabAdapter<T extends ViewPagerTabBean> extends FragmentPagerAdapter {
        public List<T> pageInfo;
        public List<Fragment> fragments;
        private FragmentManager mFmManager;
        private int mChildCount = 0;

        public BaseTabAdapter(FragmentManager fm, LinkedHashMap<T, Fragment> datas) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            pageInfo = new ArrayList<>(datas.size());
            fragments = new ArrayList<>(datas.size());
            mFmManager = fm;
            for (Map.Entry<T, Fragment> entry : datas.entrySet()) {
                pageInfo.add((entry.getKey()));
                fragments.add(entry.getValue());
            }
        }

        public void addMore(LinkedHashMap<T, Fragment> datas) {
            for (Map.Entry<T, Fragment> entry : datas.entrySet()) {
                pageInfo.add(entry.getKey());
                fragments.add(entry.getValue());
            }
            notifyDataSetChanged();
        }

        private void removeFragmentInternal(Fragment fragment) {
            FragmentTransaction transaction = mFmManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commitNow();
        }

        public void rebuildData(LinkedHashMap<T, Fragment> datas) {
            for (int i = 0; i < fragments.size(); i++) {
                removeFragmentInternal(fragments.get(i));
            }
            pageInfo.clear();
            fragments.clear();
            for (Map.Entry<T, Fragment> entry : datas.entrySet()) {
                pageInfo.add(entry.getKey());
                fragments.add(entry.getValue());
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageInfo.get(position).charSequence;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public List<String> getTitles(){
            int size = null == pageInfo ? 0 : pageInfo.size();
            List<String> titles = new ArrayList<>(size);
            if(0 == size){
                return titles;
            }
            for (ViewPagerTabBean viewPagerTabBean : pageInfo) {
                titles.add(viewPagerTabBean.charSequence.toString());
            }
            return titles;
        }

        public Fragment getCurrentFragment(int position) {
            if (fragments != null && position < fragments.size()) {
                return fragments.get(position);
            } else {
                return null;
            }
        }

        public void setTabName(int position, CharSequence name) {
            if (pageInfo != null && position < pageInfo.size() && pageInfo.get(position) != null) {
                pageInfo.get(position).charSequence = name;
            }
        }

        @Override public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

    }//BaseTabAdapter end

    protected static class KwOnScrollListener implements ViewPager.OnPageChangeListener {
        private float lastPositionOffset = 99f;
        private float offsetXAccuracy = 0.05f;
        private int selectedPosition;
        private PagerCallBack mCallBack;

        public KwOnScrollListener(ViewPager pager,PagerCallBack callBack) {
            this.selectedPosition = pager.getCurrentItem();
            this.mCallBack = callBack;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (lastPositionOffset == 99f) {
                lastPositionOffset = positionOffset;
                return;
            }

            boolean report;
            if (position > selectedPosition) {
                report = false;
            } else if (position < selectedPosition - 1) {
                report = false;
            } else {
                report = true;
            }

            float offset = positionOffset - lastPositionOffset;

            if (Math.abs(offset) >= offsetXAccuracy && report) {
                if (position == selectedPosition) {
                    //slide to left. positionOffset[0 -> 1]
                    mCallBack.pageScroll(selectedPosition, -positionOffset, positionOffsetPixels);
                } else if (position < selectedPosition) {
                    //slide to right. positionOffset[1 -> 0]
                    mCallBack.pageScroll(selectedPosition, 1f - positionOffset, positionOffsetPixels);
                }
                lastPositionOffset = positionOffset;
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            mCallBack.onPageSelected(position);

        }


        @Override
        public void onPageScrollStateChanged(int state) {

            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    lastPositionOffset = 99f;
                    mCallBack.pageScrollEnd();
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    mCallBack.pageScrollStart();
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    break;

            }
        }
    }

    public class MyIndicatorSimpleContainer extends SimpleContainer {

        public MyIndicatorSimpleContainer(@NonNull Context context) {
            super(context);
            mTextSize = 14;
        }

        @Override
        public IPagerTitle getTitleView(Context context, int index) {
            IPagerTitle titleView = super.getTitleView(context, index);
            titleView.setNormalColorRid(R.color.black60);
            titleView.setSelectedColorRid(R.color.black);
            return titleView;
        }

        //就是为了将此父类方法的protected放到成public
        @Override
        public CharSequence provideIndicatorTitle(int index) {
            if (mTabAdapter != null && mTabAdapter.getCount() > 0) {
                return mTabAdapter.getPageTitle(index);
            }
            return super.provideIndicatorTitle(index);
        }

        @Override
        protected IndicatorParameter.Builder provideIndicatorParameter() {
            IndicatorParameter.Builder builder = super.provideIndicatorParameter();
            builder.withIndicatorColorRid(R.color.skin_high_blue_color);
            return builder;
        }
    }//MyIndicatorSimpleContainer end

}