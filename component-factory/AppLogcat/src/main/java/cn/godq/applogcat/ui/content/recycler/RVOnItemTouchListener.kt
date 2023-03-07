package cn.godq.applogcat.ui.content.recycler

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.godq.applogcat.R
import cn.godq.applogcat.utils.isTouchInView


/**
 * @author  GodQ
 * @date  2022/12/29 5:50 下午
 */
class RVOnItemTouchListener(val onClickEmptyCallback: ()->Unit): RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            val touchView = rv.findChildViewUnder(e.x, e.y)
            if (touchView == null) {
                onClickEmptyCallback()
                return false
            }
            val textViewStart: View? = touchView.findViewById(R.id.log_item_tv)
            if (textViewStart == null

            ) {
                onClickEmptyCallback()
                return false
            }

            if (!isTouchInView(textViewStart, e)

            ) {
                onClickEmptyCallback()
                return false
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}