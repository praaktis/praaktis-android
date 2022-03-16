package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentExerciseAnalysisBinding
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.details.view.PlayerAnalysisDetailsFragment
import com.mobile.gympraaktis.ui.main.adapter.PlayersAnalysisAdapter
import com.mobile.gympraaktis.ui.main.vm.ExerciseAnalysisViewModel
import timber.log.Timber

class PlayerAnalysisFragment(override val layoutId: Int = R.layout.fragment_exercise_analysis) :
    BaseFragment<FragmentExerciseAnalysisBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerAnalysisFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    //    override val mViewModel: PlayersAnalysisViewModel by viewModels()
    override val mViewModel: ExerciseAnalysisViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {

        val adapter = PlayersAnalysisAdapter {
            startActivity(
                DetailsActivity.start(activity, PlayerAnalysisDetailsFragment.TAG)
                    .putExtra(PlayerAnalysisDetailsFragment.TAG, it)
            )
        }

        mViewModel.observePlayerAnalysis().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            Timber.d("DASHBOARD_ENTITY2 $it")
        }

        binding.rvAnalysis.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        binding.rvAnalysis.requestLayout()
    }

}