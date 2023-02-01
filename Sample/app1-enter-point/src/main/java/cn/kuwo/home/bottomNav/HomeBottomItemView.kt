package cn.kuwo.home.bottomNav

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.godq.botnav.R
import com.godq.compose.botnav.BottomItemData
import com.godq.compose.botnav.ITabView


/**
 * @author  GodQ
 * @date  2023/1/29 7:08 下午
 */
@SuppressLint("ViewConstructor")
class HomeBottomItemView(private val mContext: Context, data: BottomItemData?) : FrameLayout(mContext),
    ITabView {

    var iconView: TextView? = null

    var textView: TextView? = null

    private var bgView: View? = null

    private var iconSelectStr: Int? = null
    private var iconNormalStr: Int? = null

    init {
        iconSelectStr =data?.selectIconRes

        iconNormalStr = data?.normalIconRes


        val view = View.inflate(mContext, R.layout.ui_compose_bottom_tab, null)
        iconView = view.findViewById(R.id.icon_view)
        textView = view.findViewById(R.id.text_tv)
        bgView = view.findViewById(R.id.bg_view)
        textView?.text = data?.title ?: "unknown"
        iconNormalStr?.also {
            iconView?.setBackgroundResource(it)
        }
        addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun onSelected(selected: Boolean) {
        if (selected) {
            textView?.setTextColor(ContextCompat.getColor(context, R.color.black))
            textView?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            iconView?.setTextColor(ContextCompat.getColor(context, R.color.black))
            iconSelectStr?.also {
                iconView?.setBackgroundResource(it)
            }
            bgView?.visibility = VISIBLE
        } else {
            textView?.setTextColor(ContextCompat.getColor(context, R.color.black40))
            textView?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            iconView?.setTextColor(ContextCompat.getColor(context, R.color.black40))
            iconNormalStr?.also {
                iconView?.setBackgroundResource(it)
            }
            bgView?.visibility = INVISIBLE
        }
    }
}