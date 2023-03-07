package cn.godq.applogcat.ui

import android.app.Activity
import android.app.AlertDialog
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import cn.godq.applogcat.BR
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.ui.color.ColorSelector


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

    private val colorSelector = ColorSelector()

    private val tagSet = HashSet<String>()

    val uiState = UIState()

    var onLogCallback: ((logs: List<LogcatEntity>, type: Int) -> Unit)? = null

    fun appendLog(log: String?, tag: String?, color: AlcColor?) {
        log?: return
        val tTag: String = if (tag.isNullOrEmpty()) DEFAULT_TAG else tag
        val tColor = color?: colorSelector.nextColor()
        with(LogcatEntity(log, tTag, tColor, System.currentTimeMillis())) {
            tagSet.add(tTag)
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
        val array = arrayOfNulls<String>(tagSet.size)
        tagSet.toArray(array)
        AlertDialog.Builder(activity).setItems(array) { _, which ->
            val last = uiState.currentTag
            uiState.currentTag = array[which] ?: DEFAULT_TAG
            if (last != uiState.currentTag) {
                onLogCallback?.invoke(repository.getLogsByTag(uiState.currentTag), TYPE_NEW)
            }
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