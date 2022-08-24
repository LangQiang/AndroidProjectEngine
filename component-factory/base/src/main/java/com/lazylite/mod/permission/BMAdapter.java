package com.lazylite.mod.permission;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;
import com.lazylite.mod.permission.core.ManagerPermissions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Android M(6.0) 以下版本使用。
 * <p/>
 * Created by lizhaofei on 2018/3/19 17:14
 */
public class BMAdapter implements ManagerPermissions.PlatformAdapter {
    private ManagerPermissions managerPermissions;

    public BMAdapter(ManagerPermissions managerPermissions) {
        this.managerPermissions = managerPermissions;
    }

    @Override
    public boolean requestPermission(final Object obj, final int requestCode, final String[] permissions) {
        //android.Manifest.permission.WRITE_SETTINGS  这个权限貌似不需要处理
        //fixme lzf 这里应该添加针对 android.Manifest.permission.WRITE_SETTINGS or android.Manifest.permission.SYSTEM_ALERT_WINDOW 处理，但是由于没有用到，所以暂不处理
        if (obj instanceof Activity) {
            ActivityCompat.requestPermissions((Activity)obj,permissions, requestCode);
        } else if (obj instanceof Fragment) {
            ((Fragment) obj).requestPermissions(permissions, requestCode);
        } else {
            throw new IllegalArgumentException(
                    null != obj ? obj.getClass().getName() : "null" + " is not supported");
        }
        return true;
    }

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
            if (permissions.length > 1) {
                throw new IllegalArgumentException(
                        "android.Manifest.permission.WRITE_SETTINGS or android.Manifest.permission.SYSTEM_ALERT_WINDOW must check Alone");
            }
            if (specialPermission.equals(android.Manifest.permission.WRITE_SETTINGS)) {
                //这个权限没有限制
            } else if (specialPermission.equals(android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!isAlertWindowEnabled(context)) {
                    denied.add(specialPermission);
                }
            }
        } else {
            for (String permission : permissions) {
                int result = ContextCompat.checkSelfPermission(context,
                        permission);//这种方式是正确的，ActivityCompat.requestPermissions((Activity)obj,permissions, requestCode); 源码中是错误的
                if (result != PackageManager.PERMISSION_GRANTED) {
                    denied.add(permission);
                }
            }
        }
        return denied;
    }

    @Override
    public boolean deniedAndNoShow(Activity activity, String permission) {
        final List<String> denied = checkSelfPermission(activity,new String[]{permission});
        return null != denied && denied.size()==1 && !ActivityCompat.shouldShowRequestPermissionRationale(activity,permission);
    }

    @Override
    public void postCallback(final Object obj, @Nullable final Callback callback, final IamUI iamUI,
                             final int requestCode, final String[] permissions, final int[] grantResults) {
        if (null == grantResults || grantResults.length == 0) {
            if (null != callback) {
                callback.onCancel(requestCode);
            }
        } else {
            final List<String> denied = new ArrayList<>();//被拒绝的权限（包括 不再提示的）

            final int requestLength = grantResults.length;
            for (int i = 0; i < requestLength; i++) {
                //不能够直接根据结果来，因为低于Android M 版本的兼容包中检测是否有权限的判断有问题（SDK bug）
                //if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//拒绝
                //    final String permissionStr = permissions[i];
                //    denied.add(permissionStr);
                //}
                //这里再次判断下
                if(checkSelfPermission(getActivity(obj),new String[]{permissions[i]}).size() != 0){//拒绝
                    final String permissionStr = permissions[i];
                    denied.add(permissionStr);
                }
            }
            if (denied.size() == 0) {
                if (null != callback) {
                    callback.onSuccess(requestCode);
                }
            } else {//只要有一项拒绝，那么直接去 设置页 即可，因为这个版本下没有让android系统打开权限的方法
                iamUI.showGoSettingsTip(denied.toArray(new String[0]), new IamUI.OnClickCancel() {
                    @Override
                    public void onClick() {//取消
                        if (null != callback) {
                            callback.onFail(requestCode, permissions, grantResults);
                        }
                    }
                }, new IamUI.OnClickOk() {//去设置页
                    @Override
                    public void onClick() {
                        managerPermissions.startSettingsForResult(obj, requestCode, permissions,
                                callback, iamUI);
                    }
                });
            }
        }
    }

    private Activity getActivity(Object obj) {
        if (obj instanceof Fragment) {
            return ((Fragment) obj).getActivity();
        } else if (obj instanceof Activity) {
            return (Activity) obj;
        }
        return null;
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_SYSTEM_ALERT_WINDOW = "OP_SYSTEM_ALERT_WINDOW";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isAlertWindowEnabled(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opAlertWindowValue = appOpsClass.getDeclaredField(OP_SYSTEM_ALERT_WINDOW);
            int value = (int) opAlertWindowValue.get(Integer.class);
            //返回 0 就代表有权限，1代表没有权限，-1函数出错啦
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
