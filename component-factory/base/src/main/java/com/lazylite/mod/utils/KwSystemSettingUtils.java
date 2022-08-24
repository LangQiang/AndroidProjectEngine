package com.lazylite.mod.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.basemodule.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 冯小保
 * on 2016/11/1 0001.
 */
public class KwSystemSettingUtils {
    public static boolean isBlack;

    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final String OP_SYSTEM_ALERT_WINDOW = "OP_SYSTEM_ALERT_WINDOW";

    /**
     * 说是用来判断无权限的弹框能不能弹的，有权限也就是true的时候不弹框，false才弹框
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {
        if (context == null) {//不用弹框
            return true;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager n = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                return n.areNotificationsEnabled();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Class appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                //返回 0 就代表有权限，1代表没有权限，-1函数出错啦
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void openNotifySettingPage(Context context) {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 26) {
                // android 8.0引导
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            } else if (Build.VERSION.SDK_INT >= 21) {
                // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else {
                // 其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                // 出现异常则跳转到应用设置界面：锤子
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ignore) {

            }
        }
    }

    public static boolean isAlertWindowEnabled(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
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

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param context
     * @param packageName 应用程序的包名
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(uri);
        } else {
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaWeiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMIUIPermission(context, context.getPackageName());
        }

    }

    /**
     * 跳转到miui的权限管理页面
     */
    public static void gotoMIUIPermission(Context context, String packageName) {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditor");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", packageName);
        try {
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            showInstalledAppDetails(context, context.getPackageName());
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    public static void jumpSystemPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    public static void gotoSystemNotifiPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaWeiPermission(context);
        }
    }

    public static void resetStatusBarisBlack(Activity activity, boolean isBlack){
        if (activity == null) {
            return;
        }
        if (isBlack){
            resetStatusBarBlack(activity);
        }else {
            resetStatusBarWhite(activity);
        }
    }

    public static void resetStatusBarWhite(Activity activity) {
        if (activity == null) {
            return;
        }
        if (!isBlack) {
            return;
        }
        isBlack = false;
//        // Flyme4不管是Android6.0以上还是以下||Flyme6 Android6.0以下都按Flyme官方的辣鸡办法适配
        if ((!DeviceInfo.isFlyme6Above() && DeviceInfo.isFlyme4Above())
                || (DeviceInfo.isFlyme6Above() && !DeviceInfo.isAndroidMOrAbove())) {
            setflyme(activity, false);
            return;
        }
        // miui6不管是Android6.0以上还是以下||miui9 Android6.0以下都按miui官方的辣鸡办法适配
        if ((!DeviceInfo.isMiUi9Above() && DeviceInfo.isMiUi6Above())
                || (DeviceInfo.isMiUi9Above() && !DeviceInfo.isAndroidMOrAbove())) {
            setMiui(activity, false);
            return;
        }
        // 按google的原生适配
        if (DeviceInfo.isAndroidMOrAbove()) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            return;
        }
        if (!DeviceInfo.isFlyme4Above()
                && !DeviceInfo.isMiUi6Above()
                && !DeviceInfo.isMiUi9Above()
                && !DeviceInfo.isAndroidMOrAbove()) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.kw_common_cl_transparent);
        }
    }

    public static void resetStatusBarBlack(Activity activity) {
        if (activity == null) {
            return;
        }
        if (isBlack) {
            return;
        }
        isBlack = true;
//        // Flyme4不管是Android6.0以上还是以下||Flyme6 Android6.0以下都按Flyme官方的辣鸡办法适配
        if ((!DeviceInfo.isFlyme6Above() && DeviceInfo.isFlyme4Above())
                || (DeviceInfo.isFlyme6Above() && !DeviceInfo.isAndroidMOrAbove())) {
            setflyme(activity, true);
            return;
        }
        // miui6不管是Android6.0以上还是以下||miui9 Android6.0以下都按miui官方的辣鸡办法适配
        if ((!DeviceInfo.isMiUi9Above() && DeviceInfo.isMiUi6Above())
                || (DeviceInfo.isMiUi9Above() && !DeviceInfo.isAndroidMOrAbove())) {
            setMiui(activity, true);
            return;
        }
        // 按google的原生适配
        if (DeviceInfo.isAndroidMOrAbove()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        }
        if (!DeviceInfo.isFlyme4Above()
                && !DeviceInfo.isMiUi6Above()
                && !DeviceInfo.isMiUi9Above()
                && !DeviceInfo.isAndroidMOrAbove()) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.kw_common_cl_black);
        }
    }

    //public static void resetNavigationBarWhite(Activity activity){
    //    // 按google的原生适配
    //    if (DeviceInfo.isAndroidMOrAbove) {
    //        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //                | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    //        return;
    //    }
    //    SystemBarTintManager tintManager = new SystemBarTintManager(activity);
    //    tintManager.setNavigationBarTintEnabled(true);
    //    tintManager.setNavigationBarTintColor(Color.parseColor("#ffffff"));
    //}
    //
    //public static void resetNavigationBarBlack(Activity activity){
    //    // 按google的原生适配
    //    //if (DeviceInfo.isAndroidMOrAbove) {
    //    //    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //    //            | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    //    //    return;
    //    //}
    //    SystemBarTintManager tintManager = new SystemBarTintManager(activity);
    //    tintManager.setNavigationBarTintEnabled(true);
    //    tintManager.setNavigationBarTintColor(Color.parseColor("#000000"));
    //}

    /**
     * 时间紧张, 先不加, 随后再加进来对miui, flyme的特别处理
     */
    private static void setMiui(Activity activity, boolean darktext) {
        try {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darktext ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setflyme(Activity activity, boolean darktext) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darktext) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
