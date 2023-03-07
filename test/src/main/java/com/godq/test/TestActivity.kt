package com.godq.test

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

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


    }
}