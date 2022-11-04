package com.lazylite.mod.widget.preview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Scroller
import androidx.core.graphics.values
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import timber.log.Timber


class ZoomDraweeView(mContext: Context, attrs: AttributeSet?, defStyle: Int): SimpleDraweeView(mContext, attrs, defStyle) {
    constructor(mContext: Context, attrs: AttributeSet?): this(mContext, attrs, 0)
    constructor(mContext: Context) : this(mContext, null)

    private var mScaleDetector: ScaleGestureDetector
    private var mGestureDetector: GestureDetector

    private var mCurrentScale = 1f
    private var mCurrentMatrix: Matrix = Matrix()
    private var mMidX = 0f
    private var mMidY = 0f
    private var mZoomViewListener: OnZoomViewListener? = null

    private var mScroller: Scroller? = null
    private var mLastX = 0f
    private var mLastY = 0f

    private var inReleaseDrag = false
    private var releaseDragDistance = 0f

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                mScroller?.computeScrollOffset()
                val dx = mLastX - (mScroller?.currX?.toFloat() ?: 0f)
                val dy = mLastY - (mScroller?.currY?.toFloat() ?: 0f)
                mLastX = mScroller?.currX?.toFloat() ?: 0f
                mLastY = mScroller?.currY?.toFloat() ?: 0f
                if (mCurrentScale > 1f) {
                    mCurrentMatrix.postTranslate(-dx, -dy)
                    invalidate()
                    checkBorder()
                }
                if (mScroller?.isFinished == false) {
                    sendEmptyMessage(1)
                }
            }
        }
    }

    init {
        val scaleListener: ScaleGestureDetector.OnScaleGestureListener =
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    if (inReleaseDrag) return true
                    mScroller?.forceFinished(true)

                    val scaleFactor: Float = detector.scaleFactor

                    if (mCurrentScale < 0.75 && scaleFactor < 1) {
                        return true
                    }
                    mCurrentScale *= scaleFactor
                    if (mMidX == 0f) {
                        mMidX = width / 2f
                    }
                    if (mMidY == 0f) {
                        mMidY = height / 2f
                    }
                    mCurrentMatrix.postScale(scaleFactor, scaleFactor, mMidX, mMidY)
                    if (mCurrentScale >= 1f) {
                        checkBorder()
                    }
                    invalidate()
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                    super.onScaleEnd(detector)
                    if (inReleaseDrag) return

                    Timber.tag("onScale").e("onScaleEnd" )
                    if (mCurrentScale < 1f) {
                        reset(true)
                    }
//                    checkBorder()
                }
            }
        mScaleDetector = ScaleGestureDetector(context, scaleListener)

        val gestureListener: GestureDetector.SimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                mScroller?.forceFinished(true)
                reset(true)
                mZoomViewListener?.onClick()

                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mScroller?.forceFinished(true)
                if (mCurrentScale > 1f) {
                    mCurrentMatrix.postTranslate(-distanceX, -distanceY)
                    invalidate()
                    checkBorder()
                } else {
                    releaseDragDistance += distanceY
                    if (!inReleaseDrag && releaseDragDistance < -100 || inReleaseDrag) {
                        inReleaseDrag = true
                        mCurrentMatrix.postTranslate(-distanceX, -distanceY)
                        var scale = (1 + distanceY / height)
                        var tempScale = mCurrentScale * scale
                        if (tempScale >= 1) {
                            tempScale = 1f
                            scale = 1f / mCurrentScale
                        }
                        mCurrentScale = tempScale
                        mCurrentMatrix.postScale(scale, scale, width / 2f, height / 2f)
                        invalidate()
                    }
                }
                return true
            }




            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (inReleaseDrag) return true
                mScroller?.forceFinished(true)
                Timber.tag("fling").e("velocityX:$velocityX  velocityY:$velocityY" )
                mScroller?.fling(mLastX.toInt(), mLastY.toInt(), velocityX.toInt() , velocityY.toInt() , 0, 10000, 0, 10000)
                mHandler.sendEmptyMessage(1)
                return true
            }
        }
        mGestureDetector = GestureDetector(context, gestureListener)

        mScroller = Scroller(context)
    }

    private fun checkBorder() {
        val rectF: RectF = getDisplayRect(mCurrentMatrix)
        var reset = false
        var dx = 0f
        var dy = 0f
        if (rectF.left > 0) {
            dx = left - rectF.left
            reset = true
        }
        if (rectF.top > 0) {
            dy = top - rectF.top
            reset = true
        }
        if (rectF.right < right) {
            dx = right - rectF.right
            reset = true
        }
        if (rectF.bottom < height) {
            dy = height - rectF.bottom
            reset = true
        }
        if (reset) {
            mCurrentMatrix.postTranslate(dx, dy)
            invalidate()
        }
    }

    private fun getDisplayRect(matrix: Matrix): RectF {
        val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        matrix.mapRect(rectF)
        return rectF
    }

    override fun setImageURI(uri: Uri?) {
        reset(false)
        super.setImageURI(uri)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        reset(false)
        super.setImageBitmap(bm)
    }

    override fun setController(draweeController: DraweeController?) {
        reset(false)
        super.setController(draweeController)
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount: Int = canvas.save()
        canvas.concat(mCurrentMatrix)
        super.onDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (inReleaseDrag) {
                inReleaseDrag = false
                releaseEvent()
                return true
            }
        } else if (event?.action == MotionEvent.ACTION_DOWN) {
            releaseDragDistance = 0f
        }
        mScaleDetector.onTouchEvent(event)
        if (!mScaleDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event)
        }
        return true
    }

    private fun releaseEvent() {
        if (releaseDragDistance < -height / 4) {
            Timber.tag("matrix").e("mCurrentMatrix:${mCurrentScale}")
            mZoomViewListener?.onRelease(mCurrentMatrix)
        } else {
            reset(false)
        }
    }

    private var resetValueAnim: ValueAnimator? = null
    private fun reset(withAnim: Boolean) {
        if (withAnim) {
            if (resetValueAnim?.isRunning == true) {
                resetValueAnim?.cancel()
            }
            resetValueAnim = ValueAnimator.ofFloat(mCurrentScale, 1f)
            resetValueAnim?.addUpdateListener {
                val value = it.animatedValue as? Float ?: return@addUpdateListener
                val factor = value / mCurrentScale

                Timber.tag("anim").e("value: $value  factor: $factor  mCurrentScale:$mCurrentScale")
                if (value == 1f) {
                    mCurrentMatrix.reset()
                    mCurrentScale = 1f
                } else {
                    mCurrentScale = value
                    if (mMidX == 0f) {
                        mMidX = width / 2f
                    }
                    if (mMidY == 0f) {
                        mMidY = height / 2f
                    }
                    mCurrentMatrix.postScale(factor, factor, mMidX, mMidY)
                }
                invalidate()
            }
            resetValueAnim?.duration = 160
            resetValueAnim?.start()
        } else {
            mCurrentMatrix.reset()
            mCurrentScale = 1f
            invalidate()
        }
    }

    interface OnZoomViewListener {
        fun onClick()
        fun onRelease(scaleMatrix: Matrix)
    }

    fun setZoomOnClickListener(listener: OnZoomViewListener) {
        mZoomViewListener = listener
    }

    override fun onDetach() {
        super.onDetach()
        handler?.removeCallbacksAndMessages(null)
    }
}