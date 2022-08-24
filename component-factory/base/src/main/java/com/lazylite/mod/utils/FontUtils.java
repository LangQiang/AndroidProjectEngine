package com.lazylite.mod.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.lazylite.mod.App;

public class FontUtils {

    public static FontUtils fontsUtil;

    private Context mContext;
    private Typeface iconFontTypeface;
    private Typeface dinRegular;
    private Typeface numTypeface;
    private Typeface monospaceTypeface;  //数字等宽字体


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

    public Typeface getDinBoldType() {
        if (numTypeface == null) {
            numTypeface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/D-DIN-Bold.ttf");
        }
        return numTypeface;
    }

    public Typeface getDinRegularType() {
        if (dinRegular == null) {
            dinRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/D-DIN.ttf");
        }
        return dinRegular;
    }

    public Typeface getNumMonoSpaceType() {
        if (monospaceTypeface == null) {
            monospaceTypeface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/D-DIN-NUMBER.ttf");
        }
        return monospaceTypeface;
    }
}
