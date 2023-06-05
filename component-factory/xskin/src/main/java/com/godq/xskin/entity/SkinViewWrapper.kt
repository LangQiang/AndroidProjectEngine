package com.godq.xskin.entity

import android.view.View
import com.godq.xskin.attr.ISkinAttr
import java.lang.ref.WeakReference


/**
 * @author  GodQ
 * @date  2023/6/2 6:13 PM
 */
class SkinViewWrapper(private val skinView: WeakReference<out View>, private val skinAttrs: List<ISkinAttr>) {
    fun apply() {
        skinAttrs.forEach {
            skinView.get()?.apply { it.apply(this) }
        }
    }
    fun isInvalid() = skinView.get() == null
}