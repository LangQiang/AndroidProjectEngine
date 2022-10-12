package com.godq.compose.botnav

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.godq.compose.botnav.wrapper.IPageChangeListener
import com.godq.compose.botnav.wrapper.IViewPagerWrapper

class BottomLayoutView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    var mAdapter: BaseNavAdapter? = null
    set(value) {
        field = value
        addNewView(mAdapter)
    }

    private var mViewpager: IViewPagerWrapper? = null
    private var mTabClickListener: OnTabClickListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    fun setOnTabClickListener(listener: OnTabClickListener){
        mTabClickListener = listener
    }

    fun bind(viewPager: IViewPagerWrapper?) {
        this.mViewpager = viewPager
        viewPager?.addOnPageChangeListener(object : IPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        onSelected(viewPager?.currentItem ?: 0)
    }

    private fun addNewView(adapter: BaseNavAdapter?) {

        adapter ?: return

        removeAllViews()

        for (i in 0 until adapter.getCount()) {
            val child = adapter.getItem(this, i)
            val layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, adapter.getWeight())
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            addView(child, layoutParams)
            child.setOnClickListener {
                mTabClickListener?.onClick(child,i)
            }
        }

    }

    private fun onSelected(position: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as? ITabView)?.onSelected(i == position)
        }
    }

    interface OnTabClickListener{
        fun onClick(view: View,pos:Int)
    }
}