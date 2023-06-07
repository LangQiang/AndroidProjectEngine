package com.godq.xskin.entity

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable


/**
 * @author  GodQ
 * @date  2023/6/7 10:53 AM
 */
interface ISkinResource {
    fun getColor(originId: Int): Int?
    fun getColor(resEntryName: String, resTypeName: String): Int?
    fun getDrawable(originId: Int): Drawable?
    fun getDrawable(resEntryName: String, resTypeName: String): Drawable?
    fun getColorStateList(originId: Int): ColorStateList?
    fun getColorStateList(resEntryName: String, resTypeName: String): ColorStateList?
}