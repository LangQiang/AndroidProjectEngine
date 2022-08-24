package com.lazylite.mod.utils

import android.os.Looper
import com.lazylite.mod.messagemgr.MessageManager
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object CountDownUtil {
    /*private val lock = Object()
    private val TAG = "CountDownUtil"*/

    private const val intervalTime = 1000

    private val allItemForCountDownInMainThread = HashSet<MyItems>()

    //为了在UI上及时显示倒计时，必须在主线程，不要添加大批量数据进来，否则会卡UI线程，如果列表中数据多可以考虑在ItemView显示时添加进来，在ItemView移除屏幕时remove掉
    private var countDownTimerInMainThread: KwTimer? = null

    //CountDown in mainThread
    fun addItems(tag: String, vararg items: Item) {
        addItems(tag, items.asList())
    }

    //CountDown in mainThread
    fun addItems(tag: String, items: List<Item>) {
        if (items.isEmpty()) {
            return
        }
        val internalList = ArrayList<Item>()
        internalList.addAll(items)
        MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
            override fun call() {
                val iterator = allItemForCountDownInMainThread.iterator()
                while (iterator.hasNext()) {
                    val myItems = iterator.next()
                    if (tag == myItems.tag) {
                        myItems.items.clear()
                        for (element in internalList) {
                            myItems.items.add(WeakReference(element))
                        }
                        return
                    }
                    val isMyItemsEmpty = checkMyItemEmpty(myItems)
                    if (isMyItemsEmpty) {
                        iterator.remove()
                    }
                }
                val myItems = MyItems(tag)
                for (element in internalList) {
                    myItems.items.add(WeakReference(element))
                }
                allItemForCountDownInMainThread.add(myItems)
                startMainThreadTimer()
            }
        })
    }

    //remove mainThread countDownItem
    fun removeItems(tag: String) {
        MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
            override fun call() {
                val iterator = allItemForCountDownInMainThread.iterator()
                while (iterator.hasNext()) {
                    val myItems = iterator.next()
                    if (tag == myItems.tag) {
                        iterator.remove()
                        return
                    }
                }
                if (allItemForCountDownInMainThread.isEmpty()) {
                    stopCountDownTimer(countDownTimerInMainThread)
                }
            }
        })
    }

    private fun startMainThreadTimer() {
        if (countDownTimerInMainThread == null) {
            countDownTimerInMainThread = KwTimer {
                val allIterator = allItemForCountDownInMainThread.iterator()
                testLog("countDownTimerTick")
                while (allIterator.hasNext()) {
                    val myItems = allIterator.next()
                    val isMyItemsEmpty = checkMyItemEmpty(myItems)
                    if (isMyItemsEmpty) {
                        allIterator.remove()
                        continue
                    }
                    //
                    val itemIterator = myItems.items.iterator()
                    var needUpdateSize = 0
                    while (itemIterator.hasNext()) {
                        val aItem = itemIterator.next().get()
                        aItem?.apply {
                            val needUpdate = this.update()
                            if (needUpdate) {
                                needUpdateSize++
                            } else {
                                itemIterator.remove()
                            }
                        }
                    }
                    if (needUpdateSize == 0) {//不再更新了
                        allIterator.remove()
                    }
                }

                if (allItemForCountDownInMainThread.isEmpty()) {
                    stopCountDownTimer(countDownTimerInMainThread)
                }
            }
            countDownTimerInMainThread?.start(intervalTime)
            testLog("countDownTimerTick - start")
        } else {
            countDownTimerInMainThread?.let {
                if (!it.isRunnig) {
                    it.start(intervalTime)
                    testLog("countDownTimerTick - start")
                }
            }
        }
    }

    //
    /*private val allItemForCountDownInThread = Collections.synchronizedSet(HashSet<MyItems>())
    private val countDownTimerInThread = ThreadLocal<KwTimer>()
    private var timerThread: HandlerThread? = null

    fun addAsyncItems(tag: String, items: List<Item>) {
        if (items.isEmpty()) {
            return
        }
        val _items = ArrayList<Item>()
        _items.addAll(items)
        synchronized(lock) {
            val iterator = allItemForCountDownInThread.iterator()
            while (iterator.hasNext()) {
                val myItems = iterator.next()
                if (tag == myItems.tag) {
                    myItems.items.clear()
                    for (element in _items) {
                        myItems.items.add(WeakReference(element))
                    }
                    return
                }
                val isMyItemsEmpty = checkMyItemEmpty(myItems)
                if (isMyItemsEmpty) {
                    iterator.remove()
                }
            }
            val myItems = MyItems(tag)
            for (element in _items) {
                myItems.items.add(WeakReference(element))
            }
            allItemForCountDownInThread.add(myItems)
        }
        if (null == timerThread) {
            timerThread = object : HandlerThread("CountDownUtil") {
                override fun onLooperPrepared() {
                    starThreadTimer()
                }
            }
            timerThread?.start()
        }
    }

    fun removeAsyncItems(tag: String) {
        synchronized(lock) {
            val iterator = allItemForCountDownInThread.iterator()
            while (iterator.hasNext()) {
                val myItems = iterator.next()
                if (tag == myItems.tag) {
                    iterator.remove()
                    return
                }
            }
            if (allItemForCountDownInThread.isEmpty()) {
                val quitThread = timerThread
                timerThread = null
                quitThread?.apply {
                    quitHandlerThread(this)
                }
            }
        }
    }

    private fun starThreadTimer() {
        var timer = countDownTimerInThread.get()
        if (timer == null) {
            timer = KwTimer {
                synchronized(lock) {
                    val allIterator = allItemForCountDownInThread.iterator()
                    testLog("countDownTimerTick")
                    while (allIterator.hasNext()) {
                        val myItems = allIterator.next()
                        val isMyItemsEmpty = checkMyItemEmpty(myItems)
                        if (isMyItemsEmpty) {
                            allIterator.remove()
                            continue
                        }
                        //
                        val itemIterator = myItems.items.iterator()
                        var needUpdateSize = 0
                        while (itemIterator.hasNext()) {
                            val aItem = itemIterator.next().get()
                            if (null == aItem) {
                                itemIterator.remove()
                                continue
                            }
                            //判断主线程timer中是否包含了此Item，如果包含了就忽略，防止重复update
                            try {
                                val mainTimerItemsIterator = allItemForCountDownInMainThread.iterator()
                                while (mainTimerItemsIterator.hasNext()) {
                                    val aMainItems = mainTimerItemsIterator.next()
                                    val aMainItemIterator = aMainItems.items.iterator()
                                    while (aMainItemIterator.hasNext()){
                                        val aMainItem = aMainItemIterator.next()
                                        if (aItem.equals(aMainItem)) {
                                            continue
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                            }//由于多线程可能出现异常，这里直接cache就好，大不了重复update下

                            val needUpdate = aItem.update()
                            if (needUpdate) {
                                needUpdateSize++
                            } else {
                                itemIterator.remove()
                            }
                        }
                        if (needUpdateSize == 0) {//不再更新了
                            allIterator.remove()
                        }
                    }
                    if (allItemForCountDownInThread.isEmpty()) {
                        quitHandlerThread(Thread.currentThread() as HandlerThread)
                    }
                }
            }
            countDownTimerInThread.set(timer)
            timer.start(400)
            testLog("countDownTimerTick - start")
        } else {
            if (!timer.isRunnig) {
                timer.start(400)
                testLog("countDownTimerTick - start")
            }
        }
    }

    private fun quitHandlerThread(thread: HandlerThread) {
        synchronized(lock){
            val looper = thread.looper
            looper ?: return
            val handler = object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    val timer = countDownTimerInThread.get()
                    stopCountDownTimer(timer)
                    timerThread?.quitSafely()
                }
            }
            handler.sendEmptyMessage(1)
        }
    }*/

    private fun checkMyItemEmpty(myItems: MyItems): Boolean {
        val items = myItems.items
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val ref = iterator.next()
            if (null == ref.get()) {
                iterator.remove()
            }
        }
        return items.isEmpty()
    }

    private fun stopCountDownTimer(kwTimer: KwTimer?) {
        testLog("countDownTimerTick - stop")
        kwTimer?.stop()
    }

    private fun testLog(msg: String) {
        if (AppInfo.IS_DEBUG) {
//            Log.e(TAG,msg)
        }
    }

    interface Item {
        fun update(): Boolean
    }//

    class MyItems(val tag: String) {
        val items: MutableList<WeakReference<Item>> = ArrayList()
    }//
}