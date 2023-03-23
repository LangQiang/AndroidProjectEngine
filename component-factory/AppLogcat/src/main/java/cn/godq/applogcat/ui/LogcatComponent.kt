package cn.godq.applogcat.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import cn.godq.applogcat.databinding.AlcMainLayoutBinding
import cn.godq.applogcat.mgr.AppLogcat
import cn.godq.applogcat.ui.content.IContent
import cn.godq.applogcat.ui.content.IContentEvent
import cn.godq.applogcat.utils.OnDragTouchListener
import cn.godq.applogcat.utils.UIHelper
import cn.godq.applogcat.utils.runOnUiThread


/**
 * @author  GodQ
 * @date  2023/3/3 5:37 下午
 */
class LogcatComponent(private val mContext: Context, private val contentViewCtrl: IContent) {

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
                    contentViewCtrl.setNewData(logs, vm.uiState.currentTag)
                }
                LogcatVm.TYPE_ADD -> {
                    contentViewCtrl.addData(logs)
                }
                LogcatVm.TYPE_CLEAR -> {}
                else -> {}
            }
        }

        contentViewCtrl.setContentEvent(object : IContentEvent{
            override fun onNewLogComeViewVisible(visible: Boolean) {
                setNewMsgComeViewVisible(visible)
            }
        })

    }

    fun isAttached() = attached

    fun attach(): View {
        attached = true
        return dataBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {

        contentViewCtrl.getView(mContext)?.also {
            dataBinding.alcContentView.addView(it, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
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

        dataBinding.scrollToBottomBtn.setOnClickListener {
            contentViewCtrl.scrollToBottom()
        }

        setInitXY()

        runOnUiThread {
            AppLogcat.getInstance().log("顶部区域拖拽")
            AppLogcat.getInstance().log("打印日志：AppLogcat.getInstance().log(log);", "", null)
            vm.initReqLogs()
        }
    }

    private fun setNewMsgComeViewVisible(b: Boolean) {
        dataBinding.scrollToBottomBtn.visibility = if (b) View.VISIBLE else View.GONE
    }

    private fun setInitXY() {
        dataBinding.root.x = 0f
        dataBinding.root.y = UIHelper.getTitleBarHeight(mContext).toFloat()
    }

    private fun clear() {
        val activity = currentVisibleActivity?: return
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("确认删除log")
        builder.setCancelable(false)
        builder.setPositiveButton("确定") { _: DialogInterface?, _: Int ->
            contentViewCtrl.clear()
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

    @MainThread
    fun log(logcatEntity: LogcatEntity) {
        vm.appendLog(logcatEntity)
    }

}