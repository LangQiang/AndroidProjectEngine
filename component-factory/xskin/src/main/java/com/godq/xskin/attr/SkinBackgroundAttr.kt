package com.godq.xskin.attr

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.godq.xskin.SkinConstants
import com.godq.xskin.SkinManager


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
        if (SkinConstants.SupportAttributeName.BACKGROUND.value == xmlAttrName) {
            setBackgroundBySkinRes(view)
        }
    }

    private fun setBackgroundBySkinRes(view: View) {
        if (SkinConstants.RES_TYPE_COLOR == resTypeName) {
            SkinManager.getSkinResource()?.getColor(resEntryName, resTypeName)?.apply {
                view.setBackgroundColor(this)
            }
        } else if (SkinConstants.RES_TYPE_DRAWABLE == resTypeName) {
            SkinManager.getSkinResource()?.getDrawable(resEntryName, resTypeName)?.apply {
                //fixme 可能存在view的padding被设置为drawable的padding
                ViewCompat.setBackground(view, this)
            }
        }
    }
}