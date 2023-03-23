package cn.godq.applogcat.ui

import android.app.Activity
import android.app.AlertDialog
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cn.godq.applogcat.BR
import cn.godq.applogcat.filter.filter
import cn.godq.applogcat.repo.LogcatRepository
import cn.godq.applogcat.utils.UIHelper
import cn.godq.applogcat.utils.hasOptFlag
import kotlinx.coroutines.*


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

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val tagSet = HashSet<String>()

    val uiState = UIState()

    var onLogCallback: ((logs: List<LogcatEntity>, type: Int) -> Unit)? = null

    fun appendLog(logcatEntity: LogcatEntity) {
        with(logcatEntity) {
            if (logcatEntity.tag != DEFAULT_TAG) {
                tagSet.add(logcatEntity.tag)
            }
            if (!logcatEntity.optFLag.hasOptFlag(LogcatEntity.OPT_FLAG_NOT_SAVE_LOCAL)) {
                LogcatRepository.insertLog(this)
            }
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
                uiState.currentTag = array[which]
                if (last != uiState.currentTag) {
                    initReqLogs()
                }
            }
            setNegativeButton("取消"
            ) { dialog, _ -> UIHelper.safeDismissDialog(dialog, activity) }
        }.show()
    }

    fun initReqLogs() {
        scope.launch {
            val list = LogcatRepository.getHistoryLogsByTag(uiState.currentTag).reversed()
            onLogCallback?.invoke(filter(list), TYPE_NEW)
        }
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