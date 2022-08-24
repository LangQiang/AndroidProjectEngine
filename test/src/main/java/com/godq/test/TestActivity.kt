package com.godq.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lazylite.mod.App

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        App.init(this.applicationContext)
    }
}