package com.godq.compose.botnav.wrapper

import androidx.viewpager.widget.ViewPager

class ViewPagerWrapper(private val viewpager: ViewPager?): IViewPagerWrapper {
    override val currentItem: Int
        get() {
            return viewpager?.currentItem ?: 0
        }

    override fun addOnPageChangeListener(pageChangeDelegate: IPageChangeListener?) {
        viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                pageChangeDelegate?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                pageChangeDelegate?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                pageChangeDelegate?.onPageScrollStateChanged(state)
            }

        })
    }
}