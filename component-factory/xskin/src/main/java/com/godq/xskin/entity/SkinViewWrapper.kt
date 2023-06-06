package com.godq.xskin.entity

import android.view.View
import com.godq.xskin.SkinConstants
import com.godq.xskin.attr.ISkinAttr
import com.godq.xskin.attr.SkinAttrCreator
import java.lang.ref.WeakReference


/**
 * @author  GodQ
 * @date  2023/6/2 6:13 PM
 */
class SkinViewWrapper internal constructor(private val skinView: WeakReference<out View>, private val skinAttrs: List<ISkinAttr>) {
    fun apply() {
        skinAttrs.forEach {
            skinView.get()?.apply { it.apply(this) }
        }
    }
    fun isInvalid() = skinView.get() == null

    class Builder(private val skinView: View) {

        private val skinAttrs = ArrayList<ISkinAttr>()

        fun setAttr(attrName: SkinConstants.SupportAttributeName, attrId: Int): Builder {
            //获取属性值的字符串名称和类型: color skin_xxx_red
            val entryName = skinView.resources.getResourceEntryName(attrId)
            val typeName = skinView.resources.getResourceTypeName(attrId)
            SkinAttrCreator.create(attrName.value, attrId, entryName, typeName)?.apply {
                skinAttrs.add(this)
            }
            return this
        }

        fun build(): SkinViewWrapper {
            return SkinViewWrapper(WeakReference(skinView), skinAttrs)
        }
    }
}