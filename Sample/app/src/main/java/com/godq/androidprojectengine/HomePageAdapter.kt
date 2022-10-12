package com.godq.androidprojectengine

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.godq.compose.botnav.BottomItemData

/**
 * Created by tc :)
 */
class HomePageAdapter(fm: FragmentManager, fragments: List<Pair<BottomItemData, Fragment>>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val mFragments: List<Pair<BottomItemData, Fragment>> = fragments

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position].second
    }



    fun getFragment(position: Int): Fragment? {
        return mFragments[position].second
    }

    fun Resume() {
        for (pair in mFragments) {
            if (pair.second != null && pair.second.isAdded) {
                pair.second.onResume()
            }
        }
    }

    fun Pause() {
        for (pair in mFragments) {
            if (pair.second != null && pair.second.isAdded) {
                pair.second.onPause()
            }
        }
    }

    fun isMainTab(f: Fragment): Boolean {
        for (pair in mFragments) {
            if (f === pair.second) {
                return true
            }
        }
        return false
    }

}