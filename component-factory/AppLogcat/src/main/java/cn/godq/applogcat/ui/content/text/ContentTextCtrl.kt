package cn.godq.applogcat.ui.content.text

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import cn.godq.applogcat.R
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.content.IContent
import cn.godq.applogcat.ui.content.IContentEvent


/**
 * @author  GodQ
 * @date  2023/3/7 5:45 下午
 */
class ContentTextCtrl: IContent {

    private var event: IContentEvent? = null

    private var mTextView: TextView? = null

    override fun setContentEvent(event: IContentEvent) {
        this.event = event
    }

    override fun getView(context: Context): View? {
        return (View.inflate(context, R.layout.alc_content_text_view, null) as? TextView)?.apply {
            initView(this)
        }
    }

    private fun initView(textView: TextView) {
        mTextView = textView

        textView.isVerticalScrollBarEnabled = true
        textView.movementMethod = ScrollingMovementMethod.getInstance()
        textView.setTextIsSelectable(true)
    }

    override fun setNewData(logs: List<LogcatEntity>) {
        mTextView?.text = ""
        logs.forEach { notifyView(it) }
    }

    override fun addData(logs: List<LogcatEntity>) {
        logs.forEach { notifyView(it) }
    }

    override fun clear() {
        mTextView?.text = ""
    }

    override fun scrollToBottom() {

    }

    private fun notifyView(logcatEntity: LogcatEntity) {
        logcatEntity.formatForTextView().forEach {
            mTextView?.append(it)
        }
        val tv = mTextView?: return
        val scrollAmount = (tv.layout?.getLineTop(tv.lineCount) ?: 0) - tv.height
        if (scrollAmount > 0) tv.scrollTo(
            0,
            scrollAmount
        ) else tv.scrollTo(0, 0)
    }
}