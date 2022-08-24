package com.lazylite.mod.imageloader.fresco.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.palette.graphics.Palette;

import com.enrique.stackblur.NativeBlurProcess;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.lazylite.mod.utils.ColorUtils;

public class PaletteBlurProcessor extends BasePostprocessor {

    private int radius;

    private int marginLeft;

    private float marginTopPercent;

    private float sampling;
    private final Rect srcRect;
    private final Rect dstRect;

    private Paint p = new Paint();


    public PaletteBlurProcessor(int radius, float sampling, int marginLeft, float marginTopPercent) {
        this.radius = radius;
        this.marginLeft = marginLeft;
        this.marginTopPercent = marginTopPercent;
        this.sampling = sampling;
        srcRect = new Rect(0, 0, 0, 0);
        dstRect = new Rect(0, 0, 0, 0);
        p.setAntiAlias(true);
        p.setAlpha(20);
    }



    @Override
    public CloseableReference<Bitmap> process(
            Bitmap sourceBitmap,
            PlatformBitmapFactory bitmapFactory) {

//        int paletteColor = getPalette(sourceBitmap);

        CloseableReference<Bitmap> destBitmapRef =
                bitmapFactory.createBitmapInternal(
                        sourceBitmap.getWidth(),
                        sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(destBitmapRef.get());

//        canvas.drawColor(paletteColor);

        srcRect.set(0, 0, destBitmapRef.get().getWidth(), destBitmapRef.get().getHeight());
        dstRect.set(0, 0, destBitmapRef.get().getWidth(), destBitmapRef.get().getHeight());

        if (marginLeft != 0 || marginTopPercent != 0) {
            canvas.drawBitmap(sourceBitmap, srcRect, dstRect, null);
        }

        int marginTop = (int) (sourceBitmap.getHeight() * marginTopPercent);

        //可以缩放后 blur
//        Matrix matrix = new Matrix();
//        matrix.preScale(sampling, sampling);
//        CloseableReference<Bitmap> tempBitmapRef =
//                bitmapFactory.createBitmap(
//                        sourceBitmap,
//                        marginLeft,
//                        marginTop,
//                        sourceBitmap.getWidth() - marginLeft,
//                        sourceBitmap.getHeight() - marginTop,
//                        matrix,
//                        true);
        Bitmap blurredBitmap = NativeBlurProcess.blur(sourceBitmap, radius);

        if (blurredBitmap != null) {
            srcRect.set(0, 0, blurredBitmap.getWidth(), blurredBitmap.getHeight());
            dstRect.set(0, marginTop, destBitmapRef.get().getWidth(), destBitmapRef.get().getHeight());

            canvas.drawBitmap(blurredBitmap, srcRect, dstRect, p);
        }


        try {
//            process(destBitmapRef.get(), sourceBitmap);
            return CloseableReference.cloneOrNull(destBitmapRef);
        } finally {
            CloseableReference.closeSafely(destBitmapRef);
        }
    }

    private int getPalette(Bitmap result) {
        Palette palette = Palette.from(result).generate();

        Palette.Swatch swatch = palette.getMutedSwatch();
        if (swatch == null) {
            swatch = palette.getVibrantSwatch();
            if (swatch == null) {
                for (Palette.Swatch s : palette.getSwatches()) {
                    if (s != null) {
                        swatch = s;
                        break;
                    }
                }
            }
        }
        int color = 0;
        if (swatch != null) {
            color = swatch.getRgb();
        }
        return color;
    }

    @Override public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("picBig");
    }
}
