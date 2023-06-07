package com.godq.xskin.entity

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.godq.xskin.SkinManager


/**
 * @author  GodQ
 * @date  2023/6/5 3:56 PM
 */
data class SkinResource(val resources: Resources, val skinPackageName: String): ISkinResource {

    override fun getColor(originId: Int): Int? {
        return try {
            val entryName = SkinManager.getSkinContext().resources.getResourceEntryName(originId)
            val typeName = SkinManager.getSkinContext().resources.getResourceTypeName(originId)
            getColor(entryName, typeName)
        } catch (e: Exception) {
            null
        }
    }

    override fun getColor(resEntryName: String, resTypeName: String): Int? {
        val currentResId = resources.getIdentifier(resEntryName, resTypeName, skinPackageName).takeIf {
            it != 0
        }?: return null
        return try {
            ResourcesCompat.getColor(resources, currentResId, null)
        } catch (e: Exception) {
            null
        }
    }

    override fun getDrawable(originId: Int): Drawable? {
        return try {
            val entryName = SkinManager.getSkinContext().resources.getResourceEntryName(originId)
            val typeName = SkinManager.getSkinContext().resources.getResourceTypeName(originId)
            getDrawable(entryName, typeName)
        } catch (e: Exception) {
            null
        }
    }

    override fun getDrawable(resEntryName: String, resTypeName: String): Drawable? {
        val currentResId = resources.getIdentifier(resEntryName, resTypeName, skinPackageName).takeIf {
            it != 0
        }?: return null
        return try {
            ResourcesCompat.getDrawable(resources, currentResId, null)
        } catch (e: Exception) {
            null
        }
    }

    override fun getColorStateList(originId: Int): ColorStateList? {
        return try {
            val entryName = SkinManager.getSkinContext().resources.getResourceEntryName(originId)
            val typeName = SkinManager.getSkinContext().resources.getResourceTypeName(originId)
            getColorStateList(entryName, typeName)
        } catch (e: Exception) {
            null
        }
    }

    override fun getColorStateList(resEntryName: String, resTypeName: String): ColorStateList? {
        val currentResId = resources.getIdentifier(resEntryName, resTypeName, skinPackageName).takeIf {
            it != 0
        }?: return null
        return try {
            ResourcesCompat.getColorStateList(resources, currentResId, null)
        } catch (e: Exception) {
            null
        }
    }

}