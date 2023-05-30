package com.lazylite.mod.fragmentmgr;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

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

    private OnFragmentStackChangeListenerProxy mListenerProxy;
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

    public void bind(FragmentActivity activity,boolean clearStack, IHostActivity optFunInterface, OnFragmentStackChangeListener onFragmentStackChangeListener) {
        mBindActivity = activity;
        mFragmentManager = activity.getSupportFragmentManager();
        mDefaultParameter = getDefaultParameter();
        mHostActivityOpt = optFunInterface;
        mListenerProxy = new OnFragmentStackChangeListenerProxy(onFragmentStackChangeListener,optFunInterface,mFragmentManager);
        if (clearStack){
            mStack.clear();
        }
    }

    //在未bind前是null
    @Nullable
    public Activity getBindActivity(){
        return mBindActivity;
    }

    public IHostActivity getHostActivityOpt(){
        return mHostActivityOpt;
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
        mListenerProxy = null;
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
            transaction.setMaxLifecycle(pair.second, Lifecycle.State.STARTED);
            transaction.remove(pair.second);
        }
        final IHostActivity hostActivity = mHostActivityOpt;
        if (null != hostActivity){
            final Fragment hostActivityTopFragment = hostActivity.onGetMainLayerTopFragment();
            if (null != hostActivityTopFragment && hostActivityTopFragment.isAdded()) {
                transaction.setMaxLifecycle(hostActivityTopFragment, Lifecycle.State.RESUMED);
            }
        }
        transaction.commitAllowingStateLoss();
        mStack.clear();
        mTagAtomic.set(0);
        if (mListenerProxy != null) {
            mListenerProxy.onPopFragment(getTopFragment());
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
            transaction.setMaxLifecycle(pair.second, Lifecycle.State.STARTED);
            transaction.remove(pair.second);
            mStack.remove(pair);
        }
        if (includeTarget) {
            int targetIndex = mStack.indexOf(target);
            if (targetIndex - 1 < 0) {
                transaction.setMaxLifecycle(target.second, Lifecycle.State.STARTED);
                transaction.remove(target.second);
                //
                final IHostActivity hostActivity = mHostActivityOpt;
                if (null != hostActivity){
                    final Fragment hostActivityTopFragment = hostActivity.onGetMainLayerTopFragment();
                    if (null != hostActivityTopFragment && hostActivityTopFragment.isAdded()){
                        transaction.setMaxLifecycle(hostActivityTopFragment, Lifecycle.State.RESUMED);
                    }
                }
                transaction.commitAllowingStateLoss();
                mStack.remove(target);
                if (mListenerProxy != null) {
                    mListenerProxy.onPopFragment(getTopFragment());
                }
                return;
            } else {
                showFragment = mStack.get(targetIndex - 1).second;
                transaction.setMaxLifecycle(showFragment, Lifecycle.State.RESUMED);
                transaction
                        .setMaxLifecycle(target.second, Lifecycle.State.STARTED)
                        .show(showFragment)
                        .remove(target.second)
                        .commitNowAllowingStateLoss();
                mStack.remove(target);
                safeShowFragmentView(showFragment);
                //showFragment.onResume();

            }
        } else {
            showFragment = target.second;
            transaction.setMaxLifecycle(showFragment, Lifecycle.State.RESUMED);
            transaction.show(showFragment).commitNowAllowingStateLoss();
            safeShowFragmentView(showFragment);
            //showFragment.onResume();
        }
        // 移除了n个之后（n>=1）
        if (mListenerProxy != null) {
            mListenerProxy.onPopFragment(getTopFragment());
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
                final Fragment topFragment = getTopFragment();
                transaction.setMaxLifecycle(topFragment, Lifecycle.State.STARTED);
                transaction.remove(topFragment);
                final IHostActivity hostActivity = mHostActivityOpt;
                if (null != hostActivity){
                    final Fragment hostActivityTopFragment = hostActivity.onGetMainLayerTopFragment();
                    if (null != hostActivityTopFragment && hostActivityTopFragment.isAdded()){
                        transaction.setMaxLifecycle(hostActivityTopFragment, Lifecycle.State.RESUMED);
                    }
                }
                transaction.commitNowAllowingStateLoss();
                mStack.removeLast();
                if (mListenerProxy != null) {
                    mListenerProxy.onPopFragment(getTopFragment());
                }
                return true;
            } else {
                Fragment showFragment = mStack.get(mStack.size() - 2).second;
                LogMgr.d(TAG,"close Fragment 【"
                        + getTopFragment().getClass().getName()
                        + "】，and show pre Fragment:"
                        + showFragment.getClass().getName());
                transaction
                        .setMaxLifecycle(getTopFragment(), Lifecycle.State.STARTED)
                        .show(showFragment)
                        .remove(getTopFragment())
                        .setMaxLifecycle(showFragment, Lifecycle.State.RESUMED)
                        .commitNowAllowingStateLoss();//这种方式，会让操作的Fragment走完其生命周期，
                // 例如：remove掉了 getTopFragment()，如果在 topFragment 的 onDestroyView() 方法
                // 调用了 FragmentOperation 的打开或者remove方法，都会对 FragmentOperation 造成不可预知的影响，甚至出现crash！
                mStack.removeLast();
                safeShowFragmentView(showFragment);
                //showFragment.onResume();
                if (mListenerProxy != null) {
                    mListenerProxy.onPopFragment(getTopFragment());
                }
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取top fragment，如果栈中为空，获取{@link IHostActivity#onGetMainLayerTopFragment()}
     */
    @Nullable
    public Fragment getTopFragment() {
        if (!mStack.isEmpty()) {
            return mStack.getLast().second;
        } else {
            // viewpager
            if (null == mHostActivityOpt) {
                return null;
            }
            return mHostActivityOpt.onGetMainLayerTopFragment();
        }
    }

    @Nullable
    public IFragment getTopContentFragment(){
        final Fragment fragment = getTopFragment();
        if (fragment instanceof IFragment){
            return findTopContentIFragment((IFragment) fragment);
        }
        return null;
    }

    public String getTopFragmentClassName(){
        final OnFragmentStackChangeListenerProxy listenerProxy = mListenerProxy;
        if (null == listenerProxy){
            return "OnFragmentStackChangeListenerProxy:Null";
        }
        return listenerProxy.nowTopFragmentName;
    }

    /**
     * 获取前一个fragment，不要滥用，目前就在左滑退出做显示隐藏使用
     */
    public Fragment getPreFragment() {
        if (mStack.isEmpty() || mStack.size() == 1) {
            // viewpager
            return null;
        } else {
            return mStack.get(mStack.size() - 2).second;
        }
    }

    //显示或者隐藏前一个Fragment，只会触发onHideChange()，不会触发其它生命周期方法。
    public void showPreFragment(boolean show){
        final Fragment preFragment = getPreFragment();
        if (null == preFragment) {
            return;
        }
        if (null == mFragmentManager) {
            return;
        }
        final boolean preIsHide = preFragment.isHidden();
        if (show) {
            if (preIsHide){
                mFragmentManager.beginTransaction().show(preFragment).commitNowAllowingStateLoss();
            }
        } else {
            if (!preIsHide){
                mFragmentManager.beginTransaction().hide(preFragment).commitNowAllowingStateLoss();
            }
        }
    }

    //
    public void showHostActivityLayer(boolean show){
        final IHostActivity hostActivity = mHostActivityOpt;
        if (null != hostActivity){
            hostActivity.onShowMainLayer(show);
        }
    }

    /**
     * 是否是在主页
     */
    public boolean isMainLayerShow() {
        return mStack.size() == 0;
    }

    /**
     * 精准查找你指定的tag的fragment
     *
     * @param tag
     */
    public Fragment findFragmentByTag(String tag) {
        final Pair<String,Fragment> pair = internalFindFragmentByTag(tag);
        if (null != pair){
            return pair.second;
        }
        return null;
    }

    private Pair<String,Fragment> internalFindFragmentByTag(String tag){
        for (int i = 0, size = mStack.size(); i < size; i++) {
            Pair<String, Fragment> pair = mStack.get(i);
            if (pair.first.equals(tag)) {
                return pair;
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
            if (TextUtils.isEmpty(tag)){
                tag = createDefaultTag(fragment);
            }
        } else {
            tag = createDefaultTag(fragment);
        }
        //
        if (mStack.isEmpty()) {
            openStandard(fragment, tag, parameter);
        } else {
            handlerStartMode(fragment, tag, parameter);
        }
        if (mListenerProxy != null) {
            mListenerProxy.onPushFragment(fragment,parameter.copy());
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
                transaction.setMaxLifecycle(pair.second, Lifecycle.State.STARTED);
                transaction.remove(pair.second);
                mStack.remove(pair);
            }
            safeSetMaxLifecycle(target,transaction,Lifecycle.State.RESUMED);
            //transaction.setMaxLifecycle(target, Lifecycle.State.RESUMED);
            transaction.show(target).commitNowAllowingStateLoss();
            ((IFragment) target).onNewIntent(parameter.bundle);
            safeShowFragmentView(target);
            //target.onResume();
            return;
        }

        openStandard(fragment, tag, parameter);
    }

    //走到这里，mStack一定不为空，参见最终 showFragment() 方法中的逻辑
    /**
     * SingleInstance
     * 模拟单例实例，比方播放页
     *
     * @param fragment  目标Fragment
     * @param tag       目标Fragment tag
     * @param parameter 跳转参数
     */
    private void openSingleInstance(Fragment fragment, String tag, StartParameter parameter) {
        if (mFragmentManager == null) {
            return;
        }
        final int containerId = parameter.containerId != -1 ? parameter.containerId : mHostActivityOpt.containerViewId();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        handlerAnimation(fragment, parameter, transaction);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);

        Fragment preFragment = mStack.getLast().second;
        if (!TextUtils.isEmpty(parameter.popEndTag)) {
            List<Pair<String, Fragment>> findArrary = findTargetFragmentAndUpList(parameter.popEndTag);
            if (findArrary != null && !findArrary.isEmpty()) {
                Pair<String, Fragment> target = findArrary.get(findArrary.size() - 1);
                for (Pair<String, Fragment> pair : findArrary) {
                    if (!parameter.isIncludePopEnd && pair == target) {
                        continue;
                    }
                    transaction.setMaxLifecycle(pair.second, Lifecycle.State.STARTED);
                    transaction.remove(pair.second);
                    mStack.remove(pair);
                }
            }
        } else if(!TextUtils.isEmpty(parameter.removeTags)){
            final Pair<String, Fragment> forRemove = internalFindFragmentByTag(parameter.removeTags);
            if (null != forRemove){
                transaction.setMaxLifecycle(forRemove.second, Lifecycle.State.STARTED);
                transaction.remove(forRemove.second);
            }
            mStack.remove(forRemove);
        } else if (parameter.isPopCurrent) {
            transaction.setMaxLifecycle(preFragment, Lifecycle.State.STARTED);
            transaction.remove(preFragment);
            mStack.remove(mStack.getLast());
        } else {
            // isHideBottomLayer && 没入场动画 才能隐藏下层
            if (parameter.isHideBottomLayer && parameter.enterAnimation == 0) {
                transaction.hide(preFragment);
            }
            transaction.setMaxLifecycle(preFragment, Lifecycle.State.STARTED);// 只要前面不pop掉pre就得触发其onPause
            //preFragment.onPause();
        }

        // 把存在的实例都干掉,如果有的话
        ListIterator<Pair<String, Fragment>> it = mStack.listIterator();
        while (it.hasNext()) {
            Pair<String, Fragment> element = it.next();
            if (tag.startsWith(getRealTag(element.first))) {
                transaction.setMaxLifecycle(element.second, Lifecycle.State.STARTED);
                transaction.remove(element.second);
                it.remove();
            }
        }
        transaction.add(containerId, fragment, tag);
        transaction.commitAllowingStateLoss();
        mStack.add(new Pair<>(tag, fragment));
    }

    //当 mStack 为空时，无论何种打开方式，都会走到这里；当 mStack 不为空时，如果是标准模式打开，才会走到这里。
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
        final int containerId = parameter.containerId != -1 ? parameter.containerId : mHostActivityOpt.containerViewId();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        handlerAnimation(fragment, parameter, transaction);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        if (mStack.isEmpty()) {
            /*if (mListener != null) {
                mListener.onHideMainLayer(parameter.isHideBottomLayer
                        && parameter.enterAnimation == 0);
            }*/
            final IHostActivity hostActivity = mHostActivityOpt;
            if (null != hostActivity){
                final Fragment hostActivityTopFragment = hostActivity.onGetMainLayerTopFragment();
                if (null != hostActivityTopFragment && hostActivityTopFragment.isAdded()){
                    transaction.setMaxLifecycle(hostActivityTopFragment, Lifecycle.State.STARTED);
                }
            }
            transaction.add(containerId, fragment, tag);
            transaction.commitAllowingStateLoss();
            mStack.add(new Pair<>(tag, fragment));
            return;
        }

        Fragment preFragment = mStack.getLast().second;
        if (!TextUtils.isEmpty(parameter.popEndTag)) {
            List<Pair<String, Fragment>> findArrary
                    = findTargetFragmentAndUpList(parameter.popEndTag);
            if (findArrary != null && !findArrary.isEmpty()) {
                Pair<String, Fragment> target = findArrary.get(findArrary.size() - 1);
                for (Pair<String, Fragment> pair : findArrary) {
                    if (!parameter.isIncludePopEnd && pair == target) {
                        continue;
                    }
                    transaction.setMaxLifecycle(pair.second, Lifecycle.State.STARTED);
                    transaction.remove(pair.second);
                    mStack.remove(pair);
                }
            }
        } else if(!TextUtils.isEmpty(parameter.removeTags)){
            final Pair<String, Fragment> forRemove = internalFindFragmentByTag(parameter.removeTags);
            if (null != forRemove){
                transaction.setMaxLifecycle(forRemove.second, Lifecycle.State.STARTED);
                transaction.remove(forRemove.second);
            }
            mStack.remove(forRemove);
        } else if (parameter.isPopCurrent) {
            transaction.setMaxLifecycle(preFragment, Lifecycle.State.STARTED);
            transaction.remove(preFragment);
            mStack.remove(mStack.getLast());
        } else {
            // isHideBottomLayer && 没入场动画 才能隐藏下层
            if (parameter.isHideBottomLayer && parameter.enterAnimation == 0) {
                transaction.hide(preFragment);
            }
            transaction.setMaxLifecycle(preFragment, Lifecycle.State.STARTED); // 只要前面不pop掉pre就得触发其onPause
            //preFragment.onPause();
        }
        transaction.add(containerId, fragment, tag);
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
        return fragment instanceof IFragment
                && ((IFragment) fragment).onKeyDown(keyCode, event);
    }

    /**
     * Debug 方法：
     * 获取fragment栈队列
     */
    public List<DebugFragmentStack> getStack() {
        if (mStack.size() == 0) {
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
        if (fragmentList.isEmpty()) {
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
                transaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
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

    @Nullable
    private IFragment findTopContentIFragment(final IFragment iFragment){
        if (null == iFragment){
            return null;
        }
        final IFragment topContentIFragment = iFragment.topContentFragment();
        if (topContentIFragment == iFragment){
            return topContentIFragment;
        }
        return findTopContentIFragment(topContentIFragment);
    }

    private String createDefaultTag(Fragment fragment){
        return fragment.getClass().getName()
                + SEPARATOR
                + mTagAtomic.incrementAndGet();
    }

    public int getStackSize(){
        return mStack.size();
    }

    //todo lzf 所有设置FragmentLifecycle的地方都调用此方法
    private void safeSetMaxLifecycle(@NonNull Fragment fragment,@NonNull FragmentTransaction fragmentTransaction,@NonNull Lifecycle.State state){
        if (fragment.isAdded() && fragment.getParentFragmentManager() == mFragmentManager){
            fragmentTransaction.setMaxLifecycle(fragment,state);
        }
    }

    //FragmentOperation 会管理自身Fragment变化时的生命周期，此处是额外管理了HostActivity中contentFragment生命周期，
    // 由于是后置调用，所以会先执行showFragment的onResume()再执行HostActivity的contentFragment的onPause()。
    //此处生命周期管理，不会让被覆盖的Fragment的onStart()/onStop()方法执行，只是管理到了onResume()/onPause()的执行。
    private static class OnFragmentStackChangeListenerProxy implements OnFragmentStackChangeListener{
        @Nullable
        private final OnFragmentStackChangeListener listener;
        @NonNull
        private final IHostActivity iHostActivity;
        @NonNull
        private final FragmentManager fragmentManager;
        @NonNull
        public String nowTopFragmentName = "";

        private OnFragmentStackChangeListenerProxy(@Nullable OnFragmentStackChangeListener listener,@NonNull IHostActivity iHostActivity,@NonNull FragmentManager fragmentManager){
            this.listener = listener;
            this.iHostActivity = iHostActivity;
            this.fragmentManager = fragmentManager;

            final Fragment mainActivityTopFragment = iHostActivity.onGetMainLayerTopFragment();
            if (null != mainActivityTopFragment){
                nowTopFragmentName = mainActivityTopFragment.getClass().getName();
            }
        }

        @Override
        public void onPushFragment(Fragment top, StartParameter startParameter) {
            nowTopFragmentName = null != top?top.getClass().getName():"onPushFragment():topFragment:Null";
            final boolean showBomLayer = null == startParameter || (startParameter.enterAnimation != 0 || !startParameter.isHideBottomLayer);//有动画，不要隐藏
            if (FragmentOperation.getInstance().getStackSize()==1){
                iHostActivity.onShowMainLayer(showBomLayer);
                /*final Fragment hostActivityCurContentFragment = iHostActivity.onGetMainLayerTopFragment();
                if (null != hostActivityCurContentFragment && hostActivityCurContentFragment.isAdded()){
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setMaxLifecycle(hostActivityCurContentFragment, Lifecycle.State.STARTED);
                    transaction.commitAllowingStateLoss();
                }*/
            }
            //
            if (null != listener){
                listener.onPushFragment(top,startParameter);
            }
        }

        @Override
        public void onPopFragment(@Nullable final Fragment nowTop) {
            if (FragmentOperation.getInstance().getStackSize()==0){
                final Fragment mainActivityTopFragment = iHostActivity.onGetMainLayerTopFragment();
                if (null != mainActivityTopFragment) {
                    nowTopFragmentName = mainActivityTopFragment.getClass().getName();
                } else {
                    nowTopFragmentName = "onPopFragment():size==0:mainActivityTopFragment:Null";
                }

                iHostActivity.onShowMainLayer(true);
                /*final Fragment hostActivityCurContentFragment = iHostActivity.onGetMainLayerTopFragment();
                if (null != hostActivityCurContentFragment && hostActivityCurContentFragment.isAdded()){
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // setMaxLifecycle()事务会先于 remove()/add() 事务执行。所以在FragmentOperation中，打开新Fragment时生命周期没问题：
                    // 上一个先onPause()，新的才onResume()；但是在FragmentOperation中执行close()时，就有点问题了：
                    // 要显示的Fragment会先执行onResume()，remove掉的Fragment才会执行onPause()。
                    transaction.setMaxLifecycle(hostActivityCurContentFragment, Lifecycle.State.RESUMED);

                    //如果Fragment状态没有到达onResume()(没有执行onResume()方法),
                    // 那么会从Fragment的当前状态依次执行到onResume()方法。
                    //Fragment包含的子Fragment也会同步到此状态。
                    transaction.commitAllowingStateLoss();
                    //Caused by: java.lang.IllegalStateException: FragmentManager is already executing transactions
                    //此方法会有上面的crash
                    //transaction.commitNowAllowingStateLoss();
                }*/
            } else {
                nowTopFragmentName = null != nowTop?nowTop.getClass().getName():"onPopFragment():size!=0:Null";
            }
            //
            if (null != listener){
                listener.onPopFragment(nowTop);
            }
        }
    }//
}
