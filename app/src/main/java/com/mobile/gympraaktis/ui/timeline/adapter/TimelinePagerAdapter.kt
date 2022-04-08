package com.mobile.gympraaktis.ui.timeline.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mobile.gympraaktis.domain.entities.TimelineDTO

class TimelinePagerAdapter (fragmentManager: FragmentManager,
                            val timelineDTO: TimelineDTO): FragmentPagerAdapter(fragmentManager) {



    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }

    override fun getItem(position: Int): Fragment {
        TODO("Not yet implemented")
    }
}