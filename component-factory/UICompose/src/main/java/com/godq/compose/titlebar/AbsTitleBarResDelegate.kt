package com.godq.compose.titlebar

import android.content.Context
import android.graphics.drawable.Drawable


/**
 * @author  GodQ
 * @date  2023/6/8 3:21 PM
 */
interface AbsTitleBarResDelegate {

    fun onAttach()
    fun onDetach()

    fun getBackIcon(): Drawable?
    fun getTitleColor(): Int?
    fun getMenuIcon(): Drawable?
    fun getMenuTextStr(): String?
    fun getMenuTextColor(): Int?
    fun getBackground(): Drawable?
}