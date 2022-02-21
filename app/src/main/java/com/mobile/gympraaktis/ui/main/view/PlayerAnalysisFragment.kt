package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentExerciseAnalysisBinding
import com.mobile.gympraaktis.ui.details.view.AnalysisFragment
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.main.adapter.PlayersAnalysisAdapter
import com.mobile.gympraaktis.ui.main.vm.PlayersAnalysisViewModel

class PlayerAnalysisFragment(override val layoutId: Int = R.layout.fragment_exercise_analysis) :
    BaseFragment<FragmentExerciseAnalysisBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerAnalysisFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override val mViewModel: PlayersAnalysisViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.getFriends()
        val adapter = PlayersAnalysisAdapter {
            startActivity(
                DetailsActivity.start(activity, AnalysisFragment.TAG)
                    .putExtra(AnalysisFragment.TAG, it)
            )
        }
        binding.rvAnalysis.adapter = adapter
        mViewModel.observeFriends().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}