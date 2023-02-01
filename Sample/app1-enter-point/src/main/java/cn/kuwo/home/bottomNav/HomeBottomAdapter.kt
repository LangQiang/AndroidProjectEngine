package cn.kuwo.home.bottomNav

import android.util.Pair
import android.view.View
import androidx.fragment.app.Fragment
import cn.kuwo.home.bottomNav.HomeBottomItemView
import com.godq.compose.botnav.BaseNavAdapter
import com.godq.compose.botnav.BottomItemData


/**
 * @author  GodQ
 * @date  2023/1/29 7:07 下午
 */
class HomeBottomAdapter(private val data: List<Pair<BottomItemData, Fragment>>): BaseNavAdapter {

    override fun getWeight(): Float {
        return 1f
    }

    override fun getItem(container: View, position: Int): View {
        return HomeBottomItemView(container.context, data[position].first)
    }

    override fun getCount(): Int {
        return data.size
    }

}