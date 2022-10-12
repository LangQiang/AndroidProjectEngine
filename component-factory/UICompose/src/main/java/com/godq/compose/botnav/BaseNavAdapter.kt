package com.godq.compose.botnav

import android.view.View

interface BaseNavAdapter {
    fun getWeight(): Float
    fun getItem(container:View, position: Int): View
    fun getCount(): Int
}