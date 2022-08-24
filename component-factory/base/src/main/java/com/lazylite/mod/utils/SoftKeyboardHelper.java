package com.lazylite.mod.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 用完了要把这个listener放掉, 不然会被observer持有造成泄露并不断执行
 * Created by lxh on 2016/4/29.
 */
public class SoftKeyboardHelper {
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private View decorView;

    public void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        decorView = activity.getWindow().getDecorView();
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;
            int navigationBarHeight = 0;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom;
                int height = decorView.getHeight();

                final int keyboardHeightTemp = height - displayHeight;//此处仍然包含了 顶部状态栏的高度
                if (previousKeyboardHeight != keyboardHeightTemp) {
                    previousKeyboardHeight = keyboardHeightTemp;
                    boolean hide = (double) displayHeight / height > 0.8;
                    if (hide) {//如果是隐藏状态，那么这个差值就是 状态栏的高度
                        navigationBarHeight = keyboardHeightTemp;
                    }
                    final int realKeyboardHeight = keyboardHeightTemp - navigationBarHeight;
                    listener.onSoftKeyBoardChange(realKeyboardHeight, !hide);
                }
            }
        };
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible);
    }

    public void releaseListener() {
        if (decorView != null) {
//            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            decorView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        }
    }

    public static boolean hideKeyboard(View windowView) {
        if (windowView == null) {
            return false;
        }

        Context context = windowView.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(windowView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 李建衡：不依赖于焦点
    public static void hideKeyboard(Context context) {
        if (context == null) {
            return ;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static final View.OnTouchListener  hideKeyboardListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyboard(v);
            return false;
        }
    };

    public static void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(hideKeyboardListener);
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
