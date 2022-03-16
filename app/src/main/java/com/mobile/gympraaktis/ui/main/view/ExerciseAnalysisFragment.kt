package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentExerciseAnalysisBinding
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.details.view.ExerciseAnalysisDetailsFragment
import com.mobile.gympraaktis.ui.main.adapter.ExerciseAnalysisAdapter
import com.mobile.gympraaktis.ui.main.vm.ExerciseAnalysisViewModel

class ExerciseAnalysisFragment(override val layoutId: Int = R.layout.fragment_exercise_analysis) :
    BaseFragment<FragmentExerciseAnalysisBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            ExerciseAnalysisFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override val mViewModel: ExerciseAnalysisViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        val adapter = ExerciseAnalysisAdapter {
            startActivity(
                DetailsActivity.start(activity, ExerciseAnalysisDetailsFragment.TAG)
                    .putExtra(ExerciseAnalysisDetailsFragment.TAG, it)
            )
        }
        binding.rvAnalysis.adapter = adapter

        mViewModel.observeRoutineAnalysis().observe(viewLifecycleOwner) {
            if (it != null) adapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rvAnalysis.requestLayout()
    }
}