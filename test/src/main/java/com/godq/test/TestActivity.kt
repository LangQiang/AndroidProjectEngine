package com.godq.test

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.godq.test.sideslidewidget.SideSlideMenuActivity
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        App.init(this.applicationContext)
        var mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
//        findViewById<View>(R.id.one_tv).setOnClickListener {
//            startActivity(Intent(this, TwoActivity::class.java))
//        }
        Timber.tag("aaa").e("1")
        Timber.tag("bbb").e("2")
        Timber.tag("ccc").e("3")
        this.runOnUiThread {
            Timber.tag("aaa").e("哈哈哈哈")
        }

        findViewById<View>(R.id.side_slide_btn).setOnClickListener {
            startActivity(Intent(this, SideSlideMenuActivity::class.java))
        }
    }
}