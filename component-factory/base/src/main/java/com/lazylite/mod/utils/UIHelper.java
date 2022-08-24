package com.lazylite.mod.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import timber.log.Timber;

public class UIHelper {
    public static void visibleView(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public static void safeDismissDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        Context context = dialog.getContext();
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            Timber.e("Dialog  is your activity running ?");
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
            Timber.e("Dialog  is your activity running ?");
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception ignore) {
        }
    }
}
