package com.lazylite.mod.widget;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.permission.Permission;
import com.lazylite.mod.utils.KwSystemSettingUtils;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.widget.swipeback.app.SwipeBackFragment;
import com.lazylite.mod.widget.swipeback.SwipeBackLayout;

// by huqian
// 继承BaseFragment之后使用fragmentcontrol弹出后能收到onkeydown消息

public class BaseFragment extends SwipeBackFragment implements IFragment {

	private int mFragmentType = FragmentType.TYPE_NONE;

	protected boolean mUseChangeTheme = true;

	public boolean needSetStatusBarBlack = true;

	public static int TITLE_BAR_DP=25;

	private StartParameter parameter;


	private String mPreNCFragmemtTag;//将上一次左滑退出异常关不掉的那个fragment tag ，记录在top fragment上

	private ViewGroup skinlayer;

	// 内容容器，有可能对它进行移除添加嵌套皮肤层或者对它的高度进行调整
	private ViewGroup mContentView;

	public String mSource;
	public String mCategory;

	/**
	 * 一个干净的不带皮肤的fragment
	 */
	public boolean isCleanFrg = false;
	protected FragmentActivity mActivity;

	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (isCleanFrg){
			return;
		}

		SwipeBackLayout swipeView = getSwipeBackLayout();
		if (swipeView != null) {// 有左滑层级
			mContentView = (ViewGroup) swipeView.getChildAt(0);
		} else {
			mContentView = (ViewGroup) getView();
		}
		mActivity = getActivity();
		setTitleBarHeight();
		setBottomMargin();
	}

	/**
	 * 是否要将主页面设为GONE, 子类中进行重写, 一些tab页面也可以将这个方法返回false
	 * @return 是否要将主页面设为GONE
     */
	protected boolean needToHideMainContent() {
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Permission.onActivityResult(this,requestCode,resultCode,data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Permission.onRequestPermissionResult(this,requestCode,permissions,grantResults);
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		KwSystemSettingUtils.resetStatusBarBlack(getActivity());
		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
//				MainActivity mainActivity = App.getMainActivity();
//				if (mainActivity != null) {
//					mainActivity.clearWeakReference();
//				}
			}
		});

		Permission.onFragmentDestroy(this);

	}

	protected boolean bActive = false;

	// 继承自BaseFragment的类都需Resume代替onResume，Pause代替onPause，以保证只调用一次
	public void Resume(){
	}


	public void Pause(){
	}


	@Override
	final public void onResume() {
		super.onResume();
		if (!bActive) {
			Fragment topFragment = FragmentOperation.getInstance().getTopFragment();
			if (topFragment == this) {
				bActive = true;
				if(needSetStatusBarBlack){
					KwSystemSettingUtils.resetStatusBarBlack(getActivity());
				}
				Resume();
			}else{
			    //
			}
		}
	}


	@Override
	final public void onPause() {
		super.onPause();
		if (bActive) {
			bActive = false;
			Pause();
		}
	}


	//返回true表示不向下传递消息了
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        //非主tab页的fragment不响应menu键吊起侧边菜单，Add BY Litiancheng
		if (KeyEvent.KEYCODE_MENU == keyCode && !FragmentOperation.getInstance().isMainLayerShow()) {
			return true;
		}
		return false;
	}

	public void close() {
		Fragment topFragment = FragmentOperation.getInstance().getTopFragment();
		if (topFragment == this) {
			FragmentOperation.getInstance().close();
		}
	}

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
		if(view != null){
            LayoutParams params = view.getLayoutParams();
            int h = ScreenUtility.dip2px(ScreenUtility.getTitleBarHeightDP(App.getInstance()));
            params.height += h;
            view.setPadding(view.getPaddingLeft(), h+view.getPaddingTop(),view.getPaddingRight(), view.getPaddingBottom());
            view.setLayoutParams(params);
        }
	}


	/**
	 * 操作成功时返回到上层fragmeng时的回调，空实现，如果有需要的话子类去重写
	 * @param bundle
	 */
	public void onSuccessResult(Bundle bundle) {
	}

	/**
	 * 处理不换主题页面的提示语, 在onCreate中使用
	 * @param changeTheme
     */
	protected void useChangeTheme(boolean changeTheme) {
		mUseChangeTheme = changeTheme;
	}


	public String getPreNCFragmemtTag() {
		return mPreNCFragmemtTag;
	}

	public void setPreNCFragmemtTag(String preNCFragmemtTag) {
		this.mPreNCFragmemtTag = preNCFragmemtTag;
	}

	public void onNewIntent(Bundle bundle) {
	}

	public void setFragmentType(int type) {
		this.mFragmentType = type;
	}

	@Override
	public String tag() {
		return this.getClass().getName();
	}

	@FragmentType
	public int getFragmentType(){
		return mFragmentType;
	}

	@Override
	public boolean isNeedSwipeBack() {
		// 默认viewpager里面的子tab都不添加左滑层级了，
		// 同时所有sub的和full的默认可以左滑，
		// 不需要的话自己复写返回false
		return mFragmentType != FragmentType.TYPE_NONE;
	}

	protected View getContentView(){
		return mContentView;
	}

	public StartParameter getParameter() {
		return parameter;
	}

	public void setParameter(StartParameter parameter) {
		this.parameter = parameter;
	}

	//解决气泡显示隐藏闪的问题
	public boolean needHidePlayBubbleOnResume() {
		return true;
	}
}
