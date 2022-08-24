package com.enrique.stackblur;

import android.graphics.Bitmap;

import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.NativeLibLoadHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * https://github.com/kikoso/android-stackblur
 * Created by tiancheng on 2016/12/1
 */

public class NativeBlurProcess {

    private static final String TAG = "NativeBlurProcess";
    private static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);
    private static boolean isLoad;

    private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount, int threadIndex, int round);

    static {
        isLoad = NativeLibLoadHelper.simpleLoad("blur");

        if (!isLoad) {
            LogMgr.e(TAG, "libblur load failed");
        }

        LogMgr.e(TAG, "isLoad :" + isLoad);
    }

    public static Bitmap blur(Bitmap original, float radius) {
        if (!isLoad || original == null || original.isRecycled()) { // 如果加载失败的话，就别折腾了
            return null;
        }

        Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);

        int cores = EXECUTOR_THREADS;

        List<NativeTask> horizontal = new ArrayList<>(cores);
        List<NativeTask> vertical = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            horizontal.add(new NativeTask(bitmapOut, (int) radius, cores, i, 1));
            vertical.add(new NativeTask(bitmapOut, (int) radius, cores, i, 2));
        }

        try {
            EXECUTOR.invokeAll(horizontal);
        } catch (InterruptedException e) {
            return bitmapOut;
        }

        try {
            EXECUTOR.invokeAll(vertical);
        } catch (InterruptedException e) {
            return bitmapOut;
        }
        return bitmapOut;
    }

    private static class NativeTask implements Callable<Void> {
        private final Bitmap _bitmapOut;
        private final int _radius;
        private final int _totalCores;
        private final int _coreIndex;
        private final int _round;

        public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
            _bitmapOut = bitmapOut;
            _radius = radius;
            _totalCores = totalCores;
            _coreIndex = coreIndex;
            _round = round;
        }

        @Override
        public Void call() throws Exception {
            functionToBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
            return null;
        }
    }
}
