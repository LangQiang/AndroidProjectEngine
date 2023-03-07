package cn.godq.applogcat.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import cn.godq.applogcat.databinding.AlcMainLayoutBinding
import cn.godq.applogcat.ui.color.AlcColor
import cn.godq.applogcat.utils.OnDragTouchListener
import cn.godq.applogcat.utils.UIHelper
import cn.godq.applogcat.utils.runOnUiThread


/**
 * @author  GodQ
 * @date  2023/3/3 5:37 下午
 */
class LogcatComponent(private val mContext: Context) {

    private val vm = LogcatVm()

    private val dataBinding: AlcMainLayoutBinding by lazy {
        AlcMainLayoutBinding.inflate(LayoutInflater.from(mContext)).apply {
            root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.vm = this@LogcatComponent.vm
            this.logcatComponent = this@LogcatComponent
        }
    }

    var currentVisibleActivity: Activity? = null

    private var attached = false

    init {
        initView()
        vm.onLogCallback = { logs: List<LogcatEntity>, type: Int ->
            when (type) {
                LogcatVm.TYPE_NEW -> {
                    dataBinding.logTv.text = ""
                    logs.forEach { notifyView(it) }
                }
                LogcatVm.TYPE_ADD -> {
                    logs.forEach { notifyView(it) }
                }
                LogcatVm.TYPE_CLEAR -> {}
                else -> {}
            }
        }
    }

    fun isAttached() = attached

    fun attach(): View {
        attached = true
        return dataBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(dataBinding.logTv) {
            this.isVerticalScrollBarEnabled = true
            this.movementMethod = ScrollingMovementMethod.getInstance()
            this.setTextIsSelectable(true)

            log("顶部区域拖拽")
            log("打印日志：AppLogcat.getInstance().log(log);", "", null)
        }
        dataBinding.alcClearBtn.setOnClickListener {
            clear()
        }

        dataBinding.dragArea.setOnTouchListener(OnDragTouchListener(dataBinding.root))

        dataBinding.alcIcon.setOnClickListener {
            dataBinding.alcIcon.visibility = View.INVISIBLE
            dataBinding.alcContainer.visibility = View.VISIBLE
        }

        dataBinding.alcMinimizeBtn.setOnClickListener {
            dataBinding.alcIcon.visibility = View.VISIBLE
            dataBinding.alcContainer.visibility = View.INVISIBLE
        }

        dataBinding.alcIcon.setOnTouchListener(OnDragTouchListener(dataBinding.root))

        runOnUiThread {
            vm.forceRefresh()
        }
    }

    private fun clear() {
        val activity = currentVisibleActivity?: return
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("确认删除log")
        builder.setCancelable(false)
        builder.setPositiveButton("确定") { _: DialogInterface?, _: Int ->
            dataBinding.logTv.text = ""
        }
        builder.setNegativeButton("取消") { dialog: DialogInterface?, _: Int ->
            UIHelper.safeDismissDialog(dialog, activity)
        }
        builder.show().setCanceledOnTouchOutside(false)
    }

    fun detach(): View {
        attached = false
        return dataBinding.root
    }

    private fun notifyView(logcatEntity: LogcatEntity) {
        logcatEntity.formatForTextView().forEach {
            dataBinding.logTv.append(it)
        }
        val scrollAmount = (dataBinding.logTv.layout?.getLineTop(dataBinding.logTv.lineCount) ?: 0) - dataBinding.logTv.height
        if (scrollAmount > 0) dataBinding.logTv.scrollTo(
            0,
            scrollAmount
        ) else dataBinding.logTv.scrollTo(0, 0)
    }

    @MainThread
    fun log(log: String?, tag: String? = null, color: AlcColor? = null) {
        vm.appendLog(log, tag, color)
    }

}