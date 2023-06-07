package com.godq.test.skin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.godq.test.R
import com.godq.test.TwoActivity
import com.godq.xskin.SkinConstants
import com.godq.xskin.SkinManager
import com.godq.xskin.entity.SkinViewWrapper
import com.godq.xskin.load.SkinLoadCallback
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.widget.BaseFragment
import com.lazylite.mod.widget.loading.LoadingDialogMgr


/**
 * @author  GodQ
 * @date  2023/5/30 4:32 PM
 */
class SkinTestFragment: BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_skin_text_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.skin_1).setOnClickListener {
            val url = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/skinresapk-debug_2.apk"
            SkinManager.loadSkin(url, callback = object : SkinLoadCallback{

                override fun onProgress(progress: Float) {
                    if (progress != 1f) {
                        LoadingDialogMgr.showProcess("progress: $progress")
                    }
                }

                override fun onFinish(success: Boolean) {
                    LoadingDialogMgr.hideProcess()
                    if (success) {
                        ConfMgr.setStringValue("", "skin", url, false)
                    }
                }
            })
        }
        view.findViewById<View>(R.id.skin_2).setOnClickListener {
            val url = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/skinresapk-debug-3.apk"
            SkinManager.loadSkin(url, callback = object : SkinLoadCallback{

                override fun onProgress(progress: Float) {
                    if (progress != 1f) {
                        LoadingDialogMgr.showProcess("progress: $progress")
                    }
                }

                override fun onFinish(success: Boolean) {
                    LoadingDialogMgr.hideProcess()
                    if (success) {
                        ConfMgr.setStringValue("", "skin", url, false)
                    }
                }
            })
        }

        view.findViewById<View>(R.id.skin_3).setOnClickListener {
            ConfMgr.setStringValue("", "skin", "", false)
            SkinManager.reset()
        }

        view.findViewById<View>(R.id.skin_4).setOnClickListener {
            startActivity(Intent(context, TwoActivity::class.java))
        }

        val addView = TextView(context)
        addView.text = "hahahahahahah"
        view.findViewById<LinearLayout>(R.id.skin_test_ll).addView(addView)
        SkinManager.setSkinAttrsWhenAddViewByCode(
            SkinViewWrapper.Builder(addView)
                .addAttr(SkinConstants.SupportAttributeName.TEXT_COLOR, R.color.skin_text_primary)
                .build()
        )
    }

}