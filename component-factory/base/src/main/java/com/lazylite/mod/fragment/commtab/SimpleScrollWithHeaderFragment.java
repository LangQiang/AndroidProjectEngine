package com.lazylite.mod.fragment.commtab;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.basemodule.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.BaseFragment;
import com.lazylite.mod.widget.KwTitleBar;

/**
 * Created by lzf on 2020-02-12 11:16
 */
public abstract class SimpleScrollWithHeaderFragment extends BaseFragment {
    protected static final String ID = "key_id";
    protected static final String PARENT_PSRC = "key_psrc";
    protected static final String PSRC_INFO = "key_psrcinfo";
    protected static final String TITLE = "key_title";

    protected KwTitleBar mTitleBar;
    protected CollapsingToolbarLayout mCollapsingLayout;
    protected View mContentRootV;//内容View的根布局
    protected FrameLayout mHeaderFL;//header
    protected FrameLayout mStickyFL;//滚动悬停
    protected FrameLayout mContentFL;//内容

    protected View mToolbar;
    protected View mHeaderV;

    protected long mId;
    protected String mPsrc;
    protected String mTitle;

    private int initDisScrollDistance = 0;

    private boolean currentHeaderScrollable = true;
    private boolean isViewDestroy;

    protected abstract void initKwTitleBar(KwTitleBar titleBar);

    /**
     * 创建headerView
     */
    protected abstract View onCreateHeaderView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, boolean attachToRoot);

    /**
     * 创建滚动后header滚动完后需要悬停的View
     */
    protected abstract View onCreateStickyView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, boolean attachToRoot);

    /**
     * 创建位于悬停View下方的内容View，可以是任何View
     */
    protected abstract View onCreateContentView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, boolean attachToRoot);

    protected int getContentLayoutResId() {
        return R.layout.fragment_simple_scroll_with_herader;
    }

    /**
     * headerView滚动监听
     */
    protected void onHeaderScroll(int state, int offset, float percent) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            Bundle args = getArguments();
            mId = args.getLong(ID);
            mPsrc = args.getString(PARENT_PSRC);
            mTitle = args.getString(TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getContentLayoutResId(), container, false);
        mTitleBar = rootView.findViewById(R.id.detail_page_title);
        mCollapsingLayout = rootView.findViewById(R.id.collapsing_layout);
        mContentRootV = rootView.findViewById(R.id.cl_content_root);
        mHeaderFL = rootView.findViewById(R.id.detail_page_head_root);
        mStickyFL = rootView.findViewById(R.id.detail_page_sticky_root);
        mContentFL = rootView.findViewById(R.id.detail_page_content_root);
        mToolbar = rootView.findViewById(R.id.toolbar_layout);

        adjustViewHeight();
        initKwTitleBar(mTitleBar);
        AppBarLayout appBarLayout = rootView.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new HeadScrollListener());

        //抽象处理
        mHeaderV = onCreateHeaderView(inflater, mHeaderFL, true);
        onCreateStickyView(inflater, mStickyFL, true);
        onCreateContentView(inflater, mContentFL, true);

        isViewDestroy = false;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != mToolbar && null != mToolbar.getLayoutParams()){
            initDisScrollDistance = mToolbar.getLayoutParams().height;
        }
        currentHeaderScrollable = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewDestroy = true;
    }

    protected final boolean isFragmentAlive() {
        return !isViewDestroy && (getActivity() != null && !getActivity().isFinishing() && !isDetached());
    }

    protected void enableHeaderScroll(boolean enable) {
        if(currentHeaderScrollable == enable){
            return;
        }
        if (null == mToolbar || null == mHeaderV) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = mToolbar.getLayoutParams();
        if (null == layoutParams) {
            return;
        }
        currentHeaderScrollable = enable;
        if(enable){
            layoutParams.height = initDisScrollDistance;
        }else {
            layoutParams.height = mHeaderV.getHeight();
        }
        mToolbar.setLayoutParams(layoutParams);
    }

    /**
     * 对当前的整个head高度和悬停的高度再加上状态栏的高度
     */
    private void adjustViewHeight() {
        //api19以上如若做透明状态栏需要调整头部高度，
        if (null == mToolbar) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int h = ScreenUtility.dip2px(ScreenUtility.getTitleBarHeightDP(getContext()));
            // header域整体高度
            /*ViewGroup.LayoutParams params1 = mHeaderFL.getLayoutParams();
            params1.height += h;
            mHeaderFL.setLayoutParams(params1);*/
            // 此处调整占位用的Toolbar，真正显示的还是KwTitleBar
            ViewGroup.LayoutParams params2 = mToolbar.getLayoutParams();
            params2.height += h;
            mToolbar.setLayoutParams(params2);
        }
    }

    public static abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
        public static final int EXPANDED = 0;
        public static final int COLLAPSED = 1;
        public static final int SCROLLING = 2;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
            int state;
            if (offset == 0) {
                state = EXPANDED;
            } else if (Math.abs(offset) >= appBarLayout.getTotalScrollRange()) {
                state = COLLAPSED;
            } else {
                state = SCROLLING;
            }
            float percent = (float) Math.abs(offset) / (appBarLayout.getTotalScrollRange());
            onStateChanged(state, offset, percent);
        }

        public abstract void onStateChanged(int state, int offset, float percent);
    }//AppBarStateChangeListener end

    /**
     * 头部滑动监听
     */
    private class HeadScrollListener extends AppBarStateChangeListener {

        @Override
        public void onStateChanged(int state, int offset, float percent) {
            onHeaderScroll(state, offset, percent);
        }
    }//HeadScrollListener end
}
