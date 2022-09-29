package com.godq.statisticwidget.histogram

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class HistogramGestureHelper(context: Context, histogramGestureListener: HistogramGestureListener) {
    fun onTouchEvent(event: MotionEvent?): Boolean? {
        return scaleGestureDetector?.onTouchEvent(event)
    }

    private var scaleGestureDetector: ScaleGestureDetector? = null

    private var scaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            return histogramGestureListener.onScale(detector)
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return histogramGestureListener.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            histogramGestureListener.onScaleEnd(detector)
        }
    }

    init {
        scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)
    }
}