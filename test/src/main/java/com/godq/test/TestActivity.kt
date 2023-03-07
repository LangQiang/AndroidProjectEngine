package com.godq.test

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        App.init(this.applicationContext)
        var mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        findViewById<View>(R.id.one_tv).setOnClickListener {
            startActivity(Intent(this, TwoActivity::class.java))
        }
        Timber.tag("aaa").e("xxxx")
        Timber.tag("aaa3").e("xxxx")
        Timber.tag("aa2a").e("xxxx")
        Timber.tag("a3aa").e("xxxx")
        Timber.tag("aa3a").e("xxxx")
        Timber.tag("aa3a").e("xxxx")
        Timber.tag("aaa2").e("xxxx")
        Timber.tag("a5aa").e("xxxx")
        Timber.tag("a4aa").e("xxxx")
        Timber.tag("aa3a").e("xxxx")
        Timber.tag("aa3a").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("a2aa").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa3a").e("xxxx")
        Timber.tag("aa4a").e("xxxx")
        Timber.tag("aa2a").e("xxxx")
        Timber.tag("a2aa").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa1ea").e("xxxx")
        Timber.tag("aa1e42a").e("xxxx")
        Timber.tag("aa142acf").e("xxxx")
        Timber.tag("aa14d2a").e("xxxx")
        Timber.tag("aa142a").e("xxxx")
        Timber.tag("aa142a").e("xxxx")
        Timber.tag("aa1a").e("xxxx")
        Timber.tag("aa23e1a").e("xxxx")
        Timber.tag("aawer1a").e("xxxx")
        Timber.tag("aaweer1a").e("xxxx")
        Timber.tag("aawder1a").e("xxxx")
        Timber.tag("aawxer1a").e("xxxx")
        Timber.tag("aaaewr1a").e("xxxx")
        Timber.tag("aa1fsa").e("xxxx")
        Timber.tag("aadf1a").e("xxxx")
        Timber.tag("21saaa").e("xxxx")

    }
}