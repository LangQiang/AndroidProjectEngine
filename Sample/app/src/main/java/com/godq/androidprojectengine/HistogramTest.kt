package com.godq.androidprojectengine

import com.godq.statisticwidget.histogram.HistogramView
import com.godq.statisticwidget.histogram.IHistogramEntity
import kotlin.random.Random

object HistogramTest {
    fun init(histogramView: HistogramView) {
        var data = ArrayList<IHistogramEntity>()
        val random = Random(System.currentTimeMillis())
        for (i in 1 .. 20) {
            data.add(HistogramEntity(i * 50f, i.toString()))
        }
        histogramView.data = data
     }
}