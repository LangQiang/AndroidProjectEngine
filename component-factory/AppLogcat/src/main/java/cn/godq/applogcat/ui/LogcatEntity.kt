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
data class LogcatEntity(
    val id: Long? = null,
    val uuid: String,
    val log: String,
    val tag: String,
    val color: AlcColor,
    val isMainThread: Boolean,
    val isMainProcess: Boolean,
    val timestamp: Long,
    val optFLag: Long,
    val bootMark: String,
    ) {

    companion object {

        const val OPT_FLAG_NOT_SAVE_LOCAL = 1L shl 0

        fun getDefaultOptFlag() = 0L
    }

    fun formatForTextView(): List<CharSequence> {
        val spannable: Spannable = SpannableString(log)
        val colorSpan = ForegroundColorSpan(UIHelper.parseColor(color.getColorStr(), 0xffffffff.toInt()))
        spannable.setSpan(colorSpan, 0, log.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val isMainThreadStr = if (isMainThread) "MainT" else "OtherT"
        val date = "${UIHelper.getFormatDate("HH:mm:ss.SSS", this.timestamp)}/$isMainThreadStr:"
        val dateSpannable = SpannableString(date)
        val timeColorSpan = ForegroundColorSpan(Color.parseColor("#3993d4"))
        dateSpannable.setSpan(timeColorSpan, 0, date.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return listOf(dateSpannable, " ", spannable, "\n")
    }

    fun formatForRecyclerView(currentTag: String?): List<CharSequence> {
        val spannable: Spannable = SpannableString(log)
        val colorSpan = ForegroundColorSpan(UIHelper.parseColor(color.getColorStr(), 0xffffffff.toInt()))
        spannable.setSpan(colorSpan, 0, log.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val isMainThreadStr = if (isMainThread) "MainT" else "OtherT"
        val showTag = if (currentTag == LogcatVm.DEFAULT_TAG) "/$tag" else ""
        val date = "${UIHelper.getFormatDate("HH:mm:ss.SSS", this.timestamp)}/$isMainThreadStr$showTag:"
        val dateSpannable = SpannableString(date)
        val timeColorSpan = ForegroundColorSpan(Color.parseColor("#3993d4"))
        dateSpannable.setSpan(timeColorSpan, 0, date.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return listOf(dateSpannable, " ", spannable)
    }

    fun formatForNormalSaveStr(currentTag: String?): String {
        val isMainThreadStr = if (isMainThread) "MainT" else "OtherT"
        val showTag = if (currentTag == LogcatVm.DEFAULT_TAG) "/$tag" else ""
        val date = "${UIHelper.getFormatDate("HH:mm:ss.SSS", this.timestamp)}/$isMainThreadStr$showTag:"
        return "${date}${log}"
    }
}