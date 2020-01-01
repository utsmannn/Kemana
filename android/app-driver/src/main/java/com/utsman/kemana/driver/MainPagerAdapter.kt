package com.utsman.kemana.driver

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@SuppressLint("WrongConstant")
class MainPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    fun addFragments(vararg fragments: Fragment) {
        this.fragments.addAll(fragments)
    }
}