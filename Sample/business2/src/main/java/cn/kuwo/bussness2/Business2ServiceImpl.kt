package cn.kuwo.bussness2

import cn.kuwo.business2_api.IBusiness2Service

class Business2ServiceImpl: IBusiness2Service {

    override fun getBusiness2Info(): String {
        return "business2"
    }

}