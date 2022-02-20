package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.databinding.FragmentExerciseAnalysisBinding
import com.mobile.gympraaktis.ui.details.view.AnalysisFragment
import com.mobile.gympraaktis.ui.details.view.DetailsActivity
import com.mobile.gympraaktis.ui.main.adapter.ExerciseAnalysisAdapter
import com.mobile.gympraaktis.ui.main.vm.ExerciseAnalysisViewModel
import timber.log.Timber

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
                DetailsActivity.start(activity, AnalysisFragment.TAG)
                    .putExtra(AnalysisFragment.TAG, it)
            )
        }
        binding.rvAnalysis.adapter = adapter

        mViewModel.observeDashboard().observe(viewLifecycleOwner) {
            if (it != null) adapter.submitList(it.analysis)
        }
    }
}