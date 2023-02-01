package cn.kuwo.home

import androidx.fragment.app.Fragment
import cn.kuwo.business1_api.IBusiness1Service
import cn.kuwo.business2_api.IBusiness2Service
import com.lazylite.bridge.router.ServiceImpl


/**
 * @author  GodQ
 * @date  2023/1/31 7:15 下午
 */
object App1HomeLinkHelper {

    private var business1Service: IBusiness1Service? = null
    private var business2Service: IBusiness2Service? = null

    init {
        business1Service = ServiceImpl.getInstance().getService(IBusiness1Service::class.java.name) as? IBusiness1Service
        business2Service = ServiceImpl.getInstance().getService(IBusiness2Service::class.java.name) as? IBusiness2Service
    }

    fun getBN1Fragment(): Fragment? {
        return business1Service?.getBusiness1Fragment()
    }

    fun getBN2Info(): String? {
        return business2Service?.getBusiness2Info()
    }
}