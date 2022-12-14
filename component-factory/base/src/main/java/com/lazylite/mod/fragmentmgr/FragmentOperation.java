package com.lazylite.mod.fragmentmgr;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.KwDebug;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 除了关闭时携带路由参数，尽量不要再引入其他业务逻辑
 * <p>
 * Created by tc :)
 */
public class FragmentOperation {
    private static final String TAG = "FragmentOperation";
    private static final String SEPARATOR = "###";
    private final LinkedList<Pair<String, Fragment>> mStack = new LinkedList<>();
    private StartParameter mDefaultParameter;
    private FragmentManager mFragmentManager;
    private final AtomicInteger mTagAtomic = new AtomicInteger();
    private OnFragmentStackChangeListener mListener;

    private Activity mBindActivity;
    private IHostActivity mHostActivityOpt;


    private FragmentOperation() {
    }

    private static class SingletonHolder {
        private static final FragmentOperation INSTANCE = new FragmentOperation();
    }

    public static FragmentOperation getInstance() {
        KwDebug.mustMainThread();
        return SingletonHolder.INSTANCE;
    }

    public void bind(FragmentActivity activity, IHostActivity optFunInterface, OnFragmentStackChangeListener onFragmentStackChangeListener) {
        mBindActivity = activity;
        mFragmentManager = activity.getSupportFragmentManager();
        mDefaultParameter = getDefaultParameter();
        if (onFragmentStackChangeListener != null) {
            mListener = onFragmentStackChangeListener;
        }
        mHostActivityOpt = optFunInterface;
    }

    public Activity getBindActivity(){
        return mBindActivity;
    }

    @NonNull
    private StartParameter getDefaultParameter() {
        return new StartParameter.Builder()
                .withEnterAnimation(0)
                .withPopCurrent(false)
                .withHideBottomLayer(true)
                .withStartMode(StartMode.STANDARD)
                .withShareViews(null)
                .build();
    }

    public void unBind() {
        mStack.clear();
        mFragmentManager = null;
        mListener = null;
    }

    /**
     * 什么参数都不用管的标准形式添加一个底部控制栏之上的fragment
     *
     * @param fragment
     */
    public void showSubFragment(Fragment fragment) {
        showSubFragment(fragment, mDefaultParameter);
    }

    /**
     * 什么参数都不用管的标准形式添加一个全屏Fragment
     *
     * @param fragment
     */
    public void showFullFragment(Fragment fragment) {
        showFullFragment(fragment, mDefaultParameter);
    }

    /**
     * 需要个性化带点参数什么的添加一个底部控制栏之上的fragment
     *
     * @param fragment
     * @param parameter
     */
    public void showSubFragment(Fragment fragment, StartParameter parameter) {
        showFragment(fragment, FragmentType.TYPE_SUB, parameter);
    }

    /**
     * 需要个性化带点参数什么的添加一个全屏Fragment
     *
     * @param fragment
     * @param parameter
     */
    public void showFullFragment(Fragment fragment, StartParameter parameter) {
        showFragment(fragment, FragmentType.TYPE_FULL, parameter);
    }

