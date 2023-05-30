package com.lazylite.mod.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lazylite.mod.App;
import com.lazylite.mod.fragmentmgr.FragmentOperation;
import com.lazylite.mod.fragmentmgr.FragmentType;
import com.lazylite.mod.fragmentmgr.IFragment;
import com.lazylite.mod.fragmentmgr.StartParameter;
import com.lazylite.mod.permission.Permission;
import com.lazylite.mod.utils.KwSystemSettingUtils;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.swipeback.SwipeBackLayout;
import com.lazylite.mod.widget.swipeback.app.SwipeBackFragment;

import timber.log.Timber;

// by huqian
// 继承BaseFragment之后使用fragmentcontrol弹出后能收到onkeydown消息

public class BaseFragment extends SwipeBackFragment implements IFragment {

    private int mFragmentType = FragmentType.TYPE_NONE;
    private boolean mIsHideFragmentView = false;

    public boolean needSetStatusBarBlack = true;

    private StartParameter parameter;

    // 内容容器，有可能对它进行移除添加嵌套皮肤层或者对它的高度进行调整
    private ViewGroup mContentView;

    /**
     * 一个干净的不带皮肤的fragment
     */
    public boolean isCleanFrg = false;
    protected FragmentActivity mActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHideFragmentView(mIsHideFragmentView);
        //防止Fragment点击透传
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(v -> {

            });
        }
        mActivity = getActivity();
        if (isCleanFrg) {
            return;
        }

        SwipeBackLayout swipeView = getSwipeBackLayout();
        if (swipeView != null) {// 有左滑层级
            mContentView = (ViewGroup) swipeView.getChildAt(0);
            swipeView.setSwipeListener(new SwipeBackLayout.SwipeListener() {
                @Override
                public void onScrollStateChange(int state, float scrollPercent) {
                    if (state == SwipeBackLayout.STATE_IDLE) {
                        FragmentOperation.getInstance().showPreFragment(false);
                        FragmentOperation.getInstance().showHostActivityLayer(false);
                    } else {
                        FragmentOperation.getInstance().showPreFragment(true);
                        if (FragmentOperation.getInstance().getPreFragment() == null) {//到顶了
                            FragmentOperation.getInstance().showHostActivityLayer(true);
                        }
                    }
                }

                @Override
                public void onEdgeTouch(int edgeFlag) {

                }

                @Override
                public void onScrollOverThreshold() {

                }

                @Override
                public void onHasScroll() {

                }
            });
        } else {
            mContentView = (ViewGroup) getView();
        }
        setTitleBarHeight();
        setBottomMargin();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Permission.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        KwSystemSettingUtils.resetStatusBarBlack(getActivity());

        Permission.onFragmentDestroy(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(needSetStatusBarBlack){
            KwSystemSettingUtils.resetStatusBarBlack(getActivity());
        }
        Timber.tag("BaseFragment").d("BaseFragment == onResume()");
    }


    @Override
    public void onPause() {
        super.onPause();
        Timber.tag("BaseFragment").d("BaseFragment == onPause()");
    }


    //返回true表示不向下传递消息了
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //非主tab页的fragment不响应menu键吊起侧边菜单，Add BY Litiancheng
        return KeyEvent.KEYCODE_MENU == keyCode && !FragmentOperation.getInstance().isMainLayerShow();
    }

    public void close() {
        Fragment topFragment = FragmentOperation.getInstance().getTopFragment();
        if (topFragment == this) {
            FragmentOperation.getInstance().close();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void setTitleBarHeight() {
        //api19以上如若做透明状态栏需要调整头部高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getView() != null) {
            View titleBar = getView().findViewWithTag("titleBar");
            addViewPaddingTop(titleBar);
        }
    }

    /**
     * 调整内容容器的底部margin，预留出播放栏那么高的位置
     */
    protected void setBottomMargin() {
        if (mContentView == null || mFragmentType != FragmentType.TYPE_SUB) {
            return;
        }
        final int height = ScreenUtility.getBottomHeightPx(App.getInstance());// 单位：px
        LayoutParams lp = mContentView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) lp).bottomMargin = height;
        }
    }

    protected void addViewPaddingTop(View view) {
        if (view != null) {
            LayoutParams params = view.getLayoutParams();
            int h = ScreenUtility.dip2px(ScreenUtility.getTitleBarHeightDP(App.getInstance()));
            params.height += h;
            view.setPadding(view.getPaddingLeft(), h + view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            view.setLayoutParams(params);
        }
    }

    public void onNewIntent(Bundle bundle) {
    }

    public void setFragmentType(int type) {
        this.mFragmentType = type;
    }

    @Override
    public void setHideFragmentView(final boolean hide) {
        mIsHideFragmentView = hide;
        final View fragmentView = getView();
        if (null != fragmentView) {
            fragmentView.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            mIsHideFragmentView = hide;
        }
    }

    @Override
    public boolean isHideFragmentView() {
        return mIsHideFragmentView;
    }

    @FragmentType
    public int getFragmentType() {
        return mFragmentType;
    }

    @Override
    public boolean isNeedSwipeBack() {
        // 默认viewpager里面的子tab都不添加左滑层级了，
        // 同时所有sub的和full的默认可以左滑，
        // 不需要的话自己复写返回false
        return mFragmentType != FragmentType.TYPE_NONE;
    }

    protected View getContentView() {
        return mContentView;
    }

    public StartParameter getParameter() {
        return parameter;
    }

    public void setParameter(StartParameter parameter) {
        this.parameter = parameter;
    }

}
