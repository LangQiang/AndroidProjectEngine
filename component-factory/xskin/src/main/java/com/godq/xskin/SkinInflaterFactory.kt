package com.godq.xskin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.godq.xskin.attr.ISkinAttr
import com.godq.xskin.attr.SkinAttrCreator
import com.godq.xskin.entity.SkinViewWrapper
import java.lang.ref.WeakReference

/**
 *
 * @author  GodQ
 * @date  2023/5/30 4:47 PM
 *
 * 负责采集
 * */
class SkinInflaterFactory : LayoutInflater.Factory2 {
    private val sClassPrefixList = arrayOf(
        "android.view.",
        "android.widget.",
        "android.webkit.",
        "android.app."
    )

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return createView(name, context, attrs)
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return createView(name, context, attrs)
    }

    /**
     * 系统创建view尝试使用factory的实现，在这里统一处理
     * */
    private fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        //非换肤view直接跳过
        if (!isSkinView(name, attrs)) {
            return null
        }

        //使用系统方式创建view
        return createUseSystemBehavior(name, context, attrs)?.apply {
            //收集换肤view信息
            collectSkinView(this, context, attrs)
        }
    }

    private fun collectSkinView(view: View, context: Context, attrs: AttributeSet) {
        val skinAttrs = mutableListOf<ISkinAttr>()

        //遍历属性
        for(i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            //忽略不支持的换肤属性
            if (!SkinConstants.SUPPORT_ATTRIBUTE_TYPE_SET.contains(attrName)) {
                continue
            }
            //获取属性值 形如 @2131034848
            val attrValueStr = attrs.getAttributeValue(i)?.takeIf { it.startsWith("@") }?: continue
            try {
                val attrValue = attrValueStr.substring(1).toInt()
                //获取属性值的字符串名称和类型: color skin_xxx_red
                val entryName = context.resources.getResourceEntryName(attrValue)
                val typeName = context.resources.getResourceTypeName(attrValue)
                SkinAttrCreator.create(attrName, attrValue, entryName, typeName)?.apply {
                    skinAttrs.add(this)
                }
            } catch (e: Exception) {
                continue
            }
        }
        if (skinAttrs.isEmpty()) {
            return
        }
        //添加并应用当前资源指向的皮肤
        SkinManager.addSkinView(SkinViewWrapper(WeakReference(view), skinAttrs))
    }

    private fun isSkinView(name: String, attrs: AttributeSet): Boolean {
        //可以在这里加过滤器
        when (name) {
            "com.godq.xskin.widget.SkinTransitionBgView" -> {
                return true
            }
            else -> {

            }
        }
        return attrs.getAttributeBooleanValue(SkinConstants.NAMESPACE, SkinConstants.ATTR_SKIN_ENABLE, false)
    }

    private fun createUseSystemBehavior(name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = null
        try {
            if (-1 == name.indexOf('.')) {
                for (prefix in sClassPrefixList) {
                    try {
                        view = LayoutInflater.from(context).createView(name, prefix, attrs)
                        if (view != null) {
                            break
                        }
                    } catch (ignore: ClassNotFoundException) {

                    }
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs)
            }
        } catch (e: Exception) {
            view = null
        }
        return view
    }
}