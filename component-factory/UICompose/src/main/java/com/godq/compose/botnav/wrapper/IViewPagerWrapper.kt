package com.godq.compose.botnav.wrapper

interface IViewPagerWrapper {
    val currentItem: Int
    fun addOnPageChangeListener(pageChangeDelegate: IPageChangeListener?)
}