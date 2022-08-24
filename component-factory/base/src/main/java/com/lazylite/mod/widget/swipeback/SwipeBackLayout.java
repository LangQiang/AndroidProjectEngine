package com.lazylite.mod.widget.swipeback;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.basemodule.R;
import com.lazylite.mod.fragmentmgr.FragmentOperation;
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.widget.swipeback.app.SwipeBackFragment;
import com.lazylite.mod.widget.vp.NoScrollViewPager;

import java.util.LinkedList;
import java.util.List;

public class SwipeBackLayout extends FrameLayout {
	
	private SwipeBackFragment mFragment;
    /**
     * Minimum velocity that will be detected as a fling
     */
    private static final int MIN_FLING_VELOCITY = 400; // dips per second

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    /**
     * Edge flag indicating that the left edge should be affected.
     */
    public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;

    /**
     * Edge flag indicating that the right edge should be affected.
     */
    public static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;

    /**
     * Edge flag indicating that the bottom edge should be affected.
     */
    public static final int EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM;

    public static final int EDGE_TOP = ViewDragHelper.EDGE_TOP;

    /**
     * Edge flag set indicating all edges should be affected.
     */
    public static final int EDGE_ALL = EDGE_LEFT | EDGE_RIGHT | EDGE_BOTTOM | EDGE_TOP;

    /**
     * A view is not currently being dragged or animating as a result of a
     * fling/snap.
     */
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    /**
     * A view is currently being dragged. The position is currently changing as
     * a result of user input or simulated user input.
     */
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;

    /**
     * Default threshold of scroll
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.3f;

    private static final float DEFAULT_SCROLL_MOVE = 0.05f;

    private static final int OVERSCROLL_DISTANCE = 10;

    private static final int[] EDGE_FLAGS = {
            EDGE_LEFT, EDGE_RIGHT, EDGE_BOTTOM, EDGE_ALL
    };

    private int mEdgeFlag;

    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over
     * this value;
     */
    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;

    private float mMovedThreshold = DEFAULT_SCROLL_MOVE;

    private boolean mEnable = false;

    private View mContentView;

    private ViewDragHelper mDragHelper;

    private float mScrollPercent;

    private int mContentLeft;

    private int mContentTop;

    private SwipeListener mSwipeListener;

    private Drawable mShadowLeft;

    private Drawable mShadowRight;

    private Drawable mShadowBottom;

    private float mScrimOpacity;

    private int mScrimColor = DEFAULT_SCRIM_COLOR;

    private boolean mInLayout;

    private Rect mTmpRect = new Rect();

