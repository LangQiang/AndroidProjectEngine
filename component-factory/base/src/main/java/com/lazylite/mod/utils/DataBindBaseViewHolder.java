package com.lazylite.mod.utils;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseViewHolder;

public class DataBindBaseViewHolder extends BaseViewHolder {

    private final ViewDataBinding viewDataBinding;

    public Object extra;

    public DataBindBaseViewHolder(View view) {
        super(view);
        viewDataBinding = DataBindingUtil.bind(itemView);
    }

    public ViewDataBinding getViewDataBinding() {
        return viewDataBinding;
    }
}
