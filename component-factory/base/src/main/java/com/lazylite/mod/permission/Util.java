package com.lazylite.mod.permission;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;
import com.lazylite.mod.permission.core.SysSettingProvider;

import java.util.List;

/**
 * Created by lizhaofei on 2018/3/21 18:13
 */
public class Util {

    //获取默认 UI 弹窗
    public static IamUI createDefaultIamUI(final Context context) {
        return new DefaultIamUI(context);
    }

    /**
     * 根据 权限字符串 permissions 和 已经拒绝的权限字符串 deniedPermission 生成权限结果码。
     *
     * @param permissions 要申请的权限
     * @param deniedPermission 已经拒绝的权限
     */
    public static int[] createResultCode(String[] permissions, List<String> deniedPermission) {
        int[] result = new int[permissions.length];
        for (int i = 0, length = permissions.length; i < length; i++) {
            String one = permissions[i];
            boolean isDenied = false;
            for (int j = 0, jLength = deniedPermission.size(); j < jLength; j++) {
                if (one.equals(deniedPermission.get(j))) {
                    isDenied = true;
                    break;
                }
            }
            if (isDenied) {
                result[i] = PackageManager.PERMISSION_DENIED;
            } else {
                result[i] = PackageManager.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    //类似 断言
    public static void check(boolean value) {
//        KwDebug.classicAssert(value);
    }

//    public static CharSequence createRequestMsgForInit(String[] permissions) {
//        StringBuilder messageBuild = new StringBuilder("<html>酷我音乐需要获取");
//        List<String> text = TransformText.transformText(permissions);
//        for (int i = 0, size = text.size(); i < size; i++) {
//            messageBuild.append("<font color=\"#ff6600\">（").append(text.get(i)).append("）</font>");
//            if (size > 1) {
//                if (i == size - 2) {
//                    messageBuild.append("和");
//                } else if (i != size - 1) {
//                    messageBuild.append("，");
//                }
//            }
//        }
//        messageBuild.append("权限，以保证歌曲正常播放下载。请在之后的权限申请弹窗上放心选择【允许】。");
//        messageBuild.append("</html>");
//        return Html.fromHtml(messageBuild.toString());
//    }
//
//    public static CharSequence createGoSettingMsgForInit(String[] initPermissions, String[] dePermissions) {
//        boolean hasDevicePermission = false;//是否包含 设备信息 权限
//        for (String ps : dePermissions) {
//            if (Manifest.permission.READ_PHONE_STATE.equals(ps)) {
//                hasDevicePermission = true;
//            }
//        }
//
//        final StringBuilder messageBuild = new StringBuilder("酷我音乐需要获取");
//        List<String> text = TransformText.transformText(initPermissions);
//        for (int i = 0, size = text.size(); i < size; i++) {
//            messageBuild.append("（").append(text.get(i)).append("）");
//            if (size > 1) {
//                if (i == size - 2) {
//                    messageBuild.append("和");
//                } else if (i != size - 1) {
//                    messageBuild.append("，");
//                }
//            }
//        }
//        messageBuild.append("权限，以保证歌曲正常播放下载和流量包功能的开通使用。");
//        if(hasDevicePermission){
//            messageBuild.append("\n酷我音乐使用(访问手机识别码)权限仅用于确定设备ID，不会拨打其他号码或影响手机正常通话。");
//        }
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            messageBuild.append("\n请在【设置-应用-酷我音乐-权限】中开启");
//        }else {
//            messageBuild.append("\n请在【设置】中开启");
//        }
//        text = TransformText.transformText(dePermissions);
//        for (int i = 0, size = text.size(); i < size; i++) {
//            //设备信息 替换为 电话
//            String textStr = text.get(i);
//            if(TransformText.READ_PHONE_STATE_STR.equals(textStr)){
//                textStr = "(访问手机识别码)";
//            }
//            messageBuild.append(textStr);
//            if (size > 1) {
//                if (i == size - 2) {
//                    messageBuild.append("和");
//                } else if (i != size - 1) {
//                    messageBuild.append("，");
//                }
//            }
//        }
//        messageBuild.append("，以正常使用酷我音乐功能。");
//        return messageBuild.toString();
//    }
//
    public static CharSequence createRequestMsg(String[] permissions) {
        StringBuilder messageBuild = new StringBuilder("<html>元惜需要获取");
        List<String> text = TransformText.transformText(permissions);
        for (int i = 0, size = text.size(); i < size; i++) {
            messageBuild.append("<font color=\"#1672FA\">（").append(text.get(i)).append("）</font>");
            if (size > 1) {
                if (i == size - 2) {
                    messageBuild.append("和");
                } else if (i != size - 1) {
                    messageBuild.append("，");
                }
            }
        }
        messageBuild.append("权限，以保证" + TransformText.getPermissionTip(permissions) + "，请在之后的权限申请弹窗上放心选择【允许】。");
        messageBuild.append("</html>");
        return Html.fromHtml(messageBuild.toString());
    }

    public static CharSequence createGoSettingMsg(String[] permissions) {
        StringBuilder messageBuild = new StringBuilder("元惜需要获取");
        List<String> text = TransformText.transformText(permissions);
        for (int i = 0, size = text.size(); i < size; i++) {
            messageBuild.append("（").append(text.get(i)).append("）");
            if (size > 1) {
                if (i == size - 2) {
                    messageBuild.append("和");
                } else if (i != size - 1) {
                    messageBuild.append("，");
                }
            }
        }
        messageBuild.append("权限，以保证" + TransformText.getPermissionTip(permissions));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            messageBuild.append("。\n请在【设置-应用-元惜-权限】中开启权限。");
        } else {
            messageBuild.append("。\n请在【设置】中开启权限。");
        }
        return messageBuild.toString();
    }

    //获取 跳转到 应用权限 设置界面，在Android M（6.0）以下系统下，各个厂商的会有所同
    public static SysSettingProvider defaultSettingProvider() {
        return new DefaultSettingProvider();
    }

    public static class DefaultSettingProvider implements SysSettingProvider {
        private static final String MARK = Build.MANUFACTURER.toLowerCase();

        @Override
        public Intent get(Context context) {
            if (MARK.contains("huawei")) {
                return huaweiApi(context);
            } else if (MARK.contains("xiaomi")) {
                return xiaomiApi(context);
            } else if (MARK.contains("oppo")) {
                return oppoApi(context);
            } else if (MARK.contains("vivo")) {
                return vivoApi(context);
            } else if (MARK.contains("samsung")) {
                return samsungApi(context);
            } else if (MARK.contains("meizu")) {
                return meizuApi(context);
            } else if (MARK.contains("smartisan")) {
                return smartisanApi(context);
            }
            return defaultApi(context);
        }

        /**
         * App details page.
         */
        public static Intent defaultApi(Context context) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else {
                intent = new Intent(Settings.ACTION_SETTINGS);
            }
            return intent;
        }

        /**
         * Huawei cell phone Api23 the following method.
         */
        private static Intent huaweiApi(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return defaultApi(context);
            }
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity"));
            return intent;
        }

        /**
         * Xiaomi phone to achieve the method.
         */
        private static Intent xiaomiApi(Context context) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            return intent;
        }

