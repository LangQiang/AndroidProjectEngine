package com.lazylite.mod.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;
import com.lazylite.mod.permission.core.ManagerPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Android M(6.0)及以上版本使用。
 * <p/>
 * Created by lizhaofei on 2018/3/19 17:13
 */
public class MAdapter implements ManagerPermissions.PlatformAdapter {
    private final ManagerPermissions managerPermissions;

    MAdapter(ManagerPermissions managerPermissions) {
        this.managerPermissions = managerPermissions;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean requestPermission(Object obj, int requestCode, String[] permissions) {
        //android M 中，对于 android.Manifest.permission.WRITE_SETTINGS 和 android.Manifest.permission.SYSTEM_ALERT_WINDOW 需要特殊关照
        boolean special = false;
        String specialPermission="";
        for (String one : permissions) {
            if (one.equals(android.Manifest.permission.WRITE_SETTINGS) || one.equals(
                    android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                special = true;
                specialPermission = one;
                break;
            }
        }
        if (special) {
            if (permissions.length > 1) {
                throw new IllegalArgumentException(
                        "android.Manifest.permission.WRITE_SETTINGS or android.Manifest.permission.SYSTEM_ALERT_WINDOW must request Alone");
            } else {
                final Context context = getActivity(obj);
                Intent intent;
                if (specialPermission.equals(android.Manifest.permission.WRITE_SETTINGS)) {
                    intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                } else {
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                }
                //注意下面的requestCode，这里设置为 Permission.REQUEST_PERMISSION_SETTING 是为了让工具监听到onActivityResult()
                if (obj instanceof Activity) {
                    ((Activity) obj).startActivityForResult(intent, Permission.REQUEST_PERMISSION_SETTING);
                } else if (obj instanceof Fragment) {
                    ((Fragment) obj).startActivityForResult(intent, Permission.REQUEST_PERMISSION_SETTING);
                }
            }
        } else {
            if (obj instanceof Activity) {
                ((Activity) obj).requestPermissions(permissions, requestCode);
            } else if (obj instanceof Fragment) {
                ((Fragment) obj).requestPermissions(permissions, requestCode);
            } else {
                throw new IllegalArgumentException(
                        null != obj ? obj.getClass().getName() : "null" + " is not supported");
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public List<String> checkSelfPermission(Context context, String[] permissions) {
        final List<String> denied = new ArrayList<>();
        if (null == context) {
            return denied;
        }

        boolean special = false;
        String specialPermission = "";
        for (String one : permissions) {
            if (one.equals(android.Manifest.permission.WRITE_SETTINGS) || one.equals(
                    android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                special = true;
                specialPermission = one;
                break;
            }
        }
        if (special) {
            if(permissions.length > 1){
                throw new IllegalArgumentException(
                        "android.Manifest.permission.WRITE_SETTINGS or android.Manifest.permission.SYSTEM_ALERT_WINDOW must check Alone");
            }
            if (specialPermission.equals(android.Manifest.permission.WRITE_SETTINGS)) {
                if (!Settings.System.canWrite(context)) {
                    denied.add(specialPermission);
                }
            } else if (specialPermission.equals(android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(context)){
                    denied.add(specialPermission);
                }
            }
        } else {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    denied.add(permission);
                }
            }
        }
        return denied;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean deniedAndNoShow(Activity activity, String permission) {
        final List<String> denied = checkSelfPermission(activity,new String[]{permission});
        return null != denied && denied.size()==1 && !activity.shouldShowRequestPermissionRationale(permission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void postCallback(final Object obj, @Nullable final Callback callback, final IamUI iamUI, final int requestCode,final String[] permissions, final int[] grantResults) {
        if (grantResults.length > 0) {
            final List<String> deniedAndNoShow = new ArrayList<>();//被拒绝并且不再提示
            final List<String> denied = new ArrayList<>();//被拒绝的权限（包括 不再提示的）

            final Activity activity = getActivity(obj);
            final int requestLength = grantResults.length;
            for (int i = 0; i < requestLength; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//拒绝
                    final String permissionStr = permissions[i];
                    denied.add(permissionStr);
                    if (null != activity && !activity.shouldShowRequestPermissionRationale(
                            permissionStr)) {//不再提示
                        deniedAndNoShow.add(permissionStr);
                    }
                }
            }
            final int deniedAndNoShowSize = deniedAndNoShow.size();

            if (denied.size() == 0) {//全部授权
                if (null != callback) {
                    callback.onSuccess(requestCode);
                }
            } else {//没有全部授权
                if (deniedAndNoShowSize > 0) {//有【拒绝不再提示】状态 提示用户去设置页打开【所有拒绝的权限】的弹窗
                    iamUI.showGoSettingsTip(denied.toArray(new String[0]), new IamUI.OnClickCancel() {
                        @Override
                        public void onClick() {
                            if(null != callback){
                                callback.onFail(requestCode, permissions, grantResults);
                            }
                        }
                    }, new IamUI.OnClickOk() {
                        @Override
                        public void onClick() {
                            managerPermissions.startSettingsForResult(obj, requestCode, permissions,
                                    callback, iamUI);
                        }
                    });
                } else {
                    if(null != callback){
                        callback.onFail(requestCode, permissions, grantResults);
                    }
                }
            }
        } else {//empty 这里是系统取消
            if(null != callback) {
                callback.onCancel(requestCode);
            }
        }
    }

    private Activity getActivity(Object obj) {
        if (obj instanceof Activity) {
            return (Activity) obj;
        } else if (obj instanceof Fragment) {
            return ((Fragment) obj).getActivity();
        }
        return null;
    }
}
