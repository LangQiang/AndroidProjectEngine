package com.godq.xskin.attr

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.godq.xskin.SkinConstants
import com.godq.xskin.XSkinManager


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
            if (SkinConstants.SUPPORT_ATTRIBUTE_TYPE_TEXT_COLOR == xmlAttrName) {
                setTextColorBySkinRes(view)
            }
        }
    }

    private fun setTextColorBySkinRes(textView: TextView) {
        val (currentResource, currentPackageName) = XSkinManager.getCurrentResourceInfo() ?: return
        val currentResId = currentResource.getIdentifier(resEntryName, resTypeName, currentPackageName).takeIf {
            it != 0
        }?: return
        try {
            ResourcesCompat.getColorStateList(currentResource, currentResId, null)
        } catch (e: Exception) {
            null
        }?.apply {
            textView.setTextColor(this)
        }
    }
}