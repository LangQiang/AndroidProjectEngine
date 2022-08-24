package com.lazylite.mod.utils.toast;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

public class ToastCompat {
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
        }
    }
    public static void setSafelyToastHandler(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }
    private static class SafelyHandlerWarpper extends Handler{
        private Handler handler;

        public SafelyHandlerWarpper(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception ignored){
            }
        }

        @Override
        public void handleMessage(Message msg) {
            handler.handleMessage(msg);
        }
    }
}
