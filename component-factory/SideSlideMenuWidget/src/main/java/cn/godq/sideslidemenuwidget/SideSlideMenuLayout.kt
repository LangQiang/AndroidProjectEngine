package cn.godq.sideslidemenuwidget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Scroller
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * @author  GodQ
 * @date  2023/3/23 11:37 上午
 */
class SideSlideMenuLayout
@JvmOverloads constructor(mContext: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(mContext, attributeSet, defStyleAttr) {

    var menuShowStateCallback: ((isShow: Boolean) -> Unit)? = null

    private var firstMove: Boolean = true

    private var maxSlideWidth = 0

    private var currentStateIsClose = true

    private val mScroller = Scroller(this.context)
    private var mLastX = 0f

    private var mCurrentDistanceX = 0f

    private val releaseAnim = ValueAnimator.ofInt(0, 1).apply {
        this.addUpdateListener {
            (it.animatedValue as? Int)?.apply {
                scrollX = this
            }
        }
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                mScroller.computeScrollOffset()
                val dx = mLastX - mScroller.currX.toFloat()
                mLastX = mScroller.currX.toFloat()

                val dstScrollX = scrollX + dx.toInt()
                scrollX = min(max(0, dstScrollX), maxSlideWidth)
                currentStateIsClose = scrollX == 0
                if (!mScroller.isFinished) {
                    sendEmptyMessage(1)
                }
            }
        }
    }

    private val gestureDetector = GestureDetectorCompat(this.context, object : GestureDetector.OnGestureListener{
        override fun onDown(e: MotionEvent?): Boolean {
            firstMove = true
            return true
        }

        override fun onShowPress(e: MotionEvent?) {
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (firstMove) {
                firstMove = false
                return true
            }
            mCurrentDistanceX = distanceX
            val dstScrollX = scrollX + distanceX.toInt()
            scrollX = min(max(0, dstScrollX), maxSlideWidth)

            return true
        }

        override fun onLongPress(e: MotionEvent?) {
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (releaseAnim.isRunning) return true
//            if (inReleaseDrag) return true
            mScroller.forceFinished(true)
            mScroller.fling(mLastX.toInt(), 0, velocityX.toInt() , 0 , -width, width, 0, 0)
            mHandler.sendEmptyMessage(1)
            return true
        }

    })

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (children.count() != 2) throw RuntimeException("must have and only have two subViews!")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val mainView = getChildAt(0)
        val menuView = getChildAt(1)
        maxSlideWidth = menuView.measuredWidth
        mainView.layout(0, 0, 0 + mainView.measuredWidth, 0 + mainView.measuredHeight)
        menuView.layout(right, 0, right + menuView.measuredWidth, 0 + menuView.measuredHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onTouchEventParent(event: MotionEvent?): Boolean {
        event?: return false
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            releaseDrag()
        }
        return gestureDetector.onTouchEvent(event)
    }

    private fun releaseDrag() {
        firstMove = true

        val dstScrollX = if (mCurrentDistanceX > 0) {
            menuShowStateCallback?.invoke(true)
            maxSlideWidth
        } else {
            menuShowStateCallback?.invoke(false)
            0
        }
        currentStateIsClose = dstScrollX == 0
        startAnim(dstScrollX)
    }

    private fun startAnim(dstScrollX: Int) {
        val maxWidth = maxSlideWidth
        releaseAnim.setIntValues(scrollX, dstScrollX)
        releaseAnim.duration = if (maxWidth == 0) 100 else (100 * abs(scrollX / maxWidth.toFloat())).toLong()
        releaseAnim.start()
    }

    fun close() {
        startAnim(0)
    }

    fun currentStateIsClose(): Boolean {
        return currentStateIsClose
    }
}