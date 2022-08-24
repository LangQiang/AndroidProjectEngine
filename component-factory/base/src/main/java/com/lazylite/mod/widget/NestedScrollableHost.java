package com.lazylite.mod.widget;
/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */
public class NestedScrollableHost extends FrameLayout {

    private int touchSlop = 0;
    private float initialX = 0f;
    private float initialY = 0f;
    private ViewPager2 parentViewPager;

    private ViewPager2 childViewpager;

    public NestedScrollableHost(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        childViewpager = getChildViewpager();
        parentViewPager = getParentViewPager();
    }

    private ViewPager2 getChildViewpager() {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (!(view instanceof ViewPager2)) {
                throw new RuntimeException("NestedScrollableHost的第一个子view必须是ViewPager2!");
            }
            childViewpager = (ViewPager2) view;
        }
        return childViewpager;
    }

    private ViewPager2 getParentViewPager() {
        ViewParent view = getParent();
        while (view != null && !(view instanceof ViewPager2)) {
            view = view.getParent();
        }
        return (ViewPager2) view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    private boolean canChildScroll(int orientation, Float delta) {
        if (childViewpager == null) {
            return false;
        }
        if (orientation == 0) {
            return childViewpager.canScrollHorizontally(-delta.intValue());
        } else if (orientation == 1) {
            return childViewpager.canScrollVertically(-delta.intValue());
        } else {
            throw new IllegalArgumentException();
        }

    }


private void handleInterceptTouchEvent(MotionEvent e) {
        if (parentViewPager == null) {
            return;
        }
        int orientation = parentViewPager.getOrientation();

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return;
        }

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = e.getX();
            initialY = e.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - initialX;
            float dy = e.getY() - initialY;
            boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            float scaledDx = Math.abs(dx) * (isVpHorizontal ? .5f : 1f);
            float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : .5f);

            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                //横滑
                if (isVpHorizontal && !allowParentScroll(dx)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return;
                }
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                // Gesture is perpendicular, allow all parents to intercept
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, isVpHorizontal ? dx : dy)) {
                    // Child can scroll, disallow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                    // Child cannot scroll, allow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
        }
    }

    private boolean allowParentScroll(float dx) {
        if (parentViewPager == null) {
            return false;
        }
        if (childViewpager == null) {
            return true;
        }
        if (parentViewPager.getAdapter() == null || childViewpager.getAdapter() == null) {
            return false;
        }
        boolean isParentLeft = parentViewPager.getCurrentItem() == 0;
        boolean isParentRight = parentViewPager.getCurrentItem() == parentViewPager.getAdapter().getItemCount() - 1;
        boolean isChildLeft = childViewpager.getCurrentItem() == 0;
        boolean isChildRight = childViewpager.getCurrentItem() == childViewpager.getAdapter().getItemCount() - 1;

        if (dx < 0 && isChildRight || dx > 0 && isChildLeft) {
            return true;
        }
        return false;
    }
}