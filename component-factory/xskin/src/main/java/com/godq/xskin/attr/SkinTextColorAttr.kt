package com.godq.xskin.attr

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.godq.xskin.SkinConstants
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/2 7:02 PM
 */
class SkinTextColorAttr(
    private val xmlAttrName: String, //textColor
    private val xmlAttrValue: Int,  //2131034848 去掉@的具体值
    private val resEntryName: String, //skin_color_red
    private val resTypeName: String)  //color
    : ISkinAttr {
    override fun apply(view: View) {
        if (view is TextView) {
            if (SkinConstants.SupportAttributeName.TEXT_COLOR.value == xmlAttrName) {
                setTextColorBySkinRes(view)
            }
        }
    }

    private fun setTextColorBySkinRes(textView: TextView) {
        SkinManager.getSkinResource()?.getColorStateList(resEntryName, resTypeName)?.apply {
            textView.setTextColor(this)
        }
    }
}