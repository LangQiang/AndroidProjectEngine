package com.godq.compose.botnav

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment

class BottomNavAdapter(private val data: List<Pair<BottomItemData, Fragment>>?) : BaseNavAdapter {

    override fun getWeight(): Float {
        return 1f
    }

    override fun getItem(container: View, position: Int): View {
        return BottomTabView(container.context, data?.get(position)?.first)
    }

    override fun getCount(): Int {
        return data?.size ?: 0
    }

}