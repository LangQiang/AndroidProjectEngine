package cn.godq.applogcat.ui

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.utils.UIHelper


/**
 * @author  GodQ
 * @date  2023/3/6 5:28 下午
 */
data class LogcatEntity(val log: String, val tag: String, val color: AlcColor, val timestamp: Long) {
    fun formatForTextView(): List<CharSequence> {
        val spannable: Spannable = SpannableString(log)
        val colorSpan = ForegroundColorSpan(Color.parseColor(color.color))
        spannable.setSpan(colorSpan, 0, log.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val date: String = UIHelper.getFormatDate("HH:mm:ss.SSS").toString() + ":"
        val dateSpannable = SpannableString(date)
        val timeColorSpan = ForegroundColorSpan(Color.parseColor("#3993d4"))
        dateSpannable.setSpan(timeColorSpan, 0, date.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return listOf(dateSpannable, " ", spannable, "\n")
    }
}