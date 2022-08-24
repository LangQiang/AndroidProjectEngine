package com.lazylite.mod.utils;


import androidx.annotation.Nullable;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;

/**
 * 酷我默认权限弹窗样式。
 * <p/>
 * Created by lizhaofei on 2018/3/23 16:26
 * 初始化不弹权限弹窗
 */
public class InitPermissionUI implements IamUI {

    @Override
    public void showRequestPermissionTip(String[] permissions, final OnClickCancel onClickCancel,
                                         final OnClickOk onClickOk) {
        if (onClickOk != null) {
            onClickOk.onClick();
        }
    }

    @Override
    public void showGoSettingsTip(String[] permissions, OnClickCancel onClickCancel,
                                  OnClickOk onClickOk) {
        if (onClickCancel != null) {
            onClickCancel.onClick();
        }
        // do nothing
    }

    @Override
    public boolean onSettingsBack(@Nullable String[] permissions, int requestCode, @Nullable Callback callback) {
        return false;
    }
}
