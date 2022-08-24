package com.lazylite.mod.widget.loading;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import com.example.basemodule.R;
import com.lazylite.mod.App;
import com.lazylite.mod.widget.CircleProgressBar;

/**
 * @author qyh
 * @date 2022/2/22
 * describe:带进度的对话框
 */
public class ProgressDialogMgr {
    private static Dialog sProgressDialog;
    private static CircleProgressBar progressView;

    public static void showLoading(int progress) {
        showLoading(null, progress);
    }

    public static void showLoading(String msg, int progress) {
        Activity sActivity = App.getMainActivity();
        if (sActivity == null || sActivity.isFinishing()) {
            return;
        }
        TextView tvMsg = null;
        if (sProgressDialog == null) {
            try {
                sProgressDialog = new Dialog(sActivity);
                sProgressDialog.setContentView(R.layout.lrlite_base_layout_progress_loading);

                Window window = sProgressDialog.getWindow();
                if (window != null) {
                    progressView = window.findViewById(R.id.progress);
                    tvMsg = window.findViewById(R.id.tv_msg);

                    sProgressDialog.setCanceledOnTouchOutside(false);
                    window.setDimAmount(0);
                    window.setBackgroundDrawableResource(R.color.transparent);
                }

            } catch (Exception e) {
                sProgressDialog = null;
                e.printStackTrace();
            }
        }

        if (progressView != null) {
            progressView.setProgress(progress);
        }
        if (!TextUtils.isEmpty(msg) && tvMsg != null) {
            tvMsg.setText(msg);
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
