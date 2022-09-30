package com.godq.statisticwidget.histogram

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.sqrt

class HistogramView(context: Context, attr: AttributeSet?, style: Int) : View(context, attr, style) {

    private var histogramPaint: Paint = Paint()
    private var textPaint: Paint = Paint()
    private var linePaint: Paint = Paint()

    private var rect: RectF = RectF()

    private var boundRect = Rect()

    companion object {
        const val MAX_INTERVAL = 200f
        const val MIN_INTERVAL = 40f
        const val TEXT_ANGLE = -45f

    }

    var bottomPercent = 0.1f

    var interval: Float = 100f

    var scaleSpeed = 1f


    var data: List<IHistogramEntity>? = null
        set(value) {
            field = value
            invalidate()
        }

    var currentIndex = 0

    var totalOffset = 0f

    var bottomHeight: Float? = null

    private var histogramGestureHelper: HistogramGestureHelper? = null

    private var histogramGestureListener = object : HistogramGestureListener {
        override fun onScale(detector: ScaleGestureDetector?) {
            Log.e("HistogramView", "onScale:${detector?.scaleFactor}")
            val scale = ((detector?.scaleFactor ?: 1f) * scaleSpeed)
            val tempInterval = interval * scale
            val tempTotalOffset = totalOffset * scale
            if (tempInterval <= MIN_INTERVAL || tempInterval >= MAX_INTERVAL) return
            interval = tempInterval
            totalOffset = tempTotalOffset
            invalidate()
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?) {
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onHorScroll(x: Float) {
            val tempOffset = totalOffset + x
            val indexOffset = (tempOffset / interval).toInt()
            if (indexOffset >= data?.size?:0 || indexOffset < 0) {
                return
            }
            currentIndex = indexOffset
            totalOffset = tempOffset
            Log.e("HistogramView", "currentIndex:$currentIndex")
            invalidate()
        }

    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)


    init {
        histogramGestureHelper = HistogramGestureHelper(context, histogramGestureListener)
        histogramPaint.isAntiAlias = true
//        histogramPaint.color = 0x80ff5400.toInt()


        textPaint.isAntiAlias = true
        textPaint.color = 0xff000000.toInt()

        linePaint.isAntiAlias = true
        linePaint.color = 0xff000000.toInt()


        post {
            bottomHeight = height * bottomPercent
            histogramPaint.shader = LinearGradient(
                0f, height.toFloat() * (1 - bottomPercent), 0f, 0f,
                intArrayOf(0x80252323.toInt(), 0xff33bbff.toInt(), 0xff8757ff.toInt(), 0xffEDC98A.toInt()),
                floatArrayOf(0f, 0.3f, 0.6f, 1f),
                Shader.TileMode.CLAMP)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bottomHeight = h * 0.3f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return histogramGestureHelper?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        bottomHeight ?: return

        drawDivideLine(canvas)

        drawHistogram(canvas)

        drawAbscissa(canvas)


    }

    private fun drawDivideLine(canvas: Canvas) {
        val y = height - (bottomHeight?: 0f)
        canvas.drawLine(0f, y, width.toFloat(), y, linePaint)
    }

    private fun drawHistogram(canvas: Canvas) {
        val mid = width / 2f
        val y = height - (bottomHeight?: 0f)
        val halfCount = (mid / interval).toInt()
        for (i in 0 .. halfCount) {
            //left half
            val left = getEntity(currentIndex - i)
            var currentXCenter = mid - i * interval
            rect.set(currentXCenter - 10, y - (left?.getHistogramProgress(y) ?: 0f), currentXCenter + 10, y)
            canvas.drawRect(rect, histogramPaint)

            //right half
            if (i == halfCount) break
            val right = getEntity(currentIndex + i + 1)
            currentXCenter = mid + (i + 1) * interval
            rect.set(currentXCenter - 10, y - (right?.getHistogramProgress(y) ?: 0f), currentXCenter + 10, y)
            canvas.drawRect(rect, histogramPaint)
        }
    }

    /**
     * Abscissa æbˈsɪsə 横坐标
     * */
    private fun drawAbscissa(canvas: Canvas) {
        canvas.save()
        val y = height - (bottomHeight?: 0f)
        canvas.translate(0f, y + 60)
        canvas.rotate(TEXT_ANGLE, 0f, 0f)

        val mid = width / 2f
        val halfCount = (mid / interval).toInt()
        for (i in 0 .. halfCount) {
            //left half
            val left = getEntity(currentIndex - i)
            val xLeft = (mid - i * interval)
            drawText(canvas, left?.getAbscissaText(), xLeft / sqrt(2f),xLeft / sqrt(2f), textPaint)

            //right half
            val right = getEntity(currentIndex + i + 1)
            if (i == halfCount) break
            val xRight = (mid + (i + 1) * interval)
            drawText(canvas, right?.getAbscissaText(), xRight / sqrt(2f),xRight / sqrt(2f), textPaint)

        }

        canvas.restore()
    }

    /**
     * x 起始位置要减去text长度的一半
     * */
    private fun drawText(canvas: Canvas, text: String?, centerX: Float, startY: Float, paint: Paint) {
        text?: return
        paint.getTextBounds(text, 0, text.length, boundRect)
        canvas.drawText(text, centerX - boundRect.width() / 2, startY, paint)
    }

    private fun getEntity(index: Int): IHistogramEntity? {
        data?.takeIf {
            it.size > index && index >= 0
        }?.apply {
            return get(index)
        }

        return null
    }
}