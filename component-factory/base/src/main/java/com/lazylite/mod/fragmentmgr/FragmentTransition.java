package com.lazylite.mod.fragmentmgr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * 转换动画
 * Created by wangchenlong on 15/11/5.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FragmentTransition extends TransitionSet {
    public FragmentTransition() {
        init();
    }

    // 允许资源文件使用
    public FragmentTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
