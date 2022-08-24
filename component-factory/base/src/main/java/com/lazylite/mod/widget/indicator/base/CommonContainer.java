package com.lazylite.mod.widget.indicator.base;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.basemodule.R;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.widget.HorizontalScrollViewCompat;
import com.lazylite.mod.widget.indicator.TabSelectedListener;
import com.lazylite.mod.widget.indicator.base.wrapper.IPagerAdapterWrapper;
import com.lazylite.mod.widget.indicator.base.wrapper.IViewPagerWrapper;
import com.lazylite.mod.widget.indicator.model.LocationModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * @author DongJr
 *
 * @date 2018/5/25.
 */
public abstract class CommonContainer extends FrameLayout implements IPagerContainer {

    /**
     * 滑动模式，当tab不满一屏时会充满屏幕，超过一屏时可滚动
     */
    public static final int MODE_SCROLLABLE = 0;
    /**
     * 平分宽度模式，不可滑动
     */
    public static final int MODE_FIXED = 1;
    /**
     * 固定间距(padding)模式，属于滑动模式，不满一屏时不会充满屏幕
     */
    public static final int MODE_FIXED_SPACE = 2;
    protected int mMode = MODE_FIXED;
    protected int mSelectedIndex;
    private int mLastIndex;
    private int mScrollState;
    private float mLastPositionOffset;
    private float mScrollRate = 0.5f;

    private float mRightFade;

    private float mLeftFade;

    private boolean withPageChangeAnim = true;
    /**
     * 渐变标题需要强制更新
     */
    private boolean mIsTransform;

    private List<LocationModel> mLocationDatas = new ArrayList<>();
    private SparseArray<Float> mLeavedPercents = new SparseArray<Float>();
    private SparseBooleanArray mDeselectedItems = new SparseBooleanArray();

    private IPagerIndicator mPagerIndicator;
    private IPagerAdapterWrapper mAdapter;
    private IViewPagerWrapper mViewPager;
    protected LinearLayout mTitleContainer;
    private LinearLayout mIndicatorContainer;
    private HorizontalScrollViewCompat mScrollView;
    private TabSelectedListener mTabSelectedListener;

    public CommonContainer(@NonNull Context context) {
        super(context);
    }

    @IntDef(value = {MODE_SCROLLABLE, MODE_FIXED, MODE_FIXED_SPACE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {}

    public void setTabMode(@Mode int mode) {
        mMode = mode;
    }

    public void setRightFade(float rightFade) {
        mRightFade = rightFade;
    }

    public void setLeftFade(float leftFade) {
        mLeftFade = leftFade;
    }

    public int getTabMode(){
        return mMode;
    }

    public void withPageChangeAnim(boolean withPageChangeAnim) {
        this.withPageChangeAnim = withPageChangeAnim;
    }

    protected abstract IPagerTitle getTitleView(Context context, int index);

    protected abstract IPagerIndicator getIndicator(Context context);

    public void setTransform(boolean isTransform){
        mIsTransform = isTransform;
    }

    /**
     * MODE_FIXED模式可重写，默认为1
     */
    protected int getTitleWeight(Context context, int index){
        return 1;
    }

    /**
     * MODE_FIXED模式可重写，默认为0
     * getTitleWidth = WRAP_CONTENT & getTitleWeight = 0,
     * 可实现将titleView按照内容宽度放在屏幕上，不填充屏幕宽度的需求
     */
    protected int getTitleWidth(Context context, int index) {
        return 0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter != null && mTitleContainer != null){
            buildLocationModel();
            if (mPagerIndicator != null){
                mPagerIndicator.onProvideLocation(mLocationDatas);
            }
        }
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            onPageSelected(mSelectedIndex);
            onPageScrolled(mSelectedIndex, 0.0f, 0);
        }
    }

    @Override
    public void setOnTabSelectedListener(TabSelectedListener listener) {
        mTabSelectedListener = listener;
    }

