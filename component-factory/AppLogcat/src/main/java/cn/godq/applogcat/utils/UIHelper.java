package cn.godq.applogcat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.godq.applogcat.mgr.AppLogcat;

public class UIHelper {

    public static int TITLE_BAR_DP = 25;

    private static SimpleDateFormat dateFormat = null;


    @SuppressLint("LogNotTimber")
    public static void safeDismissDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        AppLogcat.getInstance().log("");
        Context context = dialog.getContext();
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            Log.e("UIHelper", "Dialog  is your activity running ?");
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception ignore) {
        }
    }

    @SuppressLint("LogNotTimber")
    public static void safeDismissDialog(DialogInterface dialog, Context context) {
        if (dialog == null) {
            return;
        }
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            Log.e("UIHelper","Dialog  is your activity running ?");
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception ignore) {
        }
    }

    public static String getFormatDate(final String format, long timestamp) {
        if(dateFormat == null){
            dateFormat  = new SimpleDateFormat(format, Locale.CHINA);
        }else{
            dateFormat.applyPattern(format);
        }
        return dateFormat.format(new Date(timestamp));
    }

    public static int getTitleBarHeight(Context context) {
        int height = dip2px(context, TITLE_BAR_DP);
        if (context != null) {
            try {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    height = context.getResources().getDimensionPixelSize(resourceId);
                }
            } catch (Exception e) {
                height = dip2px(context, TITLE_BAR_DP);
            }
        }
        return height;
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = getDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = getDensity(context);
        return (int) (dpValue * scale + 0.5f);
    }

    private static float sDensity;

    public static float getDensity(Context context) {
        if (sDensity == 0) {
            try {
                DisplayMetrics dm = new DisplayMetrics();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
                } else {
                    ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                }
                sDensity = dm.density;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sDensity;
    }

    public static void showToast(Context context, final String toastStr) {
        CommonUtilsKt.runOnUiThread(0 , () -> {
            try {
                Toast toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
                safelyShow(toast);
            } catch (Exception ignored) {
            }
        });
    }

    private static void safelyShow(Toast toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            ToastCompat.setSafelyToastHandler(toast);
        }
        toast.show();
    }

    public static int parseColor(String colorStr, int defaultColor) {
        try {
            return Color.parseColor(colorStr);
        } catch (Exception e) {
            return defaultColor;
        }
    }
}
