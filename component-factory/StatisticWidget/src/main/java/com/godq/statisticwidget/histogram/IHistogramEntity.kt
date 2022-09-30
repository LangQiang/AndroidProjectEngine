package com.godq.statisticwidget.histogram

interface IHistogramEntity {

    /**
     * 柱子长度
     * */
    fun getHistogramProgress(totalLength: Float): Float

    /**
     * 横坐标文案
     * */
    fun getAbscissaText(): String
}