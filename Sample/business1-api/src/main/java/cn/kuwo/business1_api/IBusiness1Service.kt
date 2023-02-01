package cn.kuwo.business1_api

import androidx.fragment.app.Fragment
import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase


/**
 * @author  GodQ
 * @date  2023/1/31 7:04 下午
 */
interface IBusiness1Service {
    fun getBusiness1Fragment(): Fragment
    fun huimieba()

    interface IBN1Observer: IObserverBase {
        companion object {
            @JvmStatic
            val EVENT_ID = EventId { IBN1Observer::class.java }
        }

        fun testEvent(event: String)
    }
}