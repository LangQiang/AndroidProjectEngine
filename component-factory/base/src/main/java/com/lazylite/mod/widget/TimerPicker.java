package com.lazylite.mod.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.basemodule.R;
import com.lazylite.mod.utils.UIHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qyh
 * @date 2022/2/10
 * describe:自定时间选择器
 */
public class TimerPicker {
    @NonNull
    private final TimePickerView mTimerV;
    private OnSelected mListener;
    boolean isProtocolChecked = false;

    public TimerPicker(@NonNull Context context) {
        this(context, null, false);
    }

    public TimerPicker(@NonNull Context context, String title, boolean showForeverView) {

        mTimerV = new TimePickerBuilder(context, (date, view) -> {
            if (mListener != null) {
                if (!isProtocolChecked) {
                    mListener.onSelected(date);
                } else {
                    mListener.isForever();
                }
            }
        }).setLayoutRes(R.layout.base_layout_timer_picker, new CustomListener() {
            @Override
            public void customLayout(View view) {
                if (!TextUtils.isEmpty(title)) {
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText(title);
                }
                if (showForeverView) {
                    TextView tvForever = view.findViewById(R.id.tv_forever);
                    CheckBox protocolCheck = view.findViewById(R.id.protocol_check);
                    UIHelper.visibleView(tvForever, protocolCheck);

                    protocolCheck.setOnCheckedChangeListener((bottonView, isChecked) -> isProtocolChecked = isChecked);
                    tvForever.setOnClickListener(v -> {
                        protocolCheck.performClick();
                    });
                }
                view.findViewById(R.id.btn_cancel).setOnClickListener(v -> mTimerV.dismiss());
                view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                    mTimerV.returnData();
                    mTimerV.dismiss();
                });
            }
        }).setTitleText(title)
                .setDividerColor(ContextCompat.getColor(context, R.color.app_theme_color))
                .setTextColorCenter(ContextCompat.getColor(context, R.color.app_theme_color))
                .setLineSpacingMultiplier(2)
                .build();
    }

    public void show(OnSelected listener) {
        mListener = listener;
        mTimerV.show();
    }

    public boolean isShowing() {
        return mTimerV.isShowing();
    }

    public interface OnSelected {
        void onSelected(Date date);

        void isForever();
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
