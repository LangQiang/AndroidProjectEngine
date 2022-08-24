package com.lazylite.mod.utils.toast;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basemodule.R;
import com.lazylite.mod.App;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.AppInfo;

import java.lang.ref.WeakReference;

//by wang meng
public final class KwToast {
    private static WeakReference<Toast> sCurToast;

    //
    public static void show(final String content) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                showToast(content, 0, false);
            }
        });
    }

    public static void show(final String content, final boolean longToast) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                showToast(content, 0, longToast);
            }
        });
    }

    public static void show(final int resId) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                showToast(resId, 0, false);
            }
        });
    }

    public static void show(final int resId, final boolean longToast) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                showToast(resId, 0, longToast);
            }
        });
    }

    public static void showIconToast(final String content, final int iconResId, boolean longToast) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                showToast(content, iconResId, longToast);
            }
        });
    }

    //
    //系统toast
    @Deprecated
    public static void showSysToast(final String content) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                try {
                    Toast toast = Toast.makeText(App.getInstance().getApplicationContext(), content, Toast.LENGTH_SHORT);
                    safelyShow(toast);
                } catch (Exception ignored) {
                }
            }
        });
    }

    //系统toast
    @Deprecated
    public static void showSysToast(final int resId) {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                try {
                    Toast toast = Toast.makeText(App.getInstance().getApplicationContext(), resId, Toast.LENGTH_SHORT);
                    safelyShow(toast);
                } catch (Exception ignored) {
                }
            }
        });
    }

    //
    private static void showToast(final Object content, final int tipIconResId, final boolean longToast) {
        // 只在前台线程显示Toast
        if (AppInfo.IS_FORGROUND) {
            if (App.getInstance() == null || App.getInstance().getApplicationContext() == null) {
                return;
            }
            try {
                final String msg;
                final View view;
                if (content instanceof Integer) {
                    msg = App.getInstance().getApplicationContext().getResources().getString((Integer) content);
                } else {
                    msg = content.toString();
                }
                view = createToastView(msg, tipIconResId);
                final TextView textTV = view.findViewById(R.id.tv_msg);
                final ImageView tipIV = view.findViewById(R.id.iv_icon);

                if(null != tipIV){
                    if (tipIconResId <= 0) {
                        tipIV.setImageResource(R.drawable.base_icon_toast_tip);
                    } else {
                        tipIV.setImageResource(tipIconResId);
                    }
                }
                if(null !=  textTV){
                    textTV.setText(msg);
                }

                closeIfNeed();
                Toast toast = new Toast(App.getInstance().getApplicationContext());
                sCurToast = new WeakReference<>(toast);
                toast.setDuration(longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.setGravity(Gravity.CENTER, 0, 0);
                safelyShow(toast);
            } catch (Exception ignored) {
                if (content instanceof Integer) {
                    showSysToast((Integer) content);
                } else {
                    showSysToast(content.toString());
                }
            }
        }
    }

    private static void closeIfNeed() {
        if (null != sCurToast) {
            final Toast toast = sCurToast.get();
            if (null != toast) {
                toast.cancel();
            }
        }
    }

    private static View createToastView(String content, int iconResId) {
        final View view;
        if (iconResId > 0) {
            if (content.length() >= 21) {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip_big_big_with_icon, null);
            } else if (content.length() >= 12) {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip_big_with_icon, null);
            } else {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip_with_icon, null);
            }
        } else {
            if (content.length() >= 21) {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip_big_big, null);
            } else if (content.length() >= 12) {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip_big, null);
            } else {
                view = View.inflate(App.getInstance().getApplicationContext(), R.layout.base_layout_toast_tip, null);
            }
        }
        return view;
    }

    private static void safelyShow(Toast toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            ToastCompat.setSafelyToastHandler(toast);
            toast.show();
        } else {
            toast.show();
        }
    }

    private KwToast() {
    }
}
