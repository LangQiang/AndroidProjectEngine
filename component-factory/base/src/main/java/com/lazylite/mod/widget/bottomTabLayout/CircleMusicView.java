package com.lazylite.mod.widget.bottomTabLayout;

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
import android.os.Message;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.basemodule.R;

/**
 * Created by tc :)
 */
public class CircleMusicView extends AppCompatImageView implements CommonHandler.MessageHandler {

    private static final int DEFAULT_BORDER_WIDTH = 2;//dp
    private static final int DEFAULT_DEFALUT_SIZE = 43;//dp

    private static final int MSG_WHAT_ROTATION = 0;
    private static final int DEFAULT_ROTATION_SPEED = 50;

    private int mDefalutSize;
    private float mProgressWidth;
    // 进度槽颜色
    private int mProgressSlotColor;
    // 缓冲进度颜色
    private int mProgressBufferColor;
    // 进度颜色
    private int mProgressColor;
    private int mSize;

    private float mBufferprogress;
    private float mProgress;
    private float mDegree;

    private CommonHandler mRotateHandler;

    // 遮罩相关
    private Bitmap mShadeBitmap;
    private Paint mPaint;

    // 进度相关
    private Paint mProgressPaint;
    private RectF mProgressRect;
    // 是否需要进度槽
    private boolean mNeedProessSlot;

    public CircleMusicView(Context context) {
        this(context, null);
    }

    public CircleMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRotateHandler = new CommonHandler(this);
        mDefalutSize = (int) (getResources().getDisplayMetrics().density * DEFAULT_DEFALUT_SIZE);
        int defaultBorderWidth = (int) (getResources().getDisplayMetrics().density * DEFAULT_BORDER_WIDTH);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LRLiteBase_CircleMusicView, 0, 0);
        try {
            mProgressWidth = ta.getDimensionPixelOffset(R.styleable.LRLiteBase_CircleMusicView_circle_music_view_progress_width,
                    defaultBorderWidth);
            mProgressBufferColor = ta.getColor(R.styleable.LRLiteBase_CircleMusicView_circle_music_view_progress_slot_color,
                    Color.LTGRAY);
            mProgressSlotColor = ta.getColor(R.styleable.LRLiteBase_CircleMusicView_circle_music_view_progress_buffer_color,
                    Color.GRAY);
            mProgressColor = ta.getColor(R.styleable.LRLiteBase_CircleMusicView_circle_music_view_progress_color,
                    getResources().getColor(R.color.rgbFFFF5400));
            mNeedProessSlot = ta.getBoolean(R.styleable.LRLiteBase_CircleMusicView_circle_music_view_progress_need_slot,
                    false);
        } finally {
            ta.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setFilterBitmap(true);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final Drawable drawable = getDrawable();
        if (drawable == null || drawable instanceof NinePatchDrawable) {
            return;
        }
        int layer = canvas.saveLayer(0F, 0F, mSize, mSize, null, Canvas.ALL_SAVE_FLAG);
        canvas.rotate(mDegree, mSize / 2, mSize / 2);
        drawable.setBounds(0, 0, mSize, mSize);
        // 原图
        drawable.draw(canvas);
        if (mShadeBitmap == null || mShadeBitmap.isRecycled()) {
            mShadeBitmap = createShadeBitmap();
        }
        // 遮罩
        canvas.drawBitmap(mShadeBitmap, 0F, 0F, mPaint);
        canvas.restoreToCount(layer);

        // 进度槽
        if (mNeedProessSlot) {
            mProgressPaint.setColor(mProgressSlotColor);
            canvas.drawArc(mProgressRect, 0F, 360F, false, mProgressPaint);
        }
        // 缓冲进度
//        mProgressPaint.setColor(mProgressBufferColor);
//        canvas.drawArc(mProgressRect, -90F, mBufferprogress, false, mProgressPaint);

        // 进度
        mProgressPaint.setColor(mProgressColor);
        canvas.drawArc(mProgressRect, -90F, mProgress, false, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = measureDimension(mDefalutSize, widthMeasureSpec);
        int measureHeight = measureDimension(mDefalutSize, heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSize = Math.min(w, h);
        // 进度条范围
        float halfBoardWidth = mProgressWidth / 2;
        mProgressRect = new RectF(halfBoardWidth,
                halfBoardWidth,
                mSize - halfBoardWidth,
                mSize - halfBoardWidth);
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setBufferProgress(float progress) {
        this.mBufferprogress = progress;
        invalidate();
    }

    public void setProgressColor(int color) {
        this.mProgressColor = color;
        invalidate();
    }

    public void setmNeedProessSlot(boolean needProessSlot) {
        this.mNeedProessSlot = needProessSlot;
        invalidate();
    }

    /*public void changeColorFilter(boolean isColor) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(isColor ? 1 : 0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        setColorFilter(filter);
    }*/

    public void startRotate(boolean rotate) {
        //changeColorFilter(rotate);
        mRotateHandler.removeMessages(MSG_WHAT_ROTATION);
        if (rotate) {
            mRotateHandler.sendEmptyMessage(MSG_WHAT_ROTATION);
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mDegree = 0;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mDegree = 0;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MSG_WHAT_ROTATION) {
            mDegree++;
            if (mDegree > 360) {
                mDegree = 0;
            }
            setDegree(mDegree);
            mRotateHandler.sendEmptyMessageDelayed(MSG_WHAT_ROTATION,
                    DEFAULT_ROTATION_SPEED);
        }
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    private void setDegree(float degree) {
        this.mDegree = degree;
        invalidate();
    }

    /**
     * 弄个圆形遮罩图片,Xfermode只能要图片，画别的不行
     */
    private Bitmap createShadeBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        RectF f = new RectF(mProgressWidth,
                mProgressWidth,
                mSize - mProgressWidth,
                mSize - mProgressWidth);
        canvas.drawOval(f, paint);
        return bitmap;
    }
}
