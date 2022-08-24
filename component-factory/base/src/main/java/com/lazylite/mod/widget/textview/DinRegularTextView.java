package com.lazylite.mod.widget.textview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.lazylite.mod.utils.FontUtils;

/**
 * @author DongJr
 * @date 2020/3/24
 */
public class DinRegularTextView extends AppCompatTextView {

    public DinRegularTextView(Context context) {
        super(context);
        init();
    }

    public DinRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DinRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(FontUtils.getInstance().getDinRegularType());
    }

}
