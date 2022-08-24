package com.lazylite.mod.permission;

import android.app.Activity;
import android.content.Context;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;
import com.lazylite.mod.permission.core.ManagerPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有权限全部是拥有的
 * <p/>
 * Created by lizhaofei on 2018/3/27 10:12
 */
public class EmptyAdapter implements ManagerPermissions.PlatformAdapter{
    @Override
    public boolean requestPermission(Object obj, int requestCode, String[] permissions) {
        return false;
    }

    //只要这个方法返回这个，那么其它方法都不会走，会直接回调接口已经拥有权限了
    @Override
    public List<String> checkSelfPermission(Context context, String[] permissions) {
        return new ArrayList<>(0);
    }

    @Override
    public boolean deniedAndNoShow(Activity activity, String permission) {
        return false;
    }

    @Override
    public void postCallback(Object obj, Callback callback, IamUI iamUI, int requestCode,String[] permissions, int[] grantResults) {
        callback.onSuccess(requestCode);
    }
}
