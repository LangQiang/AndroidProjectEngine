package com.godq.statisticwidget.histogram

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.abs

class HistogramGestureHelper(context: Context, histogramGestureListener: HistogramGestureListener) {

    fun onTouchEvent(event: MotionEvent?): Boolean {

        var ret = scaleGestureDetector?.onTouchEvent(event) ?: false
        if (scaleGestureDetector?.isInProgress != true && event?.pointerCount == 1) {
            ret = gestureDetector?.onTouchEvent(event) ?: false
        }
        return ret
    }

    private var scaleGestureDetector: ScaleGestureDetector? = null

    private var scaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            histogramGestureListener.onScale(detector)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            histogramGestureListener.onScaleBegin(detector)
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            histogramGestureListener.onScaleEnd(detector)
        }
    }

    private var gestureDetector: GestureDetector? = null

    private var gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (abs(distanceX) / abs(distanceY) > 1.2f) {
                histogramGestureListener.onHorScroll(distanceX)
            }
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return super.onSingleTapUp(e)
        }
    }

    init {
        scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)
        gestureDetector = GestureDetector(context, gestureListener)
    }
}