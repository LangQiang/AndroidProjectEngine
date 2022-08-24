package com.lazylite.mod.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.lazylite.mod.log.LogMgr;

/**
 * Created by lxh on 2016/6/14.
 */
public class HorizontalScrollViewCompat extends HorizontalScrollView {
    private float mLeftFading;

    private float mRightFading;

    public HorizontalScrollViewCompat(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalFadingEdgeEnabled(true);
    }
    int mLastX;
    int mLastY;
    boolean needDirJudge=true;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x= (int) ev.getX();
        int y= (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                needDirJudge =true;
                LogMgr.d("HorizontalScrollViewCompat","ACTION_DOWN-----");
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = Math.abs(x-mLastX);
                int deltaY = Math.abs(y-mLastY);
                if(needDirJudge){
                    LogMgr.d("HorizontalScrollViewCompat","ACTION_MOVE---dx:"+deltaX+",dy:"+deltaY);
                    if(deltaX<deltaY&&deltaY>16){  //如果是上下滑动，则转交父控件处理
                        LogMgr.d("HorizontalScrollViewCompat","ACTION_MOVE---dy:"+deltaY);
                        needDirJudge = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if(deltaX>deltaY&&deltaX>16){
                        LogMgr.d("HorizontalScrollViewCompat","ACTION_MOVE---dx:"+deltaX);
                        needDirJudge = false;
                        int xPos = getScrollX();
                        if(xPos<=0||xPos>=getMaxScrollAmount()){
                            LogMgr.d("HorizontalScrollViewCompat","ACTION_MOVE---xPos:"+xPos);
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                needDirJudge =true;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setLeftFading(float leftFading){
        mLeftFading = leftFading;
    }

    public void setRightFading(float rightFading){
        mRightFading = rightFading;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return mLeftFading;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return mRightFading;
    }
}
