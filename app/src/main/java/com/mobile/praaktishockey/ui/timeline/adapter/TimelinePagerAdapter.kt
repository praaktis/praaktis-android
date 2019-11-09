package com.mobile.praaktishockey.ui.timeline.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import com.mobile.praaktishockey.ui.timeline.view.TimelineItemFragment

class TimelinePagerAdapter (fragmentManager: FragmentManager,
                            val timelineDTO: TimelineDTO): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        if (position == 0) return TimelineItemFragment.getInstance(timelineDTO)
        return TimelineItemFragment.getInstance(timelineDTO.challenges[position - 1])
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }
}