    /**
     * 跳转回主页面
     */
    public void navigateToHome() {
        if (mStack.isEmpty()) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Pair<String, Fragment> pair : mStack) {
            transaction.remove(pair.second);
        }
        transaction.commitAllowingStateLoss();
        mStack.clear();
        mTagAtomic.set(0);
        if (mListener != null) {
            mListener.onShowMainLayer(true);
        }
    }
    /**
     * 跳转到某目标fragment，关闭其上所有
     *
     * @param tag           指定到tag
     * @param includeTarget 是否包含关闭目标fragment
     *                      true同时关闭目标fragment，false保留目标fragment
     */
    public void navigateToFragment(String tag, boolean includeTarget) {
        if (TextUtils.isEmpty(tag) || mStack.isEmpty()) {
            return;
        }

        List<Pair<String, Fragment>> findArrary = findTargetFragmentAndUpList(tag);
        if (findArrary == null || findArrary.isEmpty()) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Pair<String, Fragment> target = findArrary.get(findArrary.size() - 1);
        Fragment showFragment;
        for (Pair<String, Fragment> pair : findArrary) {
            if (pair == target) {
                continue;
            }
            transaction.remove(pair.second);
            mStack.remove(pair);
        }
        if (includeTarget) {
            int targetIndex = mStack.indexOf(target);
            if (targetIndex - 1 < 0) {
                transaction.remove(target.second).commitAllowingStateLoss();
                mStack.remove(target);
                if (mListener != null) {
                    mListener.onShowMainLayer(true);
                }
                return;
            } else {
                showFragment = mStack.get(targetIndex - 1).second;
                transaction.show(showFragment)
                        .remove(target.second)
                        .commitNowAllowingStateLoss();
                mStack.remove(target);
                safeShowFragmentView(showFragment);
                showFragment.onResume();
            }
        } else {
            showFragment = target.second;
            transaction.show(showFragment).commitNowAllowingStateLoss();
            safeShowFragmentView(showFragment);
            showFragment.onResume();
        }
        // 移除了n个之后（n>=1）
        if (mListener != null) {
            mListener.onPopFragment(getTopFragment());
        }
    }

    /**
     * 正常按次序pop出去一个栈顶fragment
     */
    public boolean close() {
        return closeWithRoute(null);
    }

    /**
     * 有多进程先路由到activity带参数的关闭
     *
     * @param extraParam
     */
    public boolean closeWithRoute(Map<String, Object> extraParam) {
        // 如果有多进程路由携带参数的情况得处理一下
        /*NaviBuilder currentBuilder = NaviMgr.currentNaviBuilder();
        if (currentBuilder != null && currentBuilder.canGoBack()) {
            NaviBuilder builder = currentBuilder.getBackBuilder();
            if (extraParam != null) {
                for (Map.Entry<String, Object> e : extraParam.entrySet()) {
                    if (TextUtils.isEmpty(e.getKey())) {
                        continue;
                    }
                    builder = builder.addParam(NavigableItems.NAVI_ROOT_ACTIVITY,
                            e.getKey(),
                            (Serializable) e.getValue());
                }
            }
            builder.navigate(MainActivity.getInstance());
        }*/


        if (!mStack.isEmpty()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (mStack.size() == 1) {
                transaction.remove(getTopFragment()).commitNowAllowingStateLoss();
                mStack.removeLast();
                if (mListener != null) {
                    mListener.onShowMainLayer(true);
                }
                return true;
            } else {
                Fragment showFragment = mStack.get(mStack.size() - 2).second;
                LogMgr.d(TAG,"close Fragment 【"
                        + getTopFragment().getClass().getName()
                        + "】，and show pre Fragment:"
                        + showFragment.getClass().getName());
                transaction
                        .show(showFragment)
                        .remove(getTopFragment())
                        .commitNowAllowingStateLoss();
                mStack.removeLast();
                safeShowFragmentView(showFragment);
                showFragment.onResume();
                if (mListener != null) {
                    mListener.onPopFragment(getTopFragment());
                }
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取top fragment
     */
    public Fragment getTopFragment() {
        if (mStack != null && !mStack.isEmpty()) {
            return mStack.getLast().second;
        } else {
            // viewpager
            if (null == mHostActivityOpt) {
                return null;
            }
            return mHostActivityOpt.onGetManLayerTopFragment();
        }
    }

    /**
     * 获取前一个fragment，不要滥用，目前就在左滑退出做显示隐藏使用
     */
    public Fragment getPreFragment() {
        if (mStack == null || mStack.isEmpty() || mStack.size() == 1) {
            // viewpager
            return null;
        } else {
            return mStack.get(mStack.size() - 2).second;
        }
    }

    /**
     * 是否是在主页
     */
    public boolean isMainLayerShow() {
        return mStack.size() == 0;
    }

    public OnFragmentStackChangeListener getOnFragmentStackChangeListener() {
        return mListener;
    }

    /**
     * 精准查找你指定的tag的fragment
     *
     * @param tag
     */
    public Fragment findFragmentByTag(String tag) {
        for (int i = 0, size = mStack.size(); i < size; i++) {
            Pair<String, Fragment> pair = mStack.get(i);
            if (pair.first.equals(tag)) {
                return pair.second;
            }
        }
        return null;
    }

    /**
     * 根据Fragment的Class来判断在栈中已经存在多少实例
     */
    public int getFragmentCountByClazz(Class clazz) {
        int count = 0;
        for (int i = 0, size = mStack.size(); i < size; i++) {
            Pair<String, Fragment> pair = mStack.get(i);
            if (pair.second.getClass() == clazz) {
                count++;
            }
        }
        return count;
    }

    /**
     * 最终show
     *
     * @param fragment  目标Fragment
     * @param type      FragmentType
     * @param parameter 跳转参数
     */
    private void showFragment(Fragment fragment, @FragmentType int type, StartParameter parameter) {
        KwDebug.classicAssert(fragment instanceof IFragment, TAG + " 没有继承IFragment");
        ((IFragment) fragment).setFragmentType(type);
        if (parameter == null) {
            parameter = mDefaultParameter;
        }
        String tag;
        if (!TextUtils.isEmpty(parameter.tag)) {
            tag = parameter.tag;
        } else if (fragment instanceof IFragment) {
            tag = ((IFragment) fragment).tag();
        } else {
            tag = fragment.getClass().getName()
                    + SEPARATOR
                    + mTagAtomic.incrementAndGet();
        }
        if (mStack.isEmpty()) {
            openStandard(fragment, tag, parameter);
        } else {
            handlerStartMode(fragment, tag, parameter);
        }
        if (mListener != null) {
            mListener.onPushFragment(fragment);
        }
        LogMgr.d(TAG, "show Fragment 【"
                + fragment.getClass().getName()
                + "】,FragmentType :"
                + type
                + ",StartParameter :"
                + parameter);
    }

    private void handlerStartMode(Fragment fragment, String tag, StartParameter parameter) {
        @StartMode
        int mode = parameter.startMode;
        switch (mode) {
            case StartMode.STANDARD:
                openStandard(fragment, tag, parameter);
                break;
            case StartMode.SINGLE_INSTANCE:
                openSingleInstance(fragment, tag, parameter);
                break;
            case StartMode.SINGLE_TOP:
                openSingleTop(fragment, tag, parameter);
                break;
            case StartMode.SINGLE_TASK:
                openSingleTask(fragment, tag, parameter);
                break;
        }
    }

    /**
     * SingleTop
     * 如果栈顶就是目标Fragment的实例触发其onNewInstance，可以处理预先携带的bundle数据
     * 如果栈顶不是目标Fragment，重新打开一个实例
     *
     * @param fragment  目标Fragment
     * @param tag       目标Fragment tag
     * @param parameter 跳转参数
     */
    private void openSingleTop(Fragment fragment, String tag, StartParameter parameter) {
        if (!tag.startsWith(getRealTag(mStack.getLast().first))) {
            openStandard(fragment, tag, parameter);
        } else {
            // 触发onNewIntent，自己带着刷新参数过去
            Fragment target = mStack.getLast().second;
            ((IFragment) target).onNewIntent(parameter.bundle);
        }
    }

    /**
     * SingleTask
     * 倒序寻找栈中有没有目标Fragment的实例，如果有，将其上面全部弹出，触发其onNewInstance，
     * 可以处理预先携带的bundle数据
     *
     * @param fragment  目标Fragment
     * @param tag       目标Fragment tag
     * @param parameter 跳转参数
     */
    private void openSingleTask(Fragment fragment, String tag, StartParameter parameter) {
        ListIterator<Pair<String, Fragment>> it = mStack.listIterator(mStack.size());
        List<Pair<String, Fragment>> upList = new ArrayList<>();
        Fragment target = null;
        while (it.hasPrevious()) {
            Pair<String, Fragment> element = it.previous();
            if (tag.startsWith(getRealTag(element.first))) {
                target = element.second;
                break;
            } else {
                upList.add(element);
            }
        }

        if (target != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            for (Pair<String, Fragment> pair : upList) {
                transaction.remove(pair.second);
                mStack.remove(pair);
            }
            transaction.show(target).commitNowAllowingStateLoss();
            ((IFragment) target).onNewIntent(parameter.bundle);
            safeShowFragmentView(target);
            target.onResume();
            return;
        }

        openStandard(fragment, tag, parameter);
    }

    /**
     * SingleInstance
     * 模拟单例实例，比方播放页
     *
     * @param fragment  目标Fragment
     * @param tag       目标Fragment tag
     * @param parameter 跳转参数
     */
    private void openSingleInstance(Fragment fragment, String tag, StartParameter parameter) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        handlerAnimation(fragment, parameter, transaction);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE)
                .add(mHostActivityOpt.containerViewId(), fragment, tag);

        Fragment preFragment = mStack.getLast().second;
        if (!TextUtils.isEmpty(parameter.popEndTag)) {
            List<Pair<String, Fragment>> findArrary
                    = findTargetFragmentAndUpList(parameter.popEndTag);
            if (findArrary == null || findArrary.isEmpty()) {
                return;
            }
            Pair<String, Fragment> target = findArrary.get(findArrary.size() - 1);
            for (Pair<String, Fragment> pair : findArrary) {
                if (!parameter.isIncludePopEnd && pair == target) {
                    continue;
                }
                transaction.remove(pair.second);
                mStack.remove(pair);
            }
        } else if (parameter.isPopCurrent) {
            transaction.remove(preFragment);
            mStack.remove(mStack.getLast());
        } else {
            // isHideBottomLayer && 没入场动画 才能隐藏下层
            if (parameter.isHideBottomLayer && parameter.enterAnimation == 0) {
                transaction.hide(preFragment);
            }
            // 只要前面不pop掉pre就得触发其onPause
            preFragment.onPause();
        }

        // 把存在的实例都干掉,如果有的话
        ListIterator<Pair<String, Fragment>> it = mStack.listIterator();
        while (it.hasNext()) {
            Pair<String, Fragment> element = it.next();
            if (tag.startsWith(getRealTag(element.first))) {
                transaction.remove(element.second);
                it.remove();
            }
        }

        transaction.commitAllowingStateLoss();
        mStack.add(new Pair<>(tag, fragment));
    }

    /**
     * 标准启动
     *
     * @param fragment  目标Fragment
     * @param tag       目标Fragment tag
     * @param parameter 跳转参数
     */
    private void openStandard(Fragment fragment, String tag, StartParameter parameter) {
        if (mFragmentManager == null) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        handlerAnimation(fragment, parameter, transaction);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE)
                .add(mHostActivityOpt.containerViewId(), fragment, tag);
        if (mStack.isEmpty()) {
            if (mListener != null) {
                mListener.onHideMainLayer(parameter.isHideBottomLayer
                        && parameter.enterAnimation == 0);
            }
            transaction.commitAllowingStateLoss();
            mStack.add(new Pair<>(tag, fragment));
            return;
        }

        Fragment preFragment = mStack.getLast().second;
        if (!TextUtils.isEmpty(parameter.popEndTag)) {
            List<Pair<String, Fragment>> findArrary
                    = findTargetFragmentAndUpList(parameter.popEndTag);
            if (findArrary == null || findArrary.isEmpty()) {
                return;
            }
            Pair<String, Fragment> target = findArrary.get(findArrary.size() - 1);
            for (Pair<String, Fragment> pair : findArrary) {
                if (!parameter.isIncludePopEnd && pair == target) {
                    continue;
                }
                transaction.remove(pair.second);
                mStack.remove(pair);
            }
        } else if (parameter.isPopCurrent) {
            transaction.remove(preFragment);
            mStack.remove(mStack.getLast());
        } else {
            // isHideBottomLayer && 没入场动画 才能隐藏下层
            if (parameter.isHideBottomLayer && parameter.enterAnimation == 0) {
                transaction.hide(preFragment);
            }
            // 只要前面不pop掉pre就得触发其onPause
            preFragment.onPause();
        }

        transaction.commitAllowingStateLoss();
        mStack.add(new Pair<>(tag, fragment));
    }

    /**
     * 处理动画和共享元素，其实可以整个策咯暴露出去
     *
     * @param fragment
     * @param parameter
     * @param transaction
     */
    private void handlerAnimation(Fragment fragment, StartParameter parameter,FragmentTransaction transaction) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<Map.Entry<View, String>> shareView = parameter.shareViews;
            if (shareView != null && !shareView.isEmpty()) {
                fragment.setSharedElementEnterTransition(new FragmentTransition());
                fragment.setExitTransition(new Fade(Fade.OUT));
                fragment.setEnterTransition(new Fade(Fade.IN));
                fragment.setSharedElementReturnTransition(new FragmentTransition());
                for (int i = 0, size = shareView.size(); i < size; i++) {
                    Map.Entry<View, String> item = shareView.get(i);
                    transaction.addSharedElement(item.getKey(), item.getValue());
                }
            }
        }

        if (parameter.enterAnimation != 0 || parameter.outerAnimation != 0) {
            transaction.setCustomAnimations(parameter.enterAnimation, parameter.outerAnimation);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = getTopFragment();
        return fragment != null
                && fragment instanceof IFragment
                && ((IFragment) fragment).onKeyDown(keyCode, event);
    }

    /**
     * Debug 方法：
     * 获取fragment栈队列
     */
    public List<DebugFragmentStack> getStack() {
        if (mStack == null || mStack.size() == 0) {
            return null;
        }
        List<DebugFragmentStack> fragmentRecordList = new ArrayList<>();
        for (Pair<String, Fragment> pair : mStack) {
            fragmentRecordList.add(new DebugFragmentStack(pair.first, getChildFragmentRecords(pair.second)));
        }
        return fragmentRecordList;
    }

    /**
     * Debug 方法：
     * 获取子fragment栈队列
     */
    private List<DebugFragmentStack> getChildFragmentRecords(Fragment parentFragment) {
        List<DebugFragmentStack> fragmentRecords = new ArrayList<>();
        List<Fragment> fragmentList = parentFragment.getChildFragmentManager().getFragments();
        if (fragmentList == null || fragmentList.isEmpty()) {
            return null;
        }
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment != null) {
                fragmentRecords.add(new DebugFragmentStack(fragment.getClass().getSimpleName(),
                        getChildFragmentRecords(fragment)));
            }
        }
        return fragmentRecords;
    }

    /**
     * 移除mStack中指定的fragment
     *
     * @param fragment
     */
    public void removeFragment(Fragment fragment) {
        if(fragment == getTopFragment()){
            close();
            return;
        }
        ListIterator<Pair<String, Fragment>> accurateIt = mStack.listIterator(mStack.size());
        while (accurateIt.hasPrevious()) {
            Pair<String, Fragment> pair = accurateIt.previous();
            if (fragment == pair.second) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.remove(fragment).commitAllowingStateLoss();
                mStack.remove(pair);
                break;
            }
        }
    }

    /**
     * 确保无论左滑或者back操作，当前的露出fragment必须得显示出来view
     * @param showFragment
     */
    private void safeShowFragmentView(Fragment showFragment) {
        View view = showFragment.getView();
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private String getRealTag(String tag) {
        if (!tag.contains(SEPARATOR)) {
            return tag;
        }
        String[] strings = tag.split(SEPARATOR);
        return strings[0];
    }

    /**
     * 找到目标fragment和其上所有framgent的集合
     * @param tag 目标fragment tag
     */
    private List<Pair<String, Fragment>> findTargetFragmentAndUpList(String tag) {
        ListIterator<Pair<String, Fragment>> accurateIt = mStack.listIterator(mStack.size());
        List<Pair<String, Fragment>> upList = new ArrayList<>();
        Pair<String, Fragment> target = null;
        while (accurateIt.hasPrevious()) {
            Pair<String, Fragment> pair = accurateIt.previous();
            // 精准查找优先
            if (tag.equals(pair.first)) {
                target = pair;
                break;
            } else {
                upList.add(pair);
            }
        }
        if (target == null) {
            upList.clear();
            ListIterator<Pair<String, Fragment>> fuzzyIt = mStack.listIterator(mStack.size());
            while (fuzzyIt.hasPrevious()) {
                Pair<String, Fragment> pair = fuzzyIt.previous();
                // 再寻找前部匹配
                if (pair.first.startsWith(tag)) {
                    target = pair;
                    break;
                } else {
                    upList.add(pair);
                }
            }
        }
        if (target != null) {
            // 把目标加到最后一个
            upList.add(target);
            return upList;
        }
        return null;
    }

    public int getStackSize(){
        return mStack.size();
    }
}
