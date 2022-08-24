package com.lazylite.mod.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.enrique.stackblur.NativeBlurProcess;
import com.lazylite.mod.log.LogMgr;

import java.lang.ref.WeakReference;

public class SnapShotBlurView extends View {

    private int mOverlayColor = 0x33000000;

    private HandlerThread blurThread;
    private Handler handler;
    private long frameCount;
    private long lastFrameCount;  //暂时不用
    private boolean mIsRendering;
    private Context context;
    private Canvas mBlurCanvas;
    private View blurRootView;
    private Bitmap toBlurBitmap;
    private Bitmap blurredBitmap;
    private int[] locations = new int[2];

    private final Rect mRectSrc = new Rect(), mRectDst = new Rect();
    private Paint mPaint;
    private boolean attachToUpdate;


    public SnapShotBlurView(Context context) {
        this(context, null);
    }

    public SnapShotBlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapShotBlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mPaint = new Paint();
        init();
    }

    private void init() {
        //ContextWrapper 真的烦
        for (int i = 0; i < 4 && context != null && !(context instanceof Activity) && context instanceof ContextWrapper; i++) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            blurRootView = ((Activity) context).getWindow().getDecorView();
        }
    }

    public void setBlurRootView(View view) {
        blurRootView = view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBlurBg(canvas);
    }

    private void drawBlurBg(Canvas canvas) {
        if (blurredBitmap != null) {
            lastFrameCount = frameCount;
            mRectSrc.right = blurredBitmap.getWidth();
            mRectSrc.bottom = blurredBitmap.getHeight();
            mRectDst.right = getWidth();
            mRectDst.bottom = getHeight();
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null);
            mPaint.setColor(mOverlayColor);
            canvas.drawRect(mRectDst, mPaint);
        }
    }

    private void updateBlur(boolean retry) {
        if (!ViewCompat.isAttachedToWindow(this)) {
            attachToUpdate = true;
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            if (retry) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        updateBlur(false);
                    }
                });
            }
            return ;
        }
        //绝大多数情况可以在子线程get  但是有些特殊情况不行 耗时程度可以接受 放ui线程里吧
        toBlurBitmap = getToBlurBitmap();
        if (toBlurBitmap == null) {
            return;
        }

        handler.post(new HandlerTask(this));
    }

    public void updateBlur() {
        updateBlur(true);
    }


    private Bitmap getToBlurBitmap() {
        if (blurRootView == null || getWidth() == 0 || getHeight() == 0) {
            return null;
        }
        if (toBlurBitmap == null || mBlurCanvas == null) {
            int width = getWidth();
            int height = getHeight();
            float scaledWidth = width / 5f;
            float scaledHeight = height / 5f;
            toBlurBitmap = Bitmap.createBitmap((int) scaledWidth, (int) scaledHeight, Bitmap.Config.ARGB_8888);
            mBlurCanvas = new Canvas(toBlurBitmap);
        }
        blurRootView.getLocationOnScreen(locations);
        int x = -locations[0];
        int y = -locations[1];

        getLocationOnScreen(locations);
        x += locations[0];
        y += locations[1];

        toBlurBitmap.eraseColor(0);

        int rc = mBlurCanvas.save();
        mIsRendering = true;
        try {
            mBlurCanvas.scale(1.f * toBlurBitmap.getWidth() / getWidth(), 1.f * toBlurBitmap.getHeight() / getHeight());
            mBlurCanvas.translate(-x, -y);
            if (blurRootView.getBackground() != null) {
                blurRootView.getBackground().draw(mBlurCanvas);
            }
            blurRootView.draw(mBlurCanvas);
        } catch (Exception e) {
            LogMgr.e("SnapShotBlurView", e.toString());
        } finally {
            mIsRendering = false;
            mBlurCanvas.restoreToCount(rc);
        }
        return toBlurBitmap;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        blurThread = new HandlerThread("blur-thread");
        blurThread.start();
        handler = new Handler(blurThread.getLooper());
        if (attachToUpdate) {
            updateBlur();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        blurThread.quit();
        super.onDetachedFromWindow();
    }

    private static class HandlerTask implements Runnable {
        WeakReference<SnapShotBlurView> reference;
        public HandlerTask(SnapShotBlurView snapShotBlurView) {
            reference = new WeakReference<>(snapShotBlurView);
        }
        @Override
        public void run() {
            SnapShotBlurView snapShotBlurView = reference.get();
            if (snapShotBlurView != null) {
                snapShotBlurView.blurredBitmap = NativeBlurProcess.blur(snapShotBlurView.toBlurBitmap, 15);
                try {
                    snapShotBlurView.frameCount++;
                } catch (Exception e) {
                    snapShotBlurView.frameCount = 0;
                }
                snapShotBlurView.postInvalidate();
            }
        }
    }
}
