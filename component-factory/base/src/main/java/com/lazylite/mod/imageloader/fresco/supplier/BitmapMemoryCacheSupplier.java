package com.lazylite.mod.imageloader.fresco.supplier;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.lazylite.mod.App;
import com.lazylite.mod.imageloader.fresco.config.FrescoConstant;

/**
 * Created by tiancheng on 2016/11/11
 */

public class BitmapMemoryCacheSupplier implements Supplier<MemoryCacheParams> {

    @Override
    public MemoryCacheParams get() {
        int maxSize = (int) getMaxCacheSize();
        /**
         * fix fresco TooManyBitmapsException
         * fresco限制了Ashmem中占用图片的数量为384张，5.0以上不在共享内存中所以不会有数量限制的问题
         * @see com.facebook.imagepipeline.memory.BitmapCounterProvider.MAX_BITMAP_COUNT
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new MemoryCacheParams(maxSize, 380, maxSize, Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else {
            return new MemoryCacheParams(maxSize, Integer.MAX_VALUE, maxSize, Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    private long getMaxCacheSize() {
        ActivityManager am = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        int maxMemory = am.getMemoryClass();
        if (isLargeHeap(App.getInstance())) {
            maxMemory = am.getLargeMemoryClass();
        }
        long cacheSize = (maxMemory * ByteConstants.MB) / 4;
        if (cacheSize < FrescoConstant.DEFAULT_MIN_MEMORY_CACHE_SIZE) {
            cacheSize = FrescoConstant.DEFAULT_MIN_MEMORY_CACHE_SIZE;
        }
        if (cacheSize > FrescoConstant.DEFAULT_MAX_MEMORY_CACHE_SIZE) {
            cacheSize = FrescoConstant.DEFAULT_MAX_MEMORY_CACHE_SIZE;
        }
        return cacheSize;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
    }
}
