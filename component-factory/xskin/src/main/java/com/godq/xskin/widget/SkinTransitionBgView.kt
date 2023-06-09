package com.godq.xskin.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart


/**
 * @author  GodQ
 * @date  2023/6/9 11:08 AM
 */
class SkinTransitionBgView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attributeSet, defStyleAttr) {
    private val bgView1: View = View(context)
    private val bgView2: View = View(context)
    private val bgViews = listOf(bgView1, bgView2)
    private var currentBgSetIndex = 0

    private var nextAnim: ObjectAnimator? = null
    init {
        addView(bgView1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        addView(bgView2, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun setBackground(background: Drawable?) {
        try {
            val nextIndex = (currentBgSetIndex + 1) % 2
            val current = bgViews[currentBgSetIndex]
            val next = bgViews[nextIndex]
            next.bringToFront()

            nextAnim?.removeAllListeners()
            nextAnim?.cancel()
            current.visibility = VISIBLE
            current.alpha = 1f
            next.visibility = INVISIBLE
            next.background = background
            currentBgSetIndex = nextIndex


            if (nextAnim == null) {
                nextAnim = ObjectAnimator.ofFloat(next, View.ALPHA, 0f, 1f)
            }
            nextAnim?.doOnStart {
                next.visibility = VISIBLE
            }
            nextAnim?.doOnEnd {
                current.visibility = INVISIBLE
            }
            nextAnim?.target = next
            nextAnim?.duration = if (current.background == null) 0 else 300
            nextAnim?.start()
        } catch (e: Exception) {
            //
        }
    }
}