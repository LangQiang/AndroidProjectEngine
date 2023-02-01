package cn.kuwo.bussness2

import android.content.Context
import android.util.Pair
import cn.kuwo.business2_api.IBusiness2Service
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init
import com.lazylite.mod.App
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2022/12/2 3:31 下午
 */
@AutoInit(moduleName = "business2", dependOn = ["business1"])
class Business2Init : Init() {
    override fun init(context: Context?) {
        context?.also {
            if (App.isMainProcess()) {
                Timber.tag("ProjectEngine").e("Business2Init init")
            }
        }
    }

    override fun initAfterAgreeProtocol(context: Context?) {
    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IBusiness2Service::class.java.name, Business2ServiceImpl())
    }
}