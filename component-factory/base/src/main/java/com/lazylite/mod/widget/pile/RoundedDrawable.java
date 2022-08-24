package com.lazylite.mod.widget.pile;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * @date       2014年4月30日
 * @author     谭帅
 */
public class RoundedDrawable extends Drawable {

	private final RectF mBounds = new RectF();
	protected final RectF mDrawableRect = new RectF();
	private final RectF mBitmapRect = new RectF();
	private final BitmapShader mBitmapShader;
	private final Paint mBitmapPaint;
	private final int mBitmapWidth;
	private final int mBitmapHeight;
	private final RectF mBorderRect = new RectF();
	private final Matrix mShaderMatrix = new Matrix();

	private float mCornerRadius = 0;
	private boolean isCircle;

	public RoundedDrawable(Bitmap bitmap) {

		mBitmapWidth = bitmap.getWidth();
		mBitmapHeight = bitmap.getHeight();
		mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		mBitmapShader.setLocalMatrix(mShaderMatrix);

		mBitmapPaint = new Paint();
		mBitmapPaint.setStyle(Paint.Style.FILL);
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

	}

	public static RoundedDrawable fromBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			return new RoundedDrawable(bitmap);
		} else {
			return null;
		}
	}

	private void updateShaderMatrix() {
		mBorderRect.set(mBounds);
		mShaderMatrix.set(null);
		mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect,
				Matrix.ScaleToFit.FILL);

		mDrawableRect.set(mBorderRect);
		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);

		mBounds.set(bounds);

		updateShaderMatrix();
	}

	@Override
	public void draw(Canvas canvas) {
		if (isCircle) {
			mCornerRadius = mDrawableRect.width() / 2;
		}
		canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius,
				mBitmapPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mBitmapPaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mBitmapPaint.setColorFilter(cf);
		invalidateSelf();
	}

	public RoundedDrawable setCornerRadius(float radius) {
		mCornerRadius = radius;
		return this;
	}

	public void setCircle() {
		isCircle = true;
	}

}
