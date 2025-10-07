package com.tendertool.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tendertool.app.AnalyticsFragment

class AnalyticsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity)  {
    private val pages: List<Fragment> = listOf(AnalyticsFragment())

    override fun getItemCount(): Int = pages.size
    override fun createFragment(position: Int): Fragment = pages[position]
}