package com.lazylite.mod.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lazylite.mod.utils.InputEmojiFilter;


/**
 * @author qyh
 * @date 2022/1/21
 * describe:1.禁止输入表情 2.解决EditText滑动冲突
 */
@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {


    public CustomEditText(@NonNull Context context) {
        this(context, null);
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setInputFilter();
    }

    private void setInputFilter() {
        setFilters(new InputFilter[]{new InputEmojiFilter()});
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(canVerticalScroll()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * EditText竖直方向是否可以滚动
     * @return  true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll() {
        //滚动的距离
        int scrollY = getScrollY();
        //控件内容的总高度
        int scrollRange = getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;
        if(scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }
}
