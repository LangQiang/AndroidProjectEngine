package com.godq.compose.titlebar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.godq.compose.R


/**
 * @author  GodQ
 * @date  2023/6/8 5:24 PM
 */
class TitleBarDefaultResDelegate(private val context: Context): AbsTitleBarResDelegate {

    override fun onAttach() {
    }

    override fun onDetach() {
    }

    override fun getBackIcon(): Drawable? = try {
        ResourcesCompat.getDrawable(context.resources, R.drawable.ui_compose_title_bar_back_black, null)
    } catch (e: Exception) {
        null
    }

    override fun getTitleColor(): Int? = try {
        ResourcesCompat.getColor(context.resources, R.color.black, null)
    } catch (e: Exception) {
        null
    }

    override fun getMenuIcon(): Drawable? = null

    override fun getMenuTextStr(): String? = null

    override fun getMenuTextColor(): Int? = try {
        ResourcesCompat.getColor(context.resources, R.color.black40, null)
    } catch (e: Exception) {
        null
    }

    override fun getBackground(): Drawable? = null
}