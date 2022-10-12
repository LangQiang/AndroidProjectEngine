package com.godq.compose.botnav.wrapper

import androidx.viewpager2.widget.ViewPager2

class ViewPager2Wrapper(private val viewpager2: ViewPager2?): IViewPagerWrapper {
    override val currentItem: Int
        get() {
            return viewpager2?.currentItem ?: 0
        }

    override fun addOnPageChangeListener(pageChangeDelegate: IPageChangeListener?) {
        viewpager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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