package cn.kuwo.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.kuwo.home.bottomNav.HomeBottomAdapter
import cn.kuwo.home.test.TestFragment
import com.godq.compose.botnav.BottomItemData
import com.godq.compose.botnav.BottomLayoutView
import com.godq.compose.botnav.wrapper.ViewPager2Wrapper
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IHostActivity
import com.lazylite.mod.fragmentmgr.OnFragmentStackChangeListener
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.permission.Permission
import com.lazylite.mod.utils.KwSystemSettingUtils


class MainActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null

    private var navHolder: View? = null

    private var bottomLayoutView: BottomLayoutView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        App.setMainActivity(this)
        setCustomTheme()
        setContentView(R.layout.activity_main)
        bindFragmentOperation()

        viewPager2 = findViewById(R.id.view_pager2)
        viewPager2?.isSaveEnabled = false

        bottomLayoutView= findViewById(R.id.bottom_layout)

//        decorateController.attach(findViewById(R.id.decorate_layer_container))

        val pairs: List<Pair<BottomItemData, Fragment>> = requestAdapterData()

        if (pairs.isNotEmpty()) {
            bottomLayoutView?.mAdapter = HomeBottomAdapter(pairs)
            bottomLayoutView?.bind(ViewPager2Wrapper(viewPager2))
            viewPager2?.offscreenPageLimit = 4
            setAdapter(pairs)
            bottomLayoutView?.setOnTabClickListener(object : BottomLayoutView.OnTabClickListener{
                override fun onClick(view: View,pos:Int) {
                    viewPager2?.setCurrentItem(pos, false)
                }
            })
        }

        findViewById<View>(R.id.test_deeplink_btn).setOnClickListener {
            //无需任何初始化以及除了base以外的依赖 可在任意组件中调用路由打开页面
            DeepLinkUtils.load("sample://open/bn/1?test_param=hahaha").execute()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Permission.onActivityResult(this,requestCode,resultCode,data)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permission.onRequestPermissionResult(this,requestCode,permissions,grantResults)
    }

    fun dealIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
            override fun call() {
                // TODO:  
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Permission.onActivityDestroy(this)

    }

    private fun setAdapter(fragments: List<Pair<BottomItemData, Fragment>>) {
        val mAdapter = HomePageAdapter(this, fragments)
        viewPager2?.isUserInputEnabled = false
        viewPager2?.offscreenPageLimit = 4
        viewPager2?.adapter = mAdapter
    }

    private fun requestAdapterData(): List<Pair<BottomItemData, Fragment>> {
        val data = ArrayList<Pair<BottomItemData, Fragment>>()
        data.add(Pair(BottomItemData("tab1", R.drawable.icon_friend,
            R.drawable.icon_friend_un
        ),  App1HomeLinkHelper.getBN1Fragment() ?: TestFragment.getInstance("1-1")
        ))

        data.add(Pair(BottomItemData("tab2",  R.drawable.icon_discover,
            R.drawable.icon_discover_un
        ), TestFragment.getInstance("1-2")
        ))

        data.add(Pair(BottomItemData("tab3",  R.drawable.icon_message,
            R.drawable.icon_message_un
        ), TestFragment.getInstance("1-3")
        ))

        data.add(Pair(BottomItemData("tab4",  R.drawable.icon_mine,
            R.drawable.icon_mine_un
        ), TestFragment.getInstance("1-4")
        ))

        return data
    }


    private fun setCustomTheme() {
        window.statusBarColor = Color.TRANSPARENT
        KwSystemSettingUtils.resetStatusBarBlack(this)
    }


    private val onFragmentStackChangeListener: OnFragmentStackChangeListener =
        object : OnFragmentStackChangeListener {

            override fun onPushFragment(top: Fragment?) {
                showMainLayer(true)//有动画，不要隐藏首页
            }

            override fun onPopFragment(top: Fragment?) {
                val curPageFragment = getCurPageFragment()
                val isStackEmpty = top == curPageFragment
                showMainLayer(isStackEmpty)
            }

            override fun onShowMainLayer(withBottom: Boolean) {
                showMainLayer(withBottom)
            }

            override fun onHideMainLayer(ishide: Boolean) {
            }
        }//

    private fun bindFragmentOperation() {
        FragmentOperation.getInstance().bind(this, object : IHostActivity {
            override fun onGetManLayerTopFragment(): Fragment? {
                return getCurPageFragment()
            }

            override fun containerViewId(): Int {
                return R.id.app_fragment_container
            }

        }, onFragmentStackChangeListener)
    }

    private fun showMainLayer(show:Boolean){
        if (show) {
            viewPager2?.visibility = View.VISIBLE
            bottomLayoutView?.visibility = View.VISIBLE
        } else {
            viewPager2?.visibility = View.INVISIBLE
            bottomLayoutView?.visibility = View.INVISIBLE
        }
    }

    private fun getCurPageFragment():Fragment?{
        val adapter = viewPager2?.adapter
        if (adapter is HomePageAdapter){
            return adapter.createFragment(viewPager2?.currentItem?:0)
        }
        return null
    }


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