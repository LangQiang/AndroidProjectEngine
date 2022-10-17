package com.godq.androidprojectengine

import android.os.Bundle
import android.os.SystemClock
import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.godq.compose.UICompose
import com.godq.compose.UIComposeConfig
import com.godq.compose.botnav.BottomItemData
import com.godq.compose.botnav.BottomLayoutView
import com.godq.compose.botnav.BottomNavAdapter
import com.godq.compose.botnav.wrapper.ViewPagerWrapper
import com.godq.deeplink.DeepLinkConfig
import com.godq.deeplink.DeepLinkUtils
import com.godq.deeplink.inject.IExecutor
import com.godq.threadpool.TASK_MODE_DEFAULT
import com.godq.threadpool.TASK_MODE_IO
import com.godq.threadpool.ThreadPool
import com.lazylite.bridge.init.ComponentInit
import com.lazylite.mod.global.BaseConfig
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.MainScope
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class MainActivity : AppCompatActivity() {

    private var bottomLayoutView: BottomLayoutView? = null
    private var vp: ViewPager? = null


    var countMark = AtomicLong(0)
    var finalCount = AtomicLong(0)
    var exit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mainScope = MainScope()
        with(UIComposeConfig()) {
            this.fontAssetsPath = "fonts/iconfont.ttf"
            UICompose.init(this@MainActivity, this)
        }
        setContentView(R.layout.activity_main)
        bottomLayoutView= findViewById(R.id.bottom_layout)
        vp= findViewById(R.id.vp)

        val pairs: ArrayList<Pair<BottomItemData, Fragment>> = ArrayList()
        pairs.add(Pair(BottomItemData("first",R.string.bottom_index_select_icon,
            R.string.bottom_index_normal_icon), Fragment()))
        pairs.add(Pair(BottomItemData("last",R.string.bottom_mine_select_icon,
            R.string.bottom_mine_normal_icon), Fragment()))

        if (!pairs.isNullOrEmpty()) {
            bottomLayoutView?.mAdapter = BottomNavAdapter(pairs)
            bottomLayoutView?.bind(ViewPagerWrapper(vp))
            val mAdapter = HomePageAdapter(supportFragmentManager, pairs)
            vp?.offscreenPageLimit = 4
            vp?.adapter = mAdapter
            bottomLayoutView?.setOnTabClickListener(object :BottomLayoutView.OnTabClickListener{
                override fun onClick(view: View,pos:Int) {
                    vp?.currentItem = pos
                }

            })
        }

        val baseConfig = BaseConfig()
        baseConfig.allowProxy = true
        baseConfig.deepLinkScheme = "test"
        ComponentInit.initOnAppCreate(this.applicationContext, baseConfig)
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://kuwo.cn"), null)

        findViewById<View>(R.id.btn1).setOnClickListener {
            Timber.tag("lqthreadpool").e(ThreadPool.getCurrentSchedulerInfo())
            DeepLinkUtils.load("test://open/test").execute()
//            exit = true

//            GlobalScope.launch(Dispatchers.IO) {
//                Timber.tag("lqthreadpool").e("run-Default ${Thread.currentThread().name}")
//            }
        }
//        for (i in 0 .. 100) {
//            mainScope.launch {
//                withContext(Dispatchers.IO) {
//                    Log.e("test", i.toString() + " IO  " + Thread.currentThread().name)
//                    SystemClock.sleep(10000)
//
//                }
//
//            }
//
//        }
//
//        window.decorView.postDelayed({
//            for (i in 0 .. 20) {
//                mainScope.launch {
//                    withContext(Dispatchers.Default) {
//                        Log.e("test", i.toString() + "  Default " + Thread.currentThread().name)
//                        SystemClock.sleep(5000)
//                    }
//                }
//            }
//        }, 1000)

        var dView = window.decorView

//        for (i in 0 ..40) {
//            dView.postDelayed({
//                ThreadPool.exec(TASK_MODE_DEFAULT) {
//                    Timber.tag("lqthreadpool").e("run-[CPU]-${i} ${Thread.currentThread().name}")
//                    SystemClock.sleep(1000)
//                }
//            }, i * 2000L * 0)
//
//        }
//
//        for (i in 0 ..40) {
//            dView.postDelayed({
//                ThreadPool.exec(TASK_MODE_IO) {
//                    Timber.tag("lqthreadpool").e("run-[IO]-${i} ${Thread.currentThread().name}")
//                    SystemClock.sleep(1000)
//                }
//            }, i * 2000L * 0)
//
//        }
//        Thread{
//            while (!exit) {
//                dView.postDelayed({
//                    ThreadPool.run(TASK_MODE_DEFAULT) {
//                        val index = countMark.incrementAndGet()
//                        Timber.tag("lqthreadpool").e("run-${index} ${Thread.currentThread().name}")
//                        finalCount.incrementAndGet()
//                        SystemClock.sleep(1000)
//                    }
//                }, Random.nextInt(10) * 1000L)
//            }
//        }.start()



//        var pool = Executors.newCachedThreadPool()
//
//        for (i in 0..10) {
//            pool.submit {
//                Timber.tag("threadpool").e("run-${i} ${Thread.currentThread().name}")
//            }
//        }


        HistogramTest.init(findViewById(R.id.histogram_view))
        TestHttp().main()
    }
}