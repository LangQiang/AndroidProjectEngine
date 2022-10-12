package com.godq.compose.botnav

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.godq.botnav.R

class BottomTabView(private val mContext: Context, data: BottomItemData?) : FrameLayout(mContext),
    ITabView {

    var iconView: TextView? = null

    var textView: TextView? = null

    private var bgView: View? = null

    private var iconSelectStr: String? = null
    private var iconNormalStr: String? = null

    init {
        data?.selectIconRes?.let {
            iconSelectStr = mContext.getString(it)
        }
        data?.normalIconRes?.let {
            iconNormalStr = mContext.getString(it)
        }

        val view = View.inflate(mContext, R.layout.ui_compose_bottom_tab, null)
        iconView = view.findViewById(R.id.icon_view)
        textView = view.findViewById(R.id.text_tv)
        bgView = view.findViewById(R.id.bg_view)
        textView?.text = data?.title ?: "unknown"
        iconView?.text = iconNormalStr ?: ""
        addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun onSelected(selected: Boolean) {
        if (selected) {
            textView?.setTextColor(ContextCompat.getColor(context, R.color.black))
            textView?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            iconView?.setTextColor(ContextCompat.getColor(context, R.color.black))
            iconView?.text = iconSelectStr ?: ""
            bgView?.visibility = VISIBLE
        } else {
            textView?.setTextColor(ContextCompat.getColor(context, R.color.black40))
            textView?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            iconView?.setTextColor(ContextCompat.getColor(context, R.color.black40))
            iconView?.text = iconNormalStr ?: ""
            bgView?.visibility = INVISIBLE
        }
    }
}