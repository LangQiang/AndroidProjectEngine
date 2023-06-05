package com.godq.xskin.attr

import com.godq.xskin.SkinConstants


/**
 * @author  GodQ
 * @date  2023/6/2 7:03 PM
 */
object SkinAttrCreator {
    fun create(attrName: String, attrValue: Int, entryName: String, typeName: String): ISkinAttr? {
        return when (attrName) {
            SkinConstants.SUPPORT_ATTRIBUTE_TYPE_TEXT_COLOR -> {
                SkinTextColorAttr(attrName, attrValue, entryName, typeName)
            }

            SkinConstants.SUPPORT_ATTRIBUTE_TYPE_BACKGROUND -> {
                SkinBackgroundAttr(attrName, attrValue, entryName, typeName)
            }

            else -> {
                null
            }
        }
    }
}