package com.lazylite.mod.widget.pile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by zhouchong on 2019/9/27.
 * 弯月形drawable,鬼才ui
 */
public class HalfMoonDrawable extends RoundedDrawable {
    private Path mClipPath;
    private RectF mClipRect;
    private static final int OFFSET = 4; // 这个值越大，裁掉的半圆面积越小，灵活调整

    public HalfMoonDrawable(Bitmap bitmap) {
        super(bitmap);
        mClipRect = new RectF();
        mClipPath = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        // 裁出个半圆形
        mClipRect.set(mDrawableRect);
        float radius = mDrawableRect.width() / 2;
        mClipRect.left -= (radius + OFFSET);
        mClipRect.right -= (radius + OFFSET);
        mClipRect.left -= (OFFSET / 2);
        mClipRect.right += (OFFSET / 2);
        mClipRect.top -= (OFFSET / 2);
        mClipRect.bottom += (OFFSET / 2);
        radius = mClipRect.width() / 2;
        mClipPath.addRoundRect(mClipRect, radius, radius, Path.Direction.CW);
        canvas.clipPath(mClipPath, Region.Op.DIFFERENCE);
        super.draw(canvas);
    }
}
