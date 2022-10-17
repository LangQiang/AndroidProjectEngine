package com.godq.androidprojectengine

import com.lazylite.mod.http.mgr.ICommonParamProvider
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class TestHttp {

    val testScope = CoroutineScope(Job() + Dispatchers.Main)

    fun main() {
        testScope.launch {
            withContext(Dispatchers.IO) {
                KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet("http://150.158.55.208/account/list"))?.apply {
                    Timber.tag("http").e(dataToString())
                }
            }

            KwHttpMgr.getInstance().addCommonParamProvider(object : ICommonParamProvider{
                override fun getCommonHeads(): ConcurrentHashMap<String, String>? {
                    with(ConcurrentHashMap<String, String>()) {
                        put("header1", "test1")
                        put("header2", "test2")
                        return this
                    }
                }

                override fun getCommonQueryParams(): ConcurrentHashMap<String, String>? {
                    return null
                }

                override fun providerName(): String {
                    return "test-provider"
                }

            })
            withContext(Dispatchers.IO) {
                KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet("http://150.158.55.208/account/list"))?.apply {
                    Timber.tag("http").e(dataToString())

                }
            }
            Timber.tag("http").e(KwHttpMgr.printCommonParamInfo())

        }
    }
}