package com.lazylite.mod.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;

import com.example.basemodule.R;
import com.lazylite.mod.utils.ScreenUtility;


/**
 * Created by LiTiancheng on 2016/9/27
 */
public class CircleImgView extends androidx.appcompat.widget.AppCompatImageView {

    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private float mBorderWidth;
    private int mBorderColor = Color.TRANSPARENT;
    private boolean mEnableCover;

    public CircleImgView(Context paramContext) {
        super(paramContext);
        init();
    }

    public CircleImgView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public CircleImgView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircularImage);
        mBorderColor = a.getColor(R.styleable.CircularImage_circularimage_border_color, mBorderColor);
        mBorderWidth = a.getDimension(R.styleable.CircularImage_circularimage_border_width, 0);
        a.recycle();
        init();
    }

    private void init() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (drawable instanceof NinePatchDrawable) {
            return;
        }
        final int width = getWidth();
        final int height = getHeight();
        int layer = canvas.saveLayer(0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = createOvalBitmap(width, height);
        }
        canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mBitmapPaint);
        canvas.restoreToCount(layer);
        drawBorder(canvas, width, height);
    }

    private void drawBorder(Canvas canvas, final int width, final int height) {
        if (mBorderWidth == 0) {
            return;
        }
        canvas.drawCircle(width / 2.0f, height / 2.0f, (width - mBorderWidth) / 2.0f, mBorderPaint);
    }

    public Bitmap createOvalBitmap(int width, int height) {
        if (width == 0) {
            width = ScreenUtility.dip2px(getContext(),10);
        }
        if (height == 0) {
            height = ScreenUtility.dip2px(getContext(),10);
        }

        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = Bitmap.createBitmap(width, height, localConfig);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        final int padding = 0/*mBorderWidth - 3*/;
        RectF localRectF = new RectF(padding + mBorderWidth, padding + mBorderWidth, width - padding - mBorderWidth, height - padding - mBorderWidth);
        localCanvas.drawOval(localRectF, localPaint);
        return localBitmap;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        mBorderWidth = borderWidth;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        invalidate();
    }
}