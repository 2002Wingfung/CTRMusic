package com.example.center.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.center.module.discovery.DiscoveryFragment
import com.example.center.module.home.HomeFragment
import com.example.center.module.mine.MineFragment

fun ViewPager2.initMain(fragmentActivity: FragmentActivity): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = false
    this.offscreenPageLimit = 3
    //设置适配器
    adapter = object : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()


                1 -> DiscoveryFragment()


                else -> MineFragment()

            }
        }
        override fun getItemCount() = 3
    }
    return this
}