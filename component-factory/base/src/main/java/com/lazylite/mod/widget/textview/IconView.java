package com.lazylite.mod.widget.textview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.lazylite.mod.utils.FontUtils;


public class IconView extends AppCompatTextView {

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontUtils.getInstance().getIconFontType(context));
    }
}
