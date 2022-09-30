package com.godq.androidprojectengine

import com.godq.statisticwidget.histogram.IHistogramEntity

class HistogramEntity(private val histogramProgress:Float, private val text: String) :
    IHistogramEntity {


    override fun getHistogramProgress(totalLength: Float): Float {
        return histogramProgress
    }

    override fun getAbscissaText(): String {
        return text
    }
}