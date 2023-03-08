package cn.godq.applogcat.ui.content.recycler

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.godq.applogcat.R
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.ui.content.IContent
import cn.godq.applogcat.ui.content.IContentEvent
import cn.godq.applogcat.utils.runOnUiThread


/**
 * @author  GodQ
 * @date  2023/3/7 5:46 下午
 */
class ContentRecyclerViewCtrl: IContent {

    private var event: IContentEvent? = null

    private val mAdapter = LogcatAdapter(null, R.layout.alc_content_item_layout)

    private var mRecyclerView: RecyclerView? = null

    override fun setContentEvent(event: IContentEvent) {
        this.event = event
    }

    override fun getView(context: Context): View? {
        return (View.inflate(context, R.layout.alc_content_recycler_view, null) as? RecyclerView)?.apply {
            initView(this)
        }
    }

    private fun initView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        recyclerView.clipToPadding = false
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = mAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    event?.onNewLogComeViewVisible(false)
                }
            }
        })
        recyclerView.addOnItemTouchListener(RVOnItemTouchListener {
            mAdapter.clearTextViewFocus()
        })
    }

    override fun scrollToBottom() {
        mRecyclerView?.scrollToPosition(mAdapter.itemCount - 1)
    }

    private fun scrollWhenUpdate() {
        val count = mAdapter.itemCount - 1
        if (count >= 0) {
            runOnUiThread {
                val layoutManager = mRecyclerView?.layoutManager as? LinearLayoutManager
                if ((layoutManager?.findLastVisibleItemPosition()?: 0) > count - 5) {
                    mRecyclerView?.scrollToPosition(mAdapter.itemCount - 1)
                } else {
                    event?.onNewLogComeViewVisible(true)
                }
            }
        }
    }

    override fun setNewData(logs: List<LogcatEntity>, currentTag: String) {
        mAdapter.currentTag = currentTag
        mAdapter.setNewData(logs)
        scrollToBottom()
    }

    override fun addData(logs: List<LogcatEntity>) {
        mAdapter.addData(logs)
        scrollWhenUpdate()
    }

    override fun clear() {
        mAdapter.setNewData(null)
    }

}