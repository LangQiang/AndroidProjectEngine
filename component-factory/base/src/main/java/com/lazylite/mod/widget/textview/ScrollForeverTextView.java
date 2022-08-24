package com.lazylite.mod.widget.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class ScrollForeverTextView extends AppCompatTextView {
  
    public ScrollForeverTextView(Context context) {  
        super(context);  
    }  
  
    public ScrollForeverTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public ScrollForeverTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);  
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        if(getText() != null && getText().equals(text))
            return;
        super.setText(text,type);
    }

    //oppo，vivo等奇葩手机会因为这个鸟毛弹出不键盘，add by LiTiancheng
    @Override
    public boolean isFocused() {
        return true;
    }
}