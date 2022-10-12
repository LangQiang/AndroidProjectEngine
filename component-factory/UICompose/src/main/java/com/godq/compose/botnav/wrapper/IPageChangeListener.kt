package com.godq.compose.botnav.wrapper

interface IPageChangeListener {
    fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    )
    fun onPageSelected(position: Int)
    fun onPageScrollStateChanged(state: Int)
}