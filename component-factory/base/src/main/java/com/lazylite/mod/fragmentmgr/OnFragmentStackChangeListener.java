package com.lazylite.mod.fragmentmgr;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 监听fragment栈中增删事件
 * Created by tc :)
 */
public interface OnFragmentStackChangeListener {

    /**
     * 栈顶添加了一个Fragment，【在栈顶添加后回调】。
     *
     * @param top 添加的 Fragment
     * @param startParameter 添加的 Fragment 的 StartParameter
     */
    void onPushFragment(Fragment top,StartParameter startParameter);

    /**
     * 【在栈顶移除后回调】。<br/>
     *
     * 【栈顶】移除了一个Fragment，此方法不会与 onPushFragment() 同时调用。<br/>
     * 此方法只会在FragmentOperation关闭Fragment的时候触发，不会在showFragment()时触发（在showFragment()时，无论是否需要移除Fragment，都不会触发）。<br/>
     * 即使一次性关闭N个页面，也不会回调多次，只回调一次。<br/>
     *
     * @param nowTop 移除之后新露出的fragment，如果为null或者为{@link IHostActivity#onGetMainLayerTopFragment()}证明所有Fragment都关闭了
     */
    void onPopFragment(@Nullable Fragment nowTop);
}
