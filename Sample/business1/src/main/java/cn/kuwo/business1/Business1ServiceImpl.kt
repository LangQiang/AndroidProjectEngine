package cn.kuwo.business1

import androidx.fragment.app.Fragment
import cn.kuwo.business1_api.IBusiness1Service
import cn.kuwo.business1.test.TestFragment
import timber.log.Timber

class Business1ServiceImpl: IBusiness1Service {

    override fun getBusiness1Fragment(): Fragment {
        //TestFragment 老演员了
        return TestFragment.getInstance("business1 fragment")
    }

    override fun huimieba() {
        Timber.tag("ProjectEngine").e("hui mie le")
    }
}