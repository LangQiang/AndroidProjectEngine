package com.godq.test

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.godq.test.sideslidewidget.SideSlideMenuFragment
import com.godq.test.skin.SkinTestFragment
import com.godq.xskin.SkinActivity
import com.godq.xskin.SkinManager
import com.lazylite.bridge.init.ComponentInit
import com.lazylite.mod.App
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.fragmentmgr.StartParameter
import timber.log.Timber

class TestActivity : SkinActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInit.initOnAppCreate(this.application, null)
        App.setMainActivity(this) //垃圾代码

        SkinManager.init(this.application)
        val key = ConfMgr.getStringValue("", "skin", "")
        if (!key.isNullOrEmpty()) {
            SkinManager.loadSkin(key)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Timber.tag("aaa").e("1")
        Timber.tag("bbb").e("2")
        Timber.tag("ccc").e("3")
        this.runOnUiThread {
            Timber.tag("aaa").e("哈哈哈哈")
        }

        findViewById<View>(R.id.side_slide_btn).setOnClickListener {
            FragmentOperation.getInstance().showFullFragment(SideSlideMenuFragment())
        }

        findViewById<View>(R.id.skin_btn).setOnClickListener {
            FragmentOperation.getInstance().showFullFragment(SkinTestFragment())
        }
        bindFragmentOperation()

    }

    private fun bindFragmentOperation() {

        FragmentOperation.getInstance().bind(this, true, object : IHostActivity {
            override fun containerViewId(): Int {
                return R.id.app_fragment_container
            }

            override fun onShowMainLayer(show: Boolean) {
            }

            override fun onGetMainLayerTopFragment(): Fragment? {
                return null
            }
        }, onFragmentStackChangeListener)
    }

    private val onFragmentStackChangeListener: OnFragmentStackChangeListener =
        object : OnFragmentStackChangeListener {
            override fun onPushFragment(top: Fragment?, startParameter: StartParameter?) {

            }

            override fun onPopFragment(nowTop: Fragment?) {
            }

        }//

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (FragmentOperation.getInstance().onKeyDown(keyCode, event)) {
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!FragmentOperation.getInstance().close()) {
                try {
                    moveTaskToBack(true)
                } catch (ignore: Exception) {
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}