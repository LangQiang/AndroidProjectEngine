package com.godq.test

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.godq.test.sideslidewidget.SideSlideMenuFragment
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
        bindFragmentOperation()
    }

    private fun bindFragmentOperation() {
        FragmentOperation.getInstance().bind(this, object : IHostActivity {
            override fun onGetManLayerTopFragment(): Fragment? {
                return null
            }

            override fun containerViewId(): Int {
                return R.id.app_fragment_container
            }

        }, onFragmentStackChangeListener)
    }

    private val onFragmentStackChangeListener: OnFragmentStackChangeListener =
        object : OnFragmentStackChangeListener {

            override fun onPushFragment(top: Fragment?) {
//                showMainLayer(true)//有动画，不要隐藏首页
            }

            override fun onPopFragment(top: Fragment?) {
//                val curPageFragment = getCurPageFragment()
//                val isStackEmpty = top == curPageFragment
//                showMainLayer(isStackEmpty)
            }

            override fun onShowMainLayer(withBottom: Boolean) {
//                showMainLayer(withBottom)
            }

            override fun onHideMainLayer(ishide: Boolean) {
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