package com.lazylite.mod.permission.core;

import androidx.annotation.Nullable;

/**
 * 提供每次权限请求的自定义显示。
 * <p/>
 * Created by lizhaofei on 2018/3/22 14:44
 */
public interface IamUI {

    /** 点击了提示中的 “去获取权限” */
    interface OnClickOk {
        void onClick();
    }

    /** 点击了提示中的 “取消” */
    interface OnClickCancel {
        void onClick();
    }

    /**
     * 显示授权弹框
     *
     * @param permissions 要申请的权限
     * @param onClickCancel 用户点击弹框中【取消】时的回调
     * @param onClickOk 用户点击弹窗中【去授权】的回调
     */
    void showRequestPermissionTip(String[] permissions, OnClickCancel onClickCancel, OnClickOk onClickOk);

    /**
     * 显示让用户去设置页打开权限的弹窗
     *
     * @param permissions 需要在设置页打开的权限
     * @param onClickCancel 用户点击弹框中【取消】时的回调
     * @param onClickOk 用户点击弹窗中【去授权】的回调
     */
    void showGoSettingsTip(String[] permissions, OnClickCancel onClickCancel, OnClickOk onClickOk);

    /**
     * 从设置页返回
     *
     * @param permissions 从设置页返回仍然被拒绝的权限，如果为null或者length==0 表示权限全部通过了
     * @return true自己处理；false不处理，来让工具自己处理（工具会自动去重新申请权限）
     */
    boolean onSettingsBack(@Nullable String[] permissions, int requestCode, @Nullable Callback callback);
}
