package com.godq.compose.iconview

import android.graphics.Typeface
import com.godq.compose.UICompose
import java.lang.Exception

private var iconFontTypeface: Typeface? = null

fun getIconFontType(): Typeface? {
    if (iconFontTypeface == null) {
        val context = UICompose.applicationContext ?: return null
        val path = UICompose.config?.fontAssetsPath ?: return null
        try {
            iconFontTypeface = Typeface.createFromAsset(context.assets, path)
        } catch (e: Exception) {
        }
    }
    return iconFontTypeface
}