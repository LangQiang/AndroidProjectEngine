package com.lazylite.mod.widget.loading;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Window;

import com.example.basemodule.R;
import com.lazylite.mod.App;
import com.lazylite.mod.fragmentmgr.FragmentOperation;


/**
 * Created by lzf on 7/23/21 10:49 AM
 * <p>
 * 不带进度的Loading
 */
public class LoadingDialogMgr {
    private static Dialog sProgressDialog;

    private static CommonLoadingView loadingView;

    public static void showProcess() {
        showLoading("");
    }

    public static void showProcess(String text) {
        showLoading(text);
    }

    private static void showLoading(String text) {
        Activity sActivity = App.getMainActivity();
        if (sActivity == null || sActivity.isFinishing()) {
            return;
        }
        if (sProgressDialog == null) {
            try {
                sProgressDialog = new Dialog(sActivity);
                sProgressDialog.setContentView(R.layout.lrlite_base_layout_loading);
                Window window = sProgressDialog.getWindow();
                sProgressDialog.setCanceledOnTouchOutside(false);
                if (window != null) {
                    loadingView = window.getDecorView().findViewById(R.id.view_loading);
                    window.setDimAmount(0);
                    window.setBackgroundDrawableResource(R.color.transparent);
                }

            } catch (Exception e) {
                sProgressDialog = null;
                e.printStackTrace();
            }
        }
        if (loadingView != null) {
            loadingView.setTextMessage(!TextUtils.isEmpty(text) ? text : "正在加载");
        }
        showDialog();
    }


    public static void hideProcess() {
        Activity sActivity = App.getMainActivity();
        if (sActivity == null || sActivity.isFinishing() || sProgressDialog == null) {
            return;
        }
        sProgressDialog.cancel();
    }

    private static void showDialog() {
        Activity sActivity = App.getMainActivity();
        if (sActivity == null || sActivity.isFinishing() || sProgressDialog == null || sProgressDialog.isShowing()) {
            return;
        }
        sProgressDialog.show();
    }
}
