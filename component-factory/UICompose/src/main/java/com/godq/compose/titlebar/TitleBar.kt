package com.godq.compose.titlebar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_compose_title_bar, this, true)
        backContainer = findViewById(R.id.back_container)
        backIv = findViewById(R.id.back_iv)
        titleTv = findViewById(R.id.title_tv)
        menuContainer = findViewById(R.id.menu_container)
        menuTv = findViewById(R.id.menu_tv)
        menuIv = findViewById(R.id.menu_iv)

        setResDelegate(delegate)
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

    fun setBackClickListener(onClickListener: OnClickListener) {
        backContainer?.setOnClickListener(onClickListener)
    }

    fun setMenuClickListener(onClickListener: OnClickListener) {
        menuContainer?.setOnClickListener(onClickListener)
    }

    fun showMenu(showMenuText: Boolean, showMenuIcon: Boolean) {
        if (showMenuText) {
            menuTv?.visibility = VISIBLE
            return
        }
        if (showMenuIcon) {
            menuIv?.visibility = VISIBLE
            return
        }
    }

    fun notifyStyleChanged() {
        setBackIcon(delegate?.getBackIcon())
        setTitleColor(delegate?.getTitleColor())
        setMenuIcon(delegate?.getMenuIcon())
        setMenuTextColor(delegate?.getMenuTextColor())
        setTitleBackground(delegate?.getBackground())
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

    companion object {
        var globalResDelegate: AbsTitleBarResDelegate? = null
    }

}