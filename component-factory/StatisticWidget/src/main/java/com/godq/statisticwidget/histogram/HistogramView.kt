package com.godq.statisticwidget.histogram

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class HistogramView(context: Context, attr: AttributeSet?, style: Int) : View(context, attr, style) {

    private var paint: Paint = Paint()

    var interval: Float = 100f

    var beginInterval = interval

    private var histogramGestureHelper: HistogramGestureHelper? = null

    private var histogramGestureListener = object : HistogramGestureListener {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            Log.e("HistogramView", "onScale:${detector?.scaleFactor}")
            interval = beginInterval * (detector?.scaleFactor ?: 1f)
            invalidate()
            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            beginInterval = interval
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onHorScroll(x: Float) {
        }

    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)


    init {
        histogramGestureHelper = HistogramGestureHelper(context, histogramGestureListener)
        paint.isAntiAlias = true
        paint.color = 0x80ff5400.toInt()

        post {

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return histogramGestureHelper?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val mid = width / 2f
        val h = height.toFloat()


        val preCount = (mid / interval).toInt()

        for (i in 0 .. preCount) {
            canvas.drawLine(mid - i * interval, 0f, mid - i * interval, h, paint)
            canvas.drawLine(mid + i * interval, 0f, mid + i * interval, h, paint)
        }

    }
}