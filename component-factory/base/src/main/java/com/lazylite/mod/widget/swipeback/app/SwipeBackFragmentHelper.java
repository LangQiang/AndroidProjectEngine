package com.lazylite.mod.widget.swipeback.app;

import android.view.LayoutInflater;
import android.view.View;

import com.example.basemodule.R;
import com.lazylite.mod.widget.swipeback.SwipeBackLayout;


//by huqian

public class SwipeBackFragmentHelper {

	private final SwipeBackFragmentBase mFragment;
    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackFragmentHelper(SwipeBackFragmentBase fragment) {
    	mFragment = fragment;
    }
    
    public void onFragmentCreateView(){
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mFragment.getHostActivity()).inflate(R.layout.lrlite_base_swipeback_layout,null);
        mSwipeBackLayout.attachToFragment(mFragment);
    }
    
    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }
    
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

}
