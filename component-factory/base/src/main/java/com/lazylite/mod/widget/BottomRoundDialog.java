package com.lazylite.mod.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.basemodule.R;


/**
 * @author DongJr
 * @date 2019/1/10
 */
public abstract class BottomRoundDialog extends Dialog {

    public BottomRoundDialog(Context context) {
        this(context, R.style.LRLiteBase_DialogBottomRound);
    }

    public BottomRoundDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void onTouchOutside(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null){
            window.setWindowAnimations(R.style.LRLiteBase_KwPopupAnimation);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM | Gravity.START;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(lp);
        }
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(getContainerLayoutId(), null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view, params);
        onCreateContentView(getLayoutInflater(), view);
    }

    protected int getContainerLayoutId(){
        return R.layout.lrlite_base_dialog_bottom_round;
    }

    protected abstract View onCreateContentView(LayoutInflater inflater, ViewGroup rootView);

    @Override
    public void show() {
        super.show();
        //修改系统menu菜单不能全屏显示问题
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isOutOfBounds(getContext(), event)) {
            onTouchOutside();
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

}
