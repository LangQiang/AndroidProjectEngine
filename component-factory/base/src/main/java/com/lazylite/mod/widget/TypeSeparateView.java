package com.lazylite.mod.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.basemodule.R;
import com.lazylite.mod.utils.ScreenUtility;

import java.util.List;

/**
 * @author qyh
 * @date 2022/3/8
 * describe: 文字1 | 文字2 |文字3...
 */
public class TypeSeparateView extends LinearLayout {

    private Context mContext;

    public TypeSeparateView(Context context) {
        this(context, null);
    }

    public TypeSeparateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setData(List<String> typeList) {
        if (typeList == null || typeList.size() == 0) return;
        for (int i = 0; i < typeList.size(); i++) {
            String typeName = typeList.get(i);
            if (i != 0 && i != typeList.size() - 1) {
                View view = new View(mContext);
                view.setBackgroundResource(R.color.black16);
                addView(view);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();

                layoutParams.height = ScreenUtility.dip2px(7);
                layoutParams.width = ScreenUtility.dip2px(0.5f);
                layoutParams.leftMargin = ScreenUtility.dip2px(3);
                layoutParams.rightMargin = ScreenUtility.dip2px(3);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                view.setLayoutParams(layoutParams);
            }
            TextView textView = new TextView(mContext);
            textView.setText(typeName);
            textView.setTextSize(12);
            textView.setMaxLines(1);
            textView.setIncludeFontPadding(false);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.black40));
            addView(textView);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(layoutParams);
        }
    }
}
