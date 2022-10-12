package com.godq.compose.iconview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class IconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(
    context, attrs, defStyleAttr
) {
    init {
        getIconFontType()?.apply {
            typeface = this
        }
    }
}