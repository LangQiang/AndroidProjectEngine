package cn.godq.applogcat.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.godq.applogcat.mgr.AppLogcat;

public class UIHelper {

    private static SimpleDateFormat dateFormat = null;


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

    public static String getFormatDate(final String format) {
        if(dateFormat == null){
            dateFormat  = new SimpleDateFormat(format, Locale.CHINA);
        }else{
            dateFormat.applyPattern(format);
        }
        return dateFormat.format(new Date());
    }
}
