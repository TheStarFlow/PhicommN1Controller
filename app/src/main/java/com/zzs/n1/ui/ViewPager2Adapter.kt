package com.zzs.n1.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class ViewPager2Adapter(aty: FragmentActivity) : FragmentStateAdapter(aty) {
    private val fragments by lazy {
        arrayListOf(
            BluetoothFragment.newInstance(),
            WifiFragment.newInstance()
        )
    }


    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}