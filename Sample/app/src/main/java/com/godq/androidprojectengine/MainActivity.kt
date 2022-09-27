package com.godq.androidprojectengine

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.godq.deeplink.DeepLinkConfig
import com.godq.deeplink.DeepLinkUtils
import com.godq.deeplink.inject.IExecutor
import com.godq.threadpool.TASK_MODE_DEFAULT
import com.godq.threadpool.TASK_MODE_IO
import com.godq.threadpool.ThreadPool
import com.lazylite.mod.global.CommonInit
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.MainScope
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class MainActivity : AppCompatActivity() {

    var countMark = AtomicLong(0)
    var finalCount = AtomicLong(0)
    var exit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = DeepLinkConfig()
        config.schemeName = "test"
        config.iExecutor = IExecutor {
            ThreadPool.exec {
                it.run()
            }
        }
        DeepLinkUtils.init(config)
        var mainScope = MainScope()
        setContentView(R.layout.activity_main)
        CommonInit.initOnAppCreate(this.applicationContext)
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




    }
}