package com.godq.statisticwidget.histogram

import android.view.ScaleGestureDetector

interface HistogramGestureListener {

    fun onScale(detector: ScaleGestureDetector?)

    fun onScaleBegin(detector: ScaleGestureDetector?)

    fun onScaleEnd(detector: ScaleGestureDetector?)

    fun onHorScroll(x: Float)
}