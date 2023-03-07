package cn.godq.applogcat.ui.content

import android.content.Context
import android.view.View
import cn.godq.applogcat.ui.LogcatEntity


/**
 * @author  GodQ
 * @date  2023/3/7 6:13 下午
 */
interface IContent {

    fun setContentEvent(event: IContentEvent)

    fun getView(context: Context): View?

    fun setNewData(logs: List<LogcatEntity>)

    fun addData(logs: List<LogcatEntity>)

    fun clear()

    fun scrollToBottom()

}