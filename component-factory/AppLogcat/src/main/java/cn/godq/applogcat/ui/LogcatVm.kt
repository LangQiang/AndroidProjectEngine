package cn.godq.applogcat.ui

import android.app.Activity
import android.app.AlertDialog
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cn.godq.applogcat.BR
import cn.godq.applogcat.utils.UIHelper


/**
 * @author  GodQ
 * @date  2023/3/6 5:24 下午
 */
class LogcatVm {

    companion object {
        const val TYPE_NEW = 0
        const val TYPE_ADD = 1
        const val TYPE_CLEAR = 2

        const val DEFAULT_TAG = "default"
    }

    private val repository = LogcatRepository()

    private val tagSet = HashSet<String>()

    val uiState = UIState()

    var onLogCallback: ((logs: List<LogcatEntity>, type: Int) -> Unit)? = null

    fun appendLog(logcatEntity: LogcatEntity) {
        with(logcatEntity) {
            if (logcatEntity.tag != DEFAULT_TAG) {
                tagSet.add(logcatEntity.tag)
            }
            repository.insertLog(this)
            if (checkTag(this)) {
                onLogCallback?.invoke(listOf(this), TYPE_ADD)
            }
        }

    }

    private fun checkTag(logcatEntity: LogcatEntity): Boolean {
        return uiState.currentTag == DEFAULT_TAG || logcatEntity.tag == uiState.currentTag
    }

    fun onTagClick(activity: Activity?) {
        activity?: return
        val list = mutableListOf(DEFAULT_TAG)
        list.addAll(tagSet)
        val array =list.toTypedArray()
        AlertDialog.Builder(activity).apply {
            setItems(array) { _, which ->
                val last = uiState.currentTag
                uiState.currentTag = array[which] ?: DEFAULT_TAG
                if (last != uiState.currentTag) {
                    onLogCallback?.invoke(repository.getLogsByTag(uiState.currentTag), TYPE_NEW)
                }
            }
            setNegativeButton("取消"
            ) { dialog, _ -> UIHelper.safeDismissDialog(dialog, activity) }
        }.show()
    }

    fun forceRefresh() {
        onLogCallback?.invoke(repository.getLogsByTag(uiState.currentTag), TYPE_NEW)
    }

    class UIState: BaseObservable() {

        @get:Bindable
        var currentTag: String = DEFAULT_TAG
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentTag)
        }
    }
}