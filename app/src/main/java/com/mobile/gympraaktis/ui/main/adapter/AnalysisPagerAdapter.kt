package com.mobile.gympraaktis.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.gympraaktis.ui.main.view.ExerciseAnalysisFragment
import com.mobile.gympraaktis.ui.main.view.PlayerAnalysisFragment

class AnalysisPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExerciseAnalysisFragment.newInstance()
            else -> PlayerAnalysisFragment.newInstance()
        }
    }
}