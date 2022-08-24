package com.lazylite.mod.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author qyh
 * @date 2022/1/18
 * describe:
 */
public class BitmapUtil {

    public static Bitmap getBitmap(final Resources pResources, final int pId) {
        try {
            return loadBitmap(pResources, pId);
        } catch (final OutOfMemoryError pOutOfMemoryError1) {
            System.gc();

            try {
                return loadBitmap(pResources, pId);
            } catch (final OutOfMemoryError pOutOfMemoryError12) {
                return null;
            }
        }
    }

    private static Bitmap loadBitmap(final Resources pResources, final int pId) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(pResources, pId);
        } catch (OutOfMemoryError e) {
        }
        return bitmap;
    }
}
