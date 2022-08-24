package com.lazylite.mod.widget.vp;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * If ListView in ViewPager, fix up down and right left sliding conflicts
 *
 * @author LiTiancheng 2015/8/8.
 */
public class ViewPagerCompat extends ViewPagerFixed {

    private float mDownX;
    private float mDownY;

    public ViewPagerCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 优化ViewPager和父层SwipeBackLayout左右滑动的冲突 
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                
                if (getCurrentItem() > 0) {//非第一个就阻止Parent截获事件 by wangxudong
                	getParent().requestDisallowInterceptTouchEvent(true);
				}else {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
                
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDownX) > Math.abs(ev.getY() - mDownY)) {
                	if (getCurrentItem() > 0 || (ev.getX() - mDownX) < 0) {//非第一个或者左滑都阻止Parent截获事件 by wangxudong
                    	getParent().requestDisallowInterceptTouchEvent(true);
    				}else {
    					getParent().requestDisallowInterceptTouchEvent(false);
					}
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
