package cn.godq.applogcat.ui.content.recycler

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.godq.applogcat.R
import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.ui.LogcatEntity
import cn.godq.applogcat.utils.UIHelper
import java.lang.ref.WeakReference


/**
 * @author  GodQ
 * @date  2023/3/7 5:11 下午
 */
class LogcatAdapter(data: List<LogcatEntity>?, private val layoutId: Int): RecyclerView.Adapter<LogcatAdapter.ViewHolder>() {

    private var currentTextView: TextView? = null

    private val mData = ArrayList<LogcatEntity>()

    init {
        if (!data.isNullOrEmpty()) {
            mData.addAll(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(this, View.inflate(parent.context, layoutId, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.logTv?.text = ""
        mData[position].apply {
            formatForRecyclerView().forEach {
                holder.logTv?.append(it)
            }
            holder.data = this
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(data: List<LogcatEntity>?) {
        mData.clear()
        if (!data.isNullOrEmpty()) {
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }

    fun addData(data: List<LogcatEntity>?) {
        if (!data.isNullOrEmpty()) {
            mData.addAll(data)
            notifyItemRangeInserted(mData.size - data.size, data.size)
        }
    }

    fun clearTextViewFocus() {
        currentTextView?.clearFocus()
    }

    class ViewHolder(adapter: LogcatAdapter, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val adapterReference = WeakReference(adapter)
        var data: LogcatEntity? = null
        val logTv: TextView? = itemView.findViewById<TextView?>(R.id.log_item_tv)?.apply {
//            setOnClickListener {
//                UIHelper.showToast(AppLogcat.INSTANCE.mContext, data?.log?: "null")
//            }
            setOnLongClickListener {
                adapterReference.get()?.currentTextView = this
                false
            }
        }
    }
}