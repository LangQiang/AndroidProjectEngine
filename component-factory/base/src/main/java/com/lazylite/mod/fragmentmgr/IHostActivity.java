package com.lazylite.mod.fragmentmgr;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by lzf on 5/21/21 10:06 AM
 */
public interface IHostActivity {
    //最底层的Fragment，例如：最底层是个ViewPager，那么需要返回ViewPager的currentFragment
    @Nullable
    default Fragment onGetMainLayerTopFragment(){
        return null;
    }
    @IdRes
    int containerViewId();//添加Fragment的容器id

    //显示隐藏Activity的其它非容器View
    default void onShowMainLayer(boolean show){}
}
