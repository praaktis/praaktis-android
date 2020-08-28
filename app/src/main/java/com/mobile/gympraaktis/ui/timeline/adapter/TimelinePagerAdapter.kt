package com.mobile.gympraaktis.ui.timeline.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mobile.gympraaktis.domain.entities.TimelineDTO
import com.mobile.gympraaktis.ui.timeline.view.TimelineItemFragment

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