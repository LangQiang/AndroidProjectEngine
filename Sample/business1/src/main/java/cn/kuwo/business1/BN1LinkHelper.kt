package cn.kuwo.business1

import cn.kuwo.business2_api.IBusiness2Service
import com.lazylite.bridge.router.ServiceImpl


/**
 * @author  GodQ
 * @date  2023/1/31 7:15 下午
 */
object BN1LinkHelper {

    private var business2Service: IBusiness2Service? = null

    init {
        business2Service = ServiceImpl.getInstance().getService(IBusiness2Service::class.java.name) as? IBusiness2Service
    }


    fun getBN2Infooooooo(): String? {
        return business2Service?.getBusiness2Info()
    }
}