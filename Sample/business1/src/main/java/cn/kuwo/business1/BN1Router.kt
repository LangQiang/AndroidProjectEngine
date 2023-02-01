package cn.kuwo.business1

import android.net.Uri
import cn.kuwo.business1.test.TestFragment
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation


/**
 * @author  GodQ
 * @date  2023/2/1 10:48 上午
 */
@DeepLink(path = "/bn/1")
class BN1Router: AbsRouter() {

    var testParam: String? = null

    override fun parse(p0: Uri?) {
        testParam = p0?.getQueryParameter("test_param")
    }

    override fun route(): Boolean {
        FragmentOperation.getInstance().showFullFragment(TestFragment.getInstance("from deeplink jump $testParam"))
        return true
    }
}