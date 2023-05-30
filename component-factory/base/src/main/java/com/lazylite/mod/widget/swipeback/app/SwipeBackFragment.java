
package com.lazylite.mod.widget.swipeback.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.widget.swipeback.SwipeBackLayout;


//by huqian

public class SwipeBackFragment extends Fragment implements SwipeBackFragmentBase {

    private SwipeBackFragmentHelper mHelper;
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (isNeedSwipeBack()) {
            mHelper = new SwipeBackFragmentHelper(this);
            mHelper.onFragmentCreateView();
            Integer size = getEdgeSize();
            if (size != null) {
                getSwipeBackLayout().setEdgeSize(size);
            } else {
                int screenWidth = DeviceInfo.WIDTH;
                if (screenWidth != 0) {
                    getSwipeBackLayout().setEdgeSize(screenWidth);
                }
            }
        }
        setSwipeBackEnable(isNeedSwipeBack());
    }

    @Override
	public SwipeBackLayout getSwipeBackLayout() {
        if (mHelper == null) {
            return null;
        }
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public View getFragmentView() {
        return getView();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        if (getSwipeBackLayout() != null) {
            getSwipeBackLayout().setEnableGesture(enable);
        }
    }

    @Override
    public void scrollToFinishActivity() {
        if (getSwipeBackLayout() != null) {
            getSwipeBackLayout().scrollToFinishActivity();
        }
    }
    
    @Override
    public void close(){
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.popBackStack();
    }

    /**
     * ！！！这个是加布局层级的
     * ！！！这个是加布局层级的
     * ！！！这个是加布局层级的
     *
     * 是否需要+上左滑退出布局层级，除了4个主tab和内部子页面绝大数页面应该都要
     * 需要动态改变左滑是否可用的话，再加了布局层级（return true）的基础上去调用setSwipeBackEnable（true/false）
     * @return true是需要，false是不需要
     */
    public boolean isNeedSwipeBack() {
        return false;
    }

    @Nullable
    public Integer getEdgeSize() {
        return null;
    }
    @Override
    public Activity getHostActivity() {
        return getActivity();
    }


}
