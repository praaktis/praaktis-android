package com.mobile.gympraaktis.ui.details.view

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.AnalysisComplete
import com.mobile.gympraaktis.data.entities.PlayerAnalysis
import com.mobile.gympraaktis.databinding.FragmentExercisAnalysisDetailsBinding
import com.mobile.gympraaktis.domain.extension.addFragment
import com.mobile.gympraaktis.ui.details.adapter.AnalysisAdapter
import com.mobile.gympraaktis.ui.details.adapter.AnalysisItem
import com.mobile.gympraaktis.ui.details.adapter.HeaderAdapter
import com.mobile.gympraaktis.ui.details.vm.DetailsViewModel
import com.mobile.gympraaktis.ui.details.vm.ExerciseAnalysisDetailsViewModel

class PlayerAnalysisDetailsFragment(override val layoutId: Int = R.layout.fragment_exercis_analysis_details) :
    BaseFragment<FragmentExercisAnalysisDetailsBinding>() {

    companion object {
        const val TAG = "PlayerAnalysisDetailsFragment"

        @JvmStatic
        fun newInstance(analysis: PlayerAnalysis) =
            PlayerAnalysisDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("ANALYSIS", analysis)
                }
            }
    }

    override val mViewModel: ExerciseAnalysisDetailsViewModel by viewModels()
    private val detailsViewModel: DetailsViewModel by activityViewModels()

    private val analysis by lazy { requireArguments().getSerializable("ANALYSIS") as PlayerAnalysis }

    override fun initUI(savedInstanceState: Bundle?) {
        detailsViewModel.changeTitle(getString(R.string.player_analysis))

        val adapter = AnalysisAdapter<AnalysisComplete> {
            activity.addFragment {
                add(
                    R.id.container,
                    AnalysisFragment.getInstance(it.returnItem),
                    AnalysisFragment.TAG
                )
                addToBackStack(AnalysisFragment.TAG)
            }
        }

        binding.rvAnalysis.adapter =
            ConcatAdapter(HeaderAdapter(analysis.playerEntity.name), adapter)


        adapter.submitList(analysis.analysisComplete.map {
            AnalysisItem(
                it.analysisEntity.name,
                it.analysisEntity.averageScore.toFloat(),
                it.analysisEntity.maxScore.toFloat(),
                returnItem = it
            )
        })


    }
}