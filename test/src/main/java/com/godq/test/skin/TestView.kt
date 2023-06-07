package com.godq.test.skin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import com.godq.test.R
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/2 4:28 PM
 */
class TestView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attributes, defStyleAttr) {

    private val paint = Paint()
    private val paint2 = Paint()

    private val onSkinChangedListener:() -> Unit = {
        SkinManager.getSkinResource()?.getColor(R.color.skin_text_Tertiary)?.also {
            setPaintColor(it)
            invalidate()
        }
    }

    init {
        paint.isAntiAlias = true
        paint2.isAntiAlias = true
        paint2.color = 0xffff5400.toInt()
        SkinManager.getSkinResource()?.getColor(R.color.skin_text_Tertiary)?.apply {
            setPaintColor(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        SkinManager.registerSkinChangedListener(onSkinChangedListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SkinManager.unregisterSkinChangedListener(onSkinChangedListener)

    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.drawRect(width.toFloat() / 2, 0f, width.toFloat(), height.toFloat(), paint)
        canvas?.drawRect(0f, 0f, width.toFloat() / 2, height.toFloat(), paint2)
    }


    private fun setPaintColor(color: Int) {
        paint.color = color
    }
}