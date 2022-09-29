package com.godq.statisticwidget.histogram

import android.view.ScaleGestureDetector

interface HistogramGestureListener {

    fun onScale(detector: ScaleGestureDetector?): Boolean

    fun onScaleBegin(detector: ScaleGestureDetector?): Boolean

    fun onScaleEnd(detector: ScaleGestureDetector?)

    fun onHorScroll(x: Float)
}