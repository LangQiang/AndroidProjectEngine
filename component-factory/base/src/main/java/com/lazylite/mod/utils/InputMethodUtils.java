package com.lazylite.mod.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lazylite.mod.App;
import com.lazylite.mod.log.LogMgr;

/**
 * 输入法相关的公共方法
 * 
 * @author 刘金艺
 * 
 */
public class InputMethodUtils {

	public static void closeKeyboard(Activity context) {
		try {
			InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (m != null && m.isActive()) {
				m.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showKeyboard(Context context) {
		try {
			InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (m != null && m.isActive()) {
				m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 //控制在dialog里的editview键盘，使用该方法
	public static void hideDialogIM(View editView) {
        try {
            InputMethodManager im = (InputMethodManager) App.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            IBinder windowToken = editView.getWindowToken();
            boolean ret = false;
            if (windowToken != null) {
                // always de-activate IM
                ret = im.hideSoftInputFromWindow(windowToken, 0);
            }

            if (!ret) {
            	windowToken = editView.getWindowToken();
                if (windowToken != null) {
                    im.hideSoftInputFromWindow(windowToken, 0);
                }
            }
        } catch (Exception e) {
        	LogMgr.e("InputUtils", e);
        }
    }	
}
