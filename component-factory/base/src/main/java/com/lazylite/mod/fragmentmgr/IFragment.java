package com.lazylite.mod.fragmentmgr;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

/**
 * Created by lzf on 5/27/21 5:02 PM
 */
public interface IFragment {
    void setFragmentType(@FragmentType int type);

    void setHideFragmentView(boolean hide);

    boolean isHideFragmentView();

    //无特殊需求，这个完全不必实现，因为在showFragment()时，
    // 会优先查找StartParams中的tag（这样可以非常直观的将SingleTask/SingleTop打开方式的Fragment设置成一个固定的tag）
    //showFragment()中，打开方式查找Fragment是根据tag来查找的。
    default String tag(){
        return "";
    }//唯一标识

    /** 返回 Fragment 自身的顶部 Fragment；给 带 tab 的 Fragment 用的，如果自身没有包含其它 Fragment ，返回自身就好。*/
    @NonNull
    default IFragment topContentFragment(){
        return this;
    }
    /** 如果自身是个tabFragment ，那么处理下这个tab切换*/
    default boolean changeContentTab(int tabIndex){return false;}

    void onNewIntent(Bundle args);

    boolean onKeyDown(int keyCode, KeyEvent event);
}
