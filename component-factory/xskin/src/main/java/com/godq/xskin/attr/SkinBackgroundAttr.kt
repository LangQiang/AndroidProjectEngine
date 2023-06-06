package com.godq.xskin.attr

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.godq.xskin.SkinConstants
import com.godq.xskin.XSkinManager


/**
 * @author  GodQ
 * @date  2023/6/2 7:02 PM
 */
class SkinBackgroundAttr(
    private val xmlAttrName: String, //textColor
    private val xmlAttrValue: Int,  //2131034848 去掉@的具体值
    private val resEntryName: String, //skin_color_red
    private val resTypeName: String)  //color
    : ISkinAttr {
    override fun apply(view: View) {
        if (SkinConstants.SUPPORT_ATTRIBUTE_TYPE_BACKGROUND == xmlAttrName) {
            setBackgroundBySkinRes(view)
        }
    }

    private fun setBackgroundBySkinRes(view: View) {
        val (currentResource, currentPackageName) = XSkinManager.getCurrentResourceInfo() ?: return
        val currentResId = currentResource.getIdentifier(resEntryName, resTypeName, currentPackageName).takeIf {
            it != 0
        }?: return

        if (SkinConstants.RES_TYPE_COLOR == resTypeName) {
            try {
                ResourcesCompat.getColor(currentResource, currentResId, null)
            } catch (e: Exception) {
                null
            }?.apply {
                view.setBackgroundColor(this)
            }
        } else if (SkinConstants.RES_TYPE_DRAWABLE == resTypeName) {
            try {
                ResourcesCompat.getDrawable(currentResource, currentResId, null)
            } catch (e: Exception) {
                null
            }?.apply {
                //fixme 可能存在view的padding被设置为drawable的padding
                ViewCompat.setBackground(view, this)
            }
        }
    }
}