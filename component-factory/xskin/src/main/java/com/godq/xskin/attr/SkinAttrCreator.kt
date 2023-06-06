package com.godq.xskin.attr

import com.godq.xskin.SkinConstants


/**
 * @author  GodQ
 * @date  2023/6/2 7:03 PM
 */
object SkinAttrCreator {
    fun create(attrName: String, attrValue: Int, entryName: String, typeName: String): ISkinAttr? {
        return when (attrName) {
            SkinConstants.SupportAttributeName.TEXT_COLOR.value -> {
                SkinTextColorAttr(attrName, attrValue, entryName, typeName)
            }

            SkinConstants.SupportAttributeName.BACKGROUND.value -> {
                SkinBackgroundAttr(attrName, attrValue, entryName, typeName)
            }

            else -> {
                null
            }
        }
    }
}