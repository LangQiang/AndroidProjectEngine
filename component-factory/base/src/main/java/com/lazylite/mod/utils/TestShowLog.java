package com.lazylite.mod.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lazylite.mod.App;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.widget.CommonCenterDialog;


public class TestShowLog {
    public static TextView logView;
    public static void showOrHideLogView(AppCompatActivity context) {
        if (logView == null) {
            logView = new TextView(App.getInstance());
            logView.setTextSize(15);
            logView.setTextColor(0xffffffff);
            logView.setBackgroundColor(0xee000000);
            if (Build.VERSION.SDK_INT >= 28) {
                logView.setLineHeight(50);
            }
            logView.setVerticalScrollBarEnabled(true);
            logView.setMovementMethod(ScrollingMovementMethod.getInstance());
            logView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("确认删除log");
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", (dialog, which) -> clear());
                    builder.setNegativeButton("取消", (dialog, which) -> UIHelper.safeDismissDialog(dialog, context));
                    builder.show().setCanceledOnTouchOutside(false);
                    return true;
                }
            });
            OnDragTouchListener onDragTouchListener = new OnDragTouchListener();
            logView.setOnTouchListener(onDragTouchListener);
            ViewGroup viewGroup = context.getWindow().getDecorView().findViewById(android.R.id.content);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
            viewGroup.addView(logView, layoutParams);
            log("长按清屏|边缘拖拽");
            log("打印日志：TestShowLog.log()");
        } else {
//            ViewGroup viewGroup = (ViewGroup) MainActivity.getInstance().getRootView();
            ViewGroup viewGroup = context.getWindow().getDecorView().findViewById(android.R.id.content);
            viewGroup.removeView(logView);
            logView = null;
        }
    }

    public static void log(final String msg) {
        if (logView == null) {
            return;
        }
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                if (logView != null) {
                    logView.append(new KwDate().toFormatString("HH:mm:ss.SSS") + " " + msg + "\n");
                    int scrollAmount = logView.getLayout().getLineTop(logView.getLineCount())
                            - logView.getHeight();
                    if (scrollAmount > 0)
                        logView.scrollTo(0, scrollAmount);
                    else
                        logView.scrollTo(0, 0);
                }
            }
        });
    }

    public static void clear() {
        if (logView == null) {
            return;
        }
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                if (logView != null) {
                    logView.setText("");
                }
            }
        });
    }
}