    /**
     * Edge being dragged
     */
    private int mTrackingEdge;
    private float mLastX;
    private List<ViewPager> mViewPagers = new LinkedList<ViewPager>();
    private List<ViewPager2> mViewPager2s = new LinkedList<ViewPager2>();
    private boolean lowerLayerShowing;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeBackLayoutStyle);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, new ViewDragCallback());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeBackLayout, defStyle,
                R.style.SwipeBackLayout);

        int edgeSize = a.getDimensionPixelSize(R.styleable.SwipeBackLayout_edge_size, -1);
        if (edgeSize > 0)
            setEdgeSize(edgeSize);
        else
        	setEdgeSize(DeviceInfo.WIDTH); // 若为0，则全屏都能滑动，仅限于左右滑动 modify by huqian
        int mode = EDGE_FLAGS[a.getInt(R.styleable.SwipeBackLayout_edge_flag, 0)];
        setEdgeTrackingEnabled(mode);

        int shadowLeft = a.getResourceId(R.styleable.SwipeBackLayout_shadow_left,
                R.drawable.lrlite_base_shadow_left);
        int shadowRight = a.getResourceId(R.styleable.SwipeBackLayout_shadow_right,
                R.drawable.lrlite_base_shadow_right);
        int shadowBottom = a.getResourceId(R.styleable.SwipeBackLayout_shadow_bottom,
                R.drawable.lrlite_base_shadow_bottom);
        setShadow(shadowLeft, EDGE_LEFT);
        setShadow(shadowRight, EDGE_RIGHT);
        setShadow(shadowBottom, EDGE_BOTTOM);
        a.recycle();
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        mDragHelper.setMinVelocity(minVel);
    }

    /**
     * Set up contentView which will be moved by user gesture
     * 
     * @param view
     */
    public void setContentView(View view) {
        mContentView = view;
    }

    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    /**
     * Enable edge tracking for the selected edges of the parent view. The
     * callback's
     * and
     * methods will only be invoked for edges for which edge tracking has been
     * enabled.
     * 
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mEdgeFlag = edgeFlags;
        mDragHelper.setEdgeTrackingEnabled(mEdgeFlag);
    }

    /**
     * Set a color to use for the scrim that obscures primary content while a
     * drawer is open.
     * 
     * @param color Color to use in 0xAARRGGBB format.
     */
    public void setScrimColor(int color) {
        mScrimColor = color;
        invalidate();
    }

    /**
     * Set the size of an edge. This is the range in pixels along the edges of
     * this view that will actively detect edge touches or drags if edge
     * tracking is enabled.
     * 
     * @param size The size of an edge in pixels
     */
    public void setEdgeSize(int size) {
        mDragHelper.setEdgeSize(size);
    }

    /**
     * Register a callback to be invoked when a swipe event is sent to this
     * view.
     * 
     * @param listener the swipe listener to attach to this view
     */
    public void setSwipeListener(SwipeListener listener) {
        mSwipeListener = listener;
    }

    public static interface SwipeListener {
        /**
         * Invoke when state change
         * 
         * @param state flag to describe scroll state
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         * @param scrollPercent scroll percent of this view
         */
        public void onScrollStateChange(int state, float scrollPercent);

        /**
         * Invoke when edge touched
         * 
         * @param edgeFlag edge flag describing the edge being touched
         * @see #EDGE_LEFT
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeTouch(int edgeFlag);

        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        public void onScrollOverThreshold();

        /**
         * Invoke when view Scrolled
         */
        public void onHasScroll();

    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     * 
     * @param threshold
     */
    public void setScrollThresHold(float threshold) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mScrollThreshold = threshold;
    }

    /**
     * Set a drawable used for edge shadow.
     * 
     * @param shadow Drawable to use
     * @param edgeFlag Combination of edge flags describing the edge to set
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setShadow(Drawable shadow, int edgeFlag) {
        if ((edgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        } else if ((edgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        } else if ((edgeFlag & EDGE_BOTTOM) != 0) {
            mShadowBottom = shadow;
        }
        invalidate();
    }

    /**
     * Set a drawable used for edge shadow.
     * 
     * @param resId Resource of drawable to use
     * @param edgeFlag Combination of edge flags describing the edge to set
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setShadow(int resId, int edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
    }

    /**
     * Scroll out contentView and finish the activity
     */
    public void scrollToFinishActivity() {
        final int childWidth = mContentView.getWidth();
        final int childHeight = mContentView.getHeight();

        int left = 0, top = 0;
        if ((mEdgeFlag & EDGE_LEFT) != 0) {
            left = childWidth + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_LEFT;
        } else if ((mEdgeFlag & EDGE_RIGHT) != 0) {
            left = -childWidth - mShadowRight.getIntrinsicWidth() - OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_RIGHT;
        } else if ((mEdgeFlag & EDGE_BOTTOM) != 0) {
            top = -childHeight - mShadowBottom.getIntrinsicHeight() - OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_BOTTOM;
        } else if ((mEdgeFlag & EDGE_TOP) != 0) {
            top = childHeight - mShadowBottom.getIntrinsicHeight() + OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_TOP;
        }

        mDragHelper.smoothSlideViewTo(mContentView, left, top);
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mEnable) {
            return false;
        }
        //add by Litiancheng  tiancheng写的反向注释蛋疼。。
        //只有是viewpager中第一个item，或者是NoScrollViewPager才去拦截事件
        ViewPager mViewPager = getTouchViewPager(mViewPagers, event);
        if(mViewPager != null && mViewPager.getCurrentItem() != 0
                && !(mViewPager instanceof NoScrollViewPager)){//增加NoScrollViewPager的拦截 by xudong.wang
            return false;
        }
        ViewPager2 viewPager2 = getTouchViewPager2(mViewPager2s, event);
        if(viewPager2 != null && viewPager2.getCurrentItem() != 0){
            return false;
        }
        //同时只有是往左滑才拦截,右滑丢给viewpager
        final int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = x - mLastX;
                if (dy < 0) {
                    return false;
                }
                break;
        }
        try {
            return mDragHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            // FIXME: handle exception
            // issues #9
            return false;
        } catch (NullPointerException e) { // monkey跑出来的问题
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable) {
            return true;
        }
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace(); // ui 1.0 SwipeBackLayout
        }
        return true;
    }

    private ViewPager getTouchViewPager(List<ViewPager> mViewPagers, MotionEvent ev){
        if(mViewPagers == null || mViewPagers.size() == 0){
            return null;
        }
        Rect mRect = new Rect();
        for(ViewPager v : mViewPagers){
            v.getHitRect(mRect);
            if(mRect.contains((int)ev.getX(), (int)ev.getY())){
                return v;
            }
        }
        return null;
    }

    private ViewPager2 getTouchViewPager2(List<ViewPager2> mViewPager2s, MotionEvent ev){
        if(mViewPager2s == null || mViewPager2s.size() == 0){
            return null;
        }
        Rect mRect = new Rect();
        for(ViewPager2 v : mViewPager2s){
            v.getHitRect(mRect);
            if(mRect.contains((int)ev.getX(), (int)ev.getY())){
                return v;
            }
        }
        return null;
    }

    private void getAlLViewPager(List<ViewPager> mViewPagers, ViewGroup parent){
        int childCount = parent.getChildCount();
        for(int i=0; i<childCount; i++){
            View child = parent.getChildAt(i);
            if(child instanceof ViewPager){
                mViewPagers.add((ViewPager)child);
            }else if(child instanceof ViewGroup){
                getAlLViewPager(mViewPagers, (ViewGroup)child);
            }
        }
    }

    private void getAlLViewPager2(List<ViewPager2> mViewPager2s, ViewGroup parent){
        int childCount = parent.getChildCount();
        for(int i=0; i<childCount; i++){
            View child = parent.getChildAt(i);
            if(child instanceof ViewPager2){
                mViewPager2s.add((ViewPager2)child);
            }else if(child instanceof ViewGroup){
                getAlLViewPager2(mViewPager2s, (ViewGroup)child);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null){
            try {
                mContentView.layout(mContentLeft, mContentTop,
                        mContentLeft + mContentView.getMeasuredWidth(),
                        mContentTop + mContentView.getMeasuredHeight());
            } catch (Exception e) {
                e.printStackTrace();
                //TODO 有一个ClassCastException的bug，已改怀疑还有类似的，这里try一下
            }
        }
        mInLayout = false;
        getAlLViewPager(mViewPagers, this);
        getAlLViewPager2(mViewPager2s, this);
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == mContentView;
        //drawShadow(canvas, child);
        boolean ret = false;
        try {
        	ret = super.drawChild(canvas, child, drawingTime);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        if (mScrimOpacity > 0 && drawContent
                && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawScrim(canvas, child);
        }
        return ret;
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24 | (mScrimColor & 0xffffff);

        if ((mTrackingEdge & EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.getLeft(), child.getBottom(), getRight(), getHeight());
        } else if ((mTrackingEdge & EDGE_TOP) != 0) {
            canvas.clipRect(child.getLeft(), 0, getRight(), child.getTop());
        }
        canvas.drawColor(color);
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if ((mEdgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top,
                    childRect.left, childRect.bottom);
            mShadowLeft.draw(canvas);
        }

        if ((mEdgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(childRect.right, childRect.top,
                    childRect.right + mShadowRight.getIntrinsicWidth(), childRect.bottom);
            mShadowRight.draw(canvas);
        }

        if ((mEdgeFlag & EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(childRect.left, childRect.bottom, childRect.right,
                    childRect.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.draw(canvas);
        }

        if ((mEdgeFlag & EDGE_TOP) != 0) {
            mShadowBottom.setBounds(childRect.left, childRect.bottom - mShadowBottom.getIntrinsicHeight()
                    , childRect.right, childRect.bottom);
            mShadowBottom.draw(canvas);
        }
    }

    public void attachToFragment(SwipeBackFragment fragment) {
        mFragment = fragment;
        //在support-v4版本21之前，fragment的根view是有一层NoSaveStateFrameLayout包裹的，升级成版本23后，
        //去掉了这一层，再按以前的逻辑去拿getChildAt(0)，拿到的只是布局中的第一个控件，这样明显不行啊
        //所以需要把控件拿出来放到他原来相同Layout类型的“新袋子”中，再将该“新袋子”放入左滑退出的ViewGroup中
        ViewGroup fragmentRoot = (ViewGroup) fragment.getView();
        if (fragmentRoot == null) {
            return;
        }
        ViewGroup copyGroup;//复制父容器新袋子
        Context cxt;
        if ((cxt = mFragment.getActivity()) == null) {
            return;
        }
        if (fragmentRoot instanceof RelativeLayout) {
            copyGroup = new RelativeLayout(cxt);
        } else if (fragmentRoot instanceof LinearLayout) {
            copyGroup = new LinearLayout(cxt);
            ((LinearLayout) copyGroup).setOrientation(LinearLayout.VERTICAL);
        } else if (fragmentRoot instanceof FrameLayout) {
            copyGroup = new FrameLayout(cxt);
        } else if (fragmentRoot instanceof ConstraintLayout) {
            copyGroup = new ConstraintLayout(cxt);
        } else if (fragmentRoot instanceof CoordinatorLayout) {
            copyGroup = new CoordinatorLayout(cxt);
        } else {
            throw new RuntimeException("你用的不是五大布局最常用那三个，你自己写了个ViewGroup父容器吗?!");
        }
        for (int i = 0, count = fragmentRoot.getChildCount(); i < count; i++) {
            View view = fragmentRoot.getChildAt(0);
            fragmentRoot.removeView(view);
            copyGroup.addView(view, view.getLayoutParams());
        }
        addView(copyGroup, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setContentView(copyGroup);
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
            copyGroup.setBackground(fragmentRoot.getBackground());
        }else {
            copyGroup.setBackgroundResource(R.color.LRLiteBase_background);
        }
        fragmentRoot.setBackgroundResource(R.color.transparent);
        fragmentRoot.addView(this, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        private boolean mIsScrollOverValid;

        @Override
        public boolean tryCaptureView(View view, int i) {
            boolean ret = mDragHelper.isEdgeTouched(mEdgeFlag, i);
            if (ret) {
                if (mDragHelper.isEdgeTouched(EDGE_LEFT, i)) {
                    mTrackingEdge = EDGE_LEFT;
                } else if (mDragHelper.isEdgeTouched(EDGE_RIGHT, i)) {
                    mTrackingEdge = EDGE_RIGHT;
                } else if (mDragHelper.isEdgeTouched(EDGE_BOTTOM, i)) {
                    mTrackingEdge = EDGE_BOTTOM;
                } else if (mDragHelper.isEdgeTouched(EDGE_TOP, i)) {
                    mTrackingEdge = EDGE_TOP;
                }
                if (mSwipeListener != null) {
                    mSwipeListener.onEdgeTouch(mTrackingEdge);
                }
                mIsScrollOverValid = true;
            }
            return ret;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mEdgeFlag & (EDGE_LEFT | EDGE_RIGHT);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mEdgeFlag & (EDGE_BOTTOM | EDGE_TOP);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                mScrollPercent = Math.abs((float) left
                        / (mContentView.getWidth() + mShadowLeft.getIntrinsicWidth()));
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                mScrollPercent = Math.abs((float) left
                        / (mContentView.getWidth() + mShadowRight.getIntrinsicWidth()));
            } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                mScrollPercent = Math.abs((float) top
                        / (mContentView.getHeight() + mShadowBottom.getIntrinsicHeight()));
            } else if ((mTrackingEdge & EDGE_TOP) != 0) {
                mScrollPercent = Math.abs((float) top
                        / (mContentView.getHeight() + mShadowBottom.getIntrinsicHeight()));
            }
            mContentLeft = left;
            mContentTop = top;
            invalidate();
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true;
            }
            if (mSwipeListener != null && mDragHelper.getViewDragState() == STATE_DRAGGING
                      && mIsScrollOverValid) {
                if(mScrollPercent >= mScrollThreshold){
                    mIsScrollOverValid = false;
                    mSwipeListener.onScrollOverThreshold();
                }else if(mScrollPercent >=mMovedThreshold){
                    mSwipeListener.onHasScroll();
                }

            }

            if (mScrollPercent >= 1 && mFragment != null && mFragment.isResumed()) {
            	mFragment.close();
            	mFragment = null;
            	return;
            }
            // 主页面的时候不能处理viewpager的显示隐藏
            if (!FragmentOperation.getInstance().isMainLayerShow()) {
                Fragment preFragment = FragmentOperation.getInstance().getPreFragment();
                if (mScrollPercent <= 0 && lowerLayerShowing) {
                    // gone
                    if (preFragment == null) {
                        OnFragmentStackChangeListener onFragmentStackChangeListener = FragmentOperation.getInstance().getOnFragmentStackChangeListener();
                        if (onFragmentStackChangeListener != null) {
                            onFragmentStackChangeListener.onHideMainLayer(true);
                        }
                    } else {
                        View view = preFragment.getView();
                        if (view != null) {
                            view.setVisibility(View.GONE);
                        }
                    }
                    lowerLayerShowing = false;
                } else if (mScrollPercent > 0 && !lowerLayerShowing) {
                    // visible
                    if (preFragment == null) {
                        OnFragmentStackChangeListener onFragmentStackChangeListener = FragmentOperation.getInstance().getOnFragmentStackChangeListener();
                        if (onFragmentStackChangeListener != null) {
                            onFragmentStackChangeListener.onShowMainLayer(false);
                        }
                    } else {
                        View view = preFragment.getView();
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                    lowerLayerShowing = true;
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();

            int left = 0, top = 0;
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                left = xvel >= 0 && mScrollPercent > mScrollThreshold ? childWidth
                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE : 0;
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                left = xvel <= 0 && mScrollPercent > mScrollThreshold ? -(childWidth
                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE) : 0;
            } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                top = yvel <= 0 && mScrollPercent > mScrollThreshold ? -(childHeight
                        + mShadowBottom.getIntrinsicHeight() + OVERSCROLL_DISTANCE) : 0;
            } else if ((mTrackingEdge & EDGE_TOP) != 0) {
                top = yvel >= 0 && mScrollPercent > mScrollThreshold ? (childHeight
                        + mShadowBottom.getIntrinsicHeight() + OVERSCROLL_DISTANCE) : 0;
            }

            mDragHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int ret = 0;
            if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                ret = Math.min(0, Math.max(top, -child.getHeight()));
            } else if ((mTrackingEdge & EDGE_TOP) != 0) {
                ret = Math.min(child.getHeight(), Math.max(top, 0));
            }
            return ret;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mSwipeListener != null) {
                mSwipeListener.onScrollStateChange(state, mScrollPercent);
            }
        }
    }
}
