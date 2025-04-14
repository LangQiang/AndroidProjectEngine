package com.godq.compose.titlebar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.godq.compose.R


/**
 * @author  GodQ
 * @date  2023/6/8 3:14 PM
 */
class TitleBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attributeSet, defStyleAttr) {

    private var delegate: AbsTitleBarResDelegate? = globalResDelegate?: TitleBarDefaultResDelegate(context)

    private var backContainer: View? = null
    private var backIv: ImageView? = null
    private var titleTv: TextView? = null
    private var menuContainer: View? = null
    private var menuTv: TextView? = null
    private var menuIv: ImageView? = null
    private var divideLine: View? = null
    private var contentContainer: LinearLayout? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_compose_title_bar, this, true)
        backContainer = findViewById(R.id.back_container)
        backIv = findViewById(R.id.back_iv)
        titleTv = findViewById(R.id.title_tv)
        menuContainer = findViewById(R.id.menu_container)
        menuTv = findViewById(R.id.menu_tv)
        menuIv = findViewById(R.id.menu_iv)
        divideLine = findViewById(R.id.title_bottom_divide_line)
        contentContainer = findViewById(R.id.content_container)

        setResDelegate(delegate)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        delegate?.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate?.onDetach()
    }

    fun setResDelegate(delegate: AbsTitleBarResDelegate?) {
        this.delegate = delegate
        notifyStyleChanged() //这里可以记录快照，每次对比实现局部更新
    }

    fun setTitle(title: String) {
        titleTv?.text = title
    }

    fun setMenuTitle(menuStr: String) {
        menuTv?.text = menuStr
        menuTv?.visibility = VISIBLE
    }

    fun setHorizontalMarginExceptDivideLine(@Px margin: Int) {
        //不包含divideLine
        val layoutParams = (contentContainer?.layoutParams as? LayoutParams)
        layoutParams?.setMargins(margin, marginTop, margin, marginBottom)
        contentContainer?.layoutParams = layoutParams
    }

    fun setDivideLineHorizontalMargin(@Px margin: Int) {
        val layoutParams = (divideLine?.layoutParams as? LayoutParams)
        layoutParams?.setMargins(margin, marginTop, margin, marginBottom)
        divideLine?.layoutParams = layoutParams
    }

    fun setDivideLineHeight(@Px height: Int) {
        val params = divideLine?.layoutParams
        params?.height = height
        divideLine?.layoutParams = params
    }

    fun setBackClickListener(onClickListener: OnClickListener) {
        backContainer?.setOnClickListener(onClickListener)
    }

    fun setMenuClickListener(onClickListener: OnClickListener) {
        menuContainer?.setOnClickListener(onClickListener)
    }

    @JvmOverloads
    fun setVisible(
        showBack: Boolean? = null,
        showTitle: Boolean? = null,
        showMenuText: Boolean? = null,
        showMenuIcon: Boolean? = null,
        showDivideLine: Boolean? = null,
    ) {
        if (showBack != null) {
            backContainer?.visibility = if (showBack) VISIBLE else INVISIBLE
        }
        if (showTitle != null) {
            titleTv?.visibility = if (showTitle) VISIBLE else INVISIBLE
        }
        if (showMenuText != null) {
            menuTv?.visibility = if (showMenuText) VISIBLE else INVISIBLE
        }
        if (showMenuIcon != null) {
            menuIv?.visibility = if (showMenuIcon) VISIBLE else INVISIBLE
        }
        if (showDivideLine != null) {
            divideLine?.visibility = if (showDivideLine) VISIBLE else INVISIBLE
        }
    }

    fun notifyStyleChanged() {
        setBackIcon(delegate?.getBackIcon())
        setTitleColor(delegate?.getTitleColor())
        setMenuIcon(delegate?.getMenuIcon())
        setMenuTextColor(delegate?.getMenuTextColor())
        setTitleBackground(delegate?.getBackground())
        setDivideLineBackground(delegate?.getDivideLineColor())
    }

    /*******   设置titleBar的样式     *******/

    //back
    private fun setBackIcon(drawable: Drawable?) {
        drawable?: return
        backIv?.setImageDrawable(drawable)
    }

    //title
    private fun setTitleColor(color: Int?) {
        color?: return
        titleTv?.setTextColor(color)
    }

    //menu
    private fun setMenuIcon(drawable: Drawable?) {
        drawable?: return
        menuIv?.setImageDrawable(drawable)
    }

    private fun setMenuTextColor(color: Int?) {
        color?: return
        menuTv?.setTextColor(color)
    }

    //bg
    private fun setTitleBackground(drawable: Drawable?) {
        drawable?: return
        background = drawable
    }

    private fun setDivideLineBackground(color: Int?) {
        color?: return
        divideLine?.setBackgroundColor(color)
    }

    companion object {
        var globalResDelegate: AbsTitleBarResDelegate? = null
    }

}