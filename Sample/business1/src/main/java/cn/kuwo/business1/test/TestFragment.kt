package cn.kuwo.business1.test

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.kuwo.business1.BN1LinkHelper
import cn.kuwo.business1.R
import cn.kuwo.business1_api.IBusiness1Service
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.toast.KwToast
import com.lazylite.mod.widget.BaseFragment


/**
 * @author  GodQ
 * @date  2023/1/18 6:34 下午
 */
class TestFragment: BaseFragment() {

    var pageId: String = "0"

    companion object {
        fun getInstance(pageId: String?): TestFragment {
            val fragment = TestFragment()
            fragment.pageId = pageId ?: "0"
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val textView = TextView(inflater.context)
        textView.gravity = Gravity.CENTER
        textView.textSize = 20f
        textView.setTextColor(0xff000000.toInt())
        textView.text = pageId

        textView.setOnClickListener {
            MessageManager.getInstance().asyncNotify(IBusiness1Service.IBN1Observer.EVENT_ID, object : MessageManager.Caller<IBusiness1Service.IBN1Observer>() {
                override fun call() {
                    ob.testEvent("bn1 send event")
                }
            })
        }
        with(RelativeLayout(inflater.context)) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundResource(R.color.white)
            gravity = Gravity.CENTER

            val frameLayout = FrameLayout(inflater.context)
            addView(textView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            frameLayout.addView(this)
            return frameLayout
        }

    }
}