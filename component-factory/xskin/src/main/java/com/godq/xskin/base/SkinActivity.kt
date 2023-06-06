package com.godq.xskin.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.godq.xskin.XSkinManager


/**
 * @author  GodQ
 * @date  2023/5/30 6:01 PM
 */
open class SkinActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, XSkinManager.getSkinInflaterFactory())
        super.onCreate(savedInstanceState)
    }

}