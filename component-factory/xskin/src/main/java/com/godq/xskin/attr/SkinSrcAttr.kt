package com.godq.xskin.attr

import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.godq.xskin.SkinConstants
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/2 7:02 PM
 */
class SkinSrcAttr(
    private val xmlAttrName: String, //textColor
    private val xmlAttrValue: Int,  //2131034848 去掉@的具体值
    private val resEntryName: String, //skin_color_red
    private val resTypeName: String)  //color
    : ISkinAttr {
    override fun apply(view: View) {
        if (view is ImageView) {
            if (SkinConstants.SupportAttributeName.SRC.value == xmlAttrName) {
                setSrcBySkinRes(view)
            }
        }
    }

    private fun setSrcBySkinRes(imageView: ImageView) {
        SkinManager.getSkinResource()?.getDrawable(resEntryName, resTypeName)?.apply {
            imageView.setImageDrawable(this)
        }
    }
}