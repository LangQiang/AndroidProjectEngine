package com.godq.test.uicompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.compose.titlebar.TitleBar
import com.godq.test.R
import com.lazylite.mod.widget.BaseFragment


/**
 * @author  GodQ
 * @date  2023/6/8 5:53 PM
 */
class UIComposeFragment: BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_ui_compose_text_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TitleBar>(R.id.titlebar).apply {
            setTitle("hahaha")
            setMenuTitle("heihei")
            setBackClickListener {

            }
            setMenuClickListener {

            }
            setResDelegate(SkinTitleBarResDelegate(this))
        }
    }
}