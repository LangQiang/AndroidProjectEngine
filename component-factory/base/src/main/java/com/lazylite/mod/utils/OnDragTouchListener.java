package com.lazylite.mod.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class OnDragTouchListener implements View.OnTouchListener {

    private float mOriginalX, mOriginalY;//手指按下时的初始位置
    private float mClickX, mClickY;//手指按下时的初始位置
    private int parentWidth, parentHeight;
    int[] location = new int[2];
    private OnDraggableClickListener mListener;
    private boolean notIntercept;

    public OnDragTouchListener() {
    }


    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ViewParent parent = v.getParent();
                if (parent instanceof View) {
                    ((View) parent).getLocationOnScreen(location);
                    parentWidth = ((View) parent).getWidth();
                    parentHeight = ((View) parent).getHeight();
                }
                mOriginalX = mClickX = event.getRawX();
                mOriginalY = mClickY = event.getRawY();
                if (mOriginalY > v.getY() + 100 && mOriginalX < v.getX() + v.getWidth() - 100) {
                    notIntercept = true;
                    return false;
                } else {
                    notIntercept = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (notIntercept) {
                    return false;
                }
                float mX = event.getRawX();
                float mY = event.getRawY();
                float dX = mX - mOriginalX;
                float dY = mY - mOriginalY;
                setXyWithCheckEdge(v, dX, dY);
                mOriginalX = mX;
                mOriginalY = mY;
                break;
            case MotionEvent.ACTION_UP:
                if (notIntercept) {
                    return false;
                }
                //如果移动距离过小，则判定为点击
                if (Math.abs(event.getRawX() - mClickX) <
                        ScreenUtility.dip2px(v.getContext(),5) &&
                        Math.abs(event.getRawY() - mClickY) <
                                ScreenUtility.dip2px(v.getContext(),5)) {
                    if (mListener != null) {
                        mListener.onClick(v);
                    }
                }
                //消除警告
                v.performClick();
                break;
        }
        return true;
    }


    private boolean setXyWithCheckEdge(View v, float dX, float dY) {
        float newX = v.getX() + dX;
        float newY = v.getY() + dY;
        if (dX < 0) {
            if (v.getX() + dX < location[0]) {
                newX = location[0];
            }
        } else {
            if (v.getX() + v.getWidth() + dX > location[0] + parentWidth) {
                newX = location[0] + parentWidth - v.getWidth();
            }
        }
        if (dY < 0) {
            if (v.getY() + dY < location[1]) {
                newY = location[1];
            }
        } else {
            if (v.getY() + v.getHeight() + dY > location[1] + parentHeight) {
                newY = location[1] + parentHeight - v.getHeight();
            }
        }
        v.setX(newX);
        v.setY(newY);
        return true;
    }

    public OnDraggableClickListener getOnDraggableClickListener() {
        return mListener;
    }

    public void setOnDraggableClickListener(OnDraggableClickListener listener) {
        mListener = listener;
    }



    /**
     * 控件拖拽监听器
     */
    public interface OnDraggableClickListener {

        /**
         * 当控件拖拽完后回调
         *
         * @param v    拖拽控件
         * @param left 控件左边距
         * @param top  控件右边距
         */
        void onDragged(View v, int left, int top);

        /**
         * 当可拖拽控件被点击时回调
         *
         * @param v 拖拽控件
         */
        void onClick(View v);
    }
}