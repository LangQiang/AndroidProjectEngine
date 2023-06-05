package com.godq.xskin


/**
 * @author  GodQ
 * @date  2023/6/2 5:03 PM
 */
class SkinConstants {
    companion object {
        const val NAMESPACE = "http://schemas.android.com/xskin"
        const val ATTR_SKIN_ENABLE = "enable"

        const val SUPPORT_ATTRIBUTE_TYPE_TEXT_COLOR = "textColor"
        const val SUPPORT_ATTRIBUTE_TYPE_BACKGROUND = "background"


        //资源类型
        const val RES_TYPE_COLOR = "color"
        const val RES_TYPE_DRAWABLE = "drawable"

        val SUPPORT_ATTRIBUTE_TYPE_SET = setOf(
            SUPPORT_ATTRIBUTE_TYPE_BACKGROUND,
            SUPPORT_ATTRIBUTE_TYPE_TEXT_COLOR,
            "src",
        )
    }
}