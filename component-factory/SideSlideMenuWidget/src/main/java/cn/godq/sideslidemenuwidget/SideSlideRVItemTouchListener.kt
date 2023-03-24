package cn.godq.sideslidemenuwidget

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.abs


/**
 * @author  GodQ
 * @date  2023/3/23 7:03 下午
 */
class SideSlideRVItemTouchListener : RecyclerView.OnItemTouchListener {
    private var x = 0f
    private var y = 0f
    private var finalIntercept: Boolean? = null
    private var currentTouchView: SideSlideMenuLayout? = null

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            x = e.x
            y = e.y
            finalIntercept = null
        } else if (e.actionMasked == MotionEvent.ACTION_MOVE) {

            finalIntercept?.apply {
                return this
            }
            val moveX = e.x
            val moveY = e.y
            val diffX = moveX - x
            val diffY = moveY - y
            x = moveX
            y = moveY

            val lastView = currentTouchView
            //横滑
            if (abs(diffY / diffX) < 1) {
                currentTouchView = findSideSlideLayout(rv.findChildViewUnder(e.x, e.y))
                if (currentTouchView != lastView) {
                    lastView?.close()
                }
                finalIntercept = true
                return true
            }
            lastView?.close()
            finalIntercept = false
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        currentTouchView?.onTouchEventParent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    private fun findSideSlideLayout(view: View?): SideSlideMenuLayout? {
        if (view !is ViewGroup) return null

        val viewGroupList = LinkedList<ViewGroup>()
        viewGroupList.add(view)
        while (viewGroupList.isNotEmpty()) {
            val viewGroup = viewGroupList.removeFirst()
            if (viewGroup is SideSlideMenuLayout) return viewGroup
            for (child in viewGroup.children) {
                if (child is SideSlideMenuLayout) return child
                if (child is ViewGroup) {
                    viewGroupList.add(child)
                }
            }
        }
        return null
    }
}