    @Override
    public void onAttachToIndicator() {
        removeAllViews();
        View root;
        if (mMode == MODE_SCROLLABLE || mMode == MODE_FIXED_SPACE){
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout, this, true);
            mScrollView = root.findViewById(R.id.scroll_view);
            mScrollView.setLeftFading(mLeftFade);
            mScrollView.setRightFading(mRightFade);
        } else {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout_no_scroll, this, true);
        }
        mIndicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        mTitleContainer = (LinearLayout)root.findViewById(R.id.title_container);
        initIndicatorAndTitle();
    }

    @Override
    public void onDetachFromIndicator() {
        removeAllViews();
    }

    @Override
    public void setViewPager(IViewPagerWrapper viewPager) {
        mViewPager = viewPager;
        mAdapter = viewPager.getAdapter();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPagerIndicator != null){
            mPagerIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
        onScrollViewScrolled(position, positionOffset);
        onTitleViewScrolled(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        mLastIndex = position;
        mSelectedIndex = position;
        if (mPagerIndicator != null && !withPageChangeAnim){
            mPagerIndicator.onPageSelected(position);
        }
//        scrollToPosition(position);
        dispatchOnSelected(mSelectedIndex);
        for (int i = 0; i < mAdapter.getCount(); i++){
            if (i == mSelectedIndex) {
                continue;
            }
            boolean deselected = mDeselectedItems.get(i);
            if (!deselected) {
                dispatchOnDeselected(i);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
        if (mPagerIndicator != null){
            mPagerIndicator.onPageScrollStateChanged(state);
        }
    }

    protected void setTitleClickListener(View view, final int i) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == mSelectedIndex){
                    if (mTabSelectedListener != null){
                        mTabSelectedListener.onTabReselected(i);
                    }
                } else {
                    scrollToPosition(i);
                    mViewPager.setCurrentItem(i, withPageChangeAnim);
                }
            }
        });
    }

    private void buildLocationModel() {
        mLocationDatas.clear();
        for (int i = 0 ; i < mAdapter.getCount(); i++){
            LocationModel locationModel = new LocationModel();
            View childAt = mTitleContainer.getChildAt(i);
            locationModel.left = childAt.getLeft();
            locationModel.top = childAt.getTop();
            locationModel.right = childAt.getRight();
            locationModel.bottom = childAt.getBottom();
            if (childAt instanceof IPagerTitle){
                locationModel.contentLeft = ((IPagerTitle) childAt).getContentLeft();
                locationModel.contentRight = ((IPagerTitle) childAt).getContentRight();
                locationModel.contentTop = ((IPagerTitle) childAt).getContentTop();
                locationModel.contentBottom = ((IPagerTitle) childAt).getContentBottom();
            }
            mLocationDatas.add(locationModel);
        }
    }

    private void initIndicatorAndTitle() {
        List<IPagerTitle> titleView = new ArrayList<>();
        beforeInitTitleView();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            IPagerTitle pagerTitle = getTitleView(getContext(), i);
            if (pagerTitle instanceof View) {
                View view = (View) pagerTitle;
                titleView.add(pagerTitle);
                LinearLayout.LayoutParams lp;
                if (mMode == MODE_FIXED) {
                    lp = new LinearLayout.LayoutParams(getTitleWidth(getContext(), i), ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = getTitleWeight(getContext(), i);
                } else {
                    lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                if (view.getLayoutParams() != null) {
                    mTitleContainer.addView(view);
                } else {
                    mTitleContainer.addView(view, lp);
                }
                setTitleClickListener(view, i);
                setTitleClickSelectTalkBack(view, i);
            }
        }
        calculateScrollableMode(titleView);
        mPagerIndicator = getIndicator(getContext());
        if (mPagerIndicator instanceof View) {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mIndicatorContainer.addView((View) mPagerIndicator, lp);
        }
    }

    protected void beforeInitTitleView() {

    }

    private void setTitleClickSelectTalkBack(View view, final int index) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (index == mSelectedIndex) {
                    info.setCheckable(true);
                    info.setChecked(true);
                }
            }
        });
    }

    /**
     * 如果MODE_SCROLLABLE模式没有充满屏幕，则让其充满屏幕
     */
    protected void calculateScrollableMode(List<IPagerTitle> titleView) {
        if (mMode == MODE_SCROLLABLE && !titleView.isEmpty()){
            //tab的总宽度
            int totalTabWidth = 0;
            //text的总宽度
            int totalTextWidth = 0;
            for (IPagerTitle pagerTitle : titleView){
                if (pagerTitle instanceof View){
                    View view = (View) pagerTitle;
                    int tabWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft() +
                            view.getPaddingLeft() + view.getPaddingRight();
                    totalTabWidth = totalTabWidth + tabWidth;

                    int textWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft();
                    totalTextWidth = totalTextWidth + textWidth;
                }
            }
            if (totalTabWidth < getIndicatorWidth()){
                //计算出剩余间距，平均分配到每个tab上
                int spacePadding = (getIndicatorWidth() - totalTextWidth) / titleView.size();
                for (IPagerTitle pagerTitle : titleView){
                    if (pagerTitle instanceof View){
                        View view = (View) pagerTitle;
                        view.setPadding(0, view.getPaddingTop(), 0, getPaddingBottom());
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if (layoutParams != null){
                            int textWidth = pagerTitle.getContentRight() - pagerTitle.getContentLeft();
                            layoutParams.width = textWidth + spacePadding;
                        }
                    }
                }
                if (mTitleContainer != null){
                    mTitleContainer.requestLayout();
                }
            }
        }
    }

    protected int getIndicatorWidth(){
        return DeviceInfo.WIDTH;
    }

    private void onScrollViewScrolled(int position, float positionOffset) {
        boolean positionAvailable = mLocationDatas.size() > 0 && position >= 0 && position < mLocationDatas.size();
        if (mScrollView != null && positionAvailable && positionOffset > 0) {
            int currentPosition = Math.min(mLocationDatas.size() - 1, position);
            int nextPosition = Math.min(mLocationDatas.size() - 1, position + 1);
            LocationModel current = mLocationDatas.get(currentPosition);
            LocationModel next = mLocationDatas.get(nextPosition);
            //到中间再滚动
            float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollRate;
            float nextScrollTo = next.horizontalCenter() - mScrollView.getWidth() * mScrollRate;
            mScrollView.smoothScrollTo((int) (scrollTo + (nextScrollTo - scrollTo) * positionOffset), 0);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (mAdapter != null) {
            if (position < 0) {
                position = 0;
            } else if (position >= mAdapter.getCount()) {
                position = mAdapter.getCount() - 1;
            }
            if (position > 0) {
                // 模拟从前一页滑动到后一页
                onScrollViewScrolled(position - 1, 1);
            } else {
                // 模拟滑动到第0页，
                onScrollViewScrolled(0, 0.1f);
            }
        }
    }

    private void onTitleViewScrolled(int position, float positionOffset) {
        float currentPositionOffset = position + positionOffset;
        boolean leftToRight = currentPositionOffset >= mLastPositionOffset;
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE){
            if (mLastPositionOffset == currentPositionOffset){
                return;
            }
            int nextPosition = position + 1;
            boolean normalDispatch = true;
            if (positionOffset == 0.0f) {
                if (leftToRight) {
                    nextPosition = position - 1;
                    normalDispatch = false;
                }
            }
            for (int i = 0; i < mAdapter.getCount(); i++) {
                if (i == position || i == nextPosition) {
                    continue;
                }
                Float leavedPercent = mLeavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight ,true);
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, true ,false);
                    dispatchOnEnter(nextPosition, positionOffset, true ,false);
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, false, false);
                    dispatchOnEnter(position, 1.0f - positionOffset, false, false);
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, true, false);
                dispatchOnEnter(position, 1.0f - positionOffset, true, false);
            }
        } else {
            for (int i = 0 ; i < mAdapter.getCount() ; i++){
                if (i == mSelectedIndex){
                    continue;
                }
                boolean deselected = mDeselectedItems.get(i);
                if (!deselected) {
                    dispatchOnDeselected(i);
                }
                Float leavedPercent = mLeavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight,true);
                }
            }
            dispatchOnEnter(mSelectedIndex, 1f, leftToRight, true);
            dispatchOnSelected(mSelectedIndex);
        }
        mLastPositionOffset = currentPositionOffset;
    }

    private void dispatchOnLeave(int position, float leavePercent, boolean leftToRight ,boolean force){
        if (mTitleContainer == null){
            return;
        }
        boolean dragingNearby = (position == mSelectedIndex - 1 || position == mSelectedIndex + 1) && mLeavedPercents.get(position, 0.0f) != 1.0f;
        if (mIsTransform || position == mLastIndex || mScrollState == ViewPager.SCROLL_STATE_DRAGGING || dragingNearby || force) {
            View titleView = mTitleContainer.getChildAt(position);
            if (titleView instanceof IPagerTitle){
                IPagerTitle pagerTitle = (IPagerTitle) titleView;
                pagerTitle.onLeave(position, mAdapter.getCount(), leavePercent, leftToRight);
                mLeavedPercents.put(position, leavePercent);
            }
        }
    }

    private void dispatchOnEnter(int position, float enterPercent, boolean leftToRight, boolean force){
        if (mTitleContainer == null){
            return;
        }
        if (mIsTransform || position == mSelectedIndex || mScrollState == ViewPager.SCROLL_STATE_DRAGGING || force) {
            View titleView = mTitleContainer.getChildAt(position);
            if (titleView instanceof IPagerTitle){
                IPagerTitle pagerTitle = (IPagerTitle) titleView;
                pagerTitle.onEnter(position, mAdapter.getCount(), enterPercent, leftToRight);
                mLeavedPercents.put(position, 1.0f - enterPercent);
            }
        }
    }

    private void dispatchOnSelected(int index) {
        if (mTitleContainer == null){
            return;
        }
        View view = mTitleContainer.getChildAt(index);
        if (view instanceof IPagerTitle){
            IPagerTitle pagerTitle = (IPagerTitle) view;
            pagerTitle.onSelected(index, mAdapter.getCount());
            mDeselectedItems.put(index, false);
        }
    }

    private void dispatchOnDeselected(int index) {
        if (mTitleContainer == null){
            return;
        }
        View view = mTitleContainer.getChildAt(index);
        if (view instanceof IPagerTitle){
            IPagerTitle pagerTitle = (IPagerTitle) view;
            pagerTitle.onDeselected(index, mAdapter.getCount());
            mDeselectedItems.put(index, true);
        }
    }
}