        /**
         * Vivo phone to achieve the method.
         */
        private static Intent vivoApi(Context context) {
            /*Intent intent = new Intent();
            intent.putExtra("packagename", context.getPackageName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
            } else {
                intent.setComponent(new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
            }*/
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            return intent;
        }

        /**
         * Oppo phone to achieve the method.
         */
        private static Intent oppoApi(Context context) {
            return defaultApi(context);
        }

        /**
         * Meizu phone to achieve the method.
         */
        private static Intent meizuApi(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                return defaultApi(context);
            }
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.putExtra("packageName", context.getPackageName());
            intent.setComponent(
                    new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
            return intent;
        }

        /**
         * Smartisan phone to achieve the method.
         */
        private static Intent smartisanApi(Context context) {
            return defaultApi(context);
        }

        /**
         * Samsung phone to achieve the method.
         */
        private static Intent samsungApi(Context context) {
            return defaultApi(context);
        }
    }//DefaultSettingProvider end

    public static class DefaultIamUI implements IamUI {
        private final Context context;

        public DefaultIamUI(Context context) {
            this.context = context;
            check(null != context);
        }

        @Override
        public void showRequestPermissionTip(String[] permissions, final OnClickCancel onClickCancel, final OnClickOk onClickOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(createRequestMsg(permissions));
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickOk.onClick();
                }
            });
            builder.show().setCanceledOnTouchOutside(false);
        }

        @Override
        public void showGoSettingsTip(String[] permissions, final OnClickCancel onClickCancel, final OnClickOk onClickOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(createGoSettingMsg(permissions));
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickOk.onClick();
                }
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickCancel.onClick();
                }
            });
            builder.show().setCanceledOnTouchOutside(false);
        }

        @Override
        public boolean onSettingsBack(String[] permissions, int requestCode, Callback callback) {
            return false;
        }
    }//DefaultIamUI end
}
