package com.lazylite.mod.permission;


import com.lazylite.mod.permission.core.Callback;

/**
 * 缩放一下{@link Callback}
 * <p/>
 * Created by lizhaofei on 2018/3/14 11:47
 */
public abstract class SimpleCallback implements Callback {

    @Override
    public void onError(String msg) {
        //不用处理，当Activity/Fragment存在同时多次请求权限时会回调这个
    }

    @Override
    public void onCancel(int requestCode) {
        //注意，这个是系统调用，一般不需要处理
    }
}