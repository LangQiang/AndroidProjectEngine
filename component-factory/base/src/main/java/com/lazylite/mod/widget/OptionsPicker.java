package com.lazylite.mod.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.interfaces.IPickerViewData;
import com.example.basemodule.R;

import java.util.List;

/**
 * Created by lzf on 2022/1/13 4:27 下午
 */
//https://github.com/Bigkoo/Android-PickerView
public class OptionsPicker<T extends IPickerViewData> {
    @NonNull
    private final OptionsPickerView<T> mClassifyPickerV;
    private OnSelected<T> mListener;
    private List<T> mOptions1;
    private List<List<T>> mOptions2;

    public OptionsPicker(@NonNull Context context) {
        this(context, null);
    }

    public OptionsPicker(@NonNull Context context, String title) {
        //条件选择器
        mClassifyPickerV = new OptionsPickerBuilder(context, (option1, option2, option3, v) -> {
            if (null == mListener) {
                return;
            }
            mListener.onSelected(mOptions1.get(option1), mOptions2 != null ? mOptions2.get(option1).get(option2) : null);
        })
                .setLayoutRes(R.layout.base_layout_album_classify_picker, new CustomListener() {
                    @Override
                    public void customLayout(View view) {
                        if (!TextUtils.isEmpty(title)) {
                            TextView tvTitle = view.findViewById(R.id.tv_title);
                            tvTitle.setText(title);
                        }
                        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> mClassifyPickerV.dismiss());
                        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                            mClassifyPickerV.returnData();
                            mClassifyPickerV.dismiss();
                        });
                    }
                })
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .setTextColorCenter(context.getResources().getColor(R.color.skin_high_blue_color))
                .setDividerColor(context.getResources().getColor(R.color.skin_high_blue_color))
                .setTextColorOut(context.getResources().getColor(R.color.black80))
                .setItemVisibleCount(6)
                .setContentTextSize(15)
                .setLineSpacingMultiplier(2)
                .build();
    }

    public void show(List<T> options1, List<List<T>> options2, OnSelected<T> listener) {
        mOptions1 = options1;
        mOptions2 = options2;
        mListener = listener;
        mClassifyPickerV.setPicker(options1, options2);
        mClassifyPickerV.show(false);
    }

    public void show(List<T> options, OnSelected<T> listener) {
        show(options, null, listener);
    }

    public boolean isShowing() {
        return mClassifyPickerV.isShowing();
    }

    public interface OnSelected<T> {
        void onSelected(T option1, T option2);
    }
}
