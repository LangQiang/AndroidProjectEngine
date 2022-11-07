package com.lazylite.mod.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.lazylite.mod.App;

public class FontUtils {

    public static FontUtils fontsUtil;

    private Context mContext;
    private Typeface iconFontTypeface;


    private FontUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static FontUtils getInstance() {
        if (fontsUtil == null) {
            fontsUtil = new FontUtils(App.getInstance());
        }
        return fontsUtil;
    }

    public Typeface getIconFontType(Context context) {
        if (iconFontTypeface == null) {
            try {
                iconFontTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
            } catch (Exception e) {

            }
        }
        return iconFontTypeface;
    }


}
