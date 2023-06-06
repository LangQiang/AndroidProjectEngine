package com.godq.xskin


/**
 * @author  GodQ
 * @date  2023/6/2 5:03 PM
 */
class SkinConstants {
    companion object {
        const val NAMESPACE = "http://schemas.android.com/xskin"
        const val ATTR_SKIN_ENABLE = "enable"


        //资源类型
        const val RES_TYPE_COLOR = "color"
        const val RES_TYPE_DRAWABLE = "drawable"

        val SUPPORT_ATTRIBUTE_TYPE_SET = setOf(
            SupportAttributeName.TEXT_COLOR.value,
            SupportAttributeName.BACKGROUND.value,
            SupportAttributeName.SRC.value,
            SupportAttributeName.TEXT_COLOR_HINT.value,
        )
    }

    enum class SupportAttributeName(val value: String) {
        TEXT_COLOR("textColor"),
        BACKGROUND("background"),
        SRC("src"),
        TEXT_COLOR_HINT("textColorHint"),
    }
}