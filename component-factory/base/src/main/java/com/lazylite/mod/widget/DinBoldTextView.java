package com.lazylite.mod.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.lazylite.mod.utils.FontUtils;


/**
 * Created by zhouchong on 2020/4/30.
 */
public class DinBoldTextView extends AppCompatTextView {
    public DinBoldTextView(Context context) {
        super(context);
        init();
    }

    public DinBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DinBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(FontUtils.getInstance().getDinBoldType());
    }
}
