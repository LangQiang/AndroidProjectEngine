package com.lazylite.mod.widget.textview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.lazylite.mod.utils.FontUtils;

public class DinNumMonoSpaceTextView extends AppCompatTextView {
    public DinNumMonoSpaceTextView(Context context) {
        super(context);
        init();
    }

    public DinNumMonoSpaceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DinNumMonoSpaceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(FontUtils.getInstance().getNumMonoSpaceType());
    }
}
