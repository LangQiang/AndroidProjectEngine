package com.lazylite.mod.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 有其他样式需求自行封装～～ 默认值别改
 *
 * */

public class CircularGroup extends FrameLayout {
    private static final int DEFAULT_CORNER = 42;
    private Paint p;
    private PorterDuffXfermode porterDuffXfermode;
    Path path;
    private int topLeft = DEFAULT_CORNER;
    private int topRight = DEFAULT_CORNER;
    private int bottomLeft = DEFAULT_CORNER;
    private int bottomRight = DEFAULT_CORNER;

    public CircularGroup(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircularGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);

    }

    public CircularGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, 0);

    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    }

    public void setCorners(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int i = canvas.saveLayer(0, 0, getWidth(), getHeight(), p, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        p.setXfermode(porterDuffXfermode);
//        canvas.drawRect(0.0f, 0.0f, getWidth(),getHeight(),p);
        path = new Path();
        path.moveTo(topLeft, 0);
        path.lineTo(getWidth() - topRight, 0);
        path.arcTo(new RectF(getWidth() - 2 * topRight, 0, getWidth(),
                topRight * 2), -90, 90);
        path.lineTo(getWidth(), getHeight() - bottomRight);
        path.arcTo(new RectF(getWidth() - 2 * bottomRight, getHeight() - 2 * bottomRight, getWidth(),
                getHeight()), 0, 90);
        path.lineTo(bottomLeft, getHeight());
        path.arcTo(new RectF(0, getHeight() - 2 * bottomLeft, bottomLeft * 2, getHeight()), 90, 90);
        path.lineTo(0, topLeft);
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        path.arcTo(new RectF(0, 0, 2 * topLeft, 2 * topLeft), -180, 90);
        path.close();
        canvas.drawPath(path, p);
        p.setXfermode(null);
        canvas.restoreToCount(i);
    }
}
