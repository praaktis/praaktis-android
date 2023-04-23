package com.mobile.gympraaktis.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.databinding.FragmentSecondBinding
import com.mobile.gympraaktis.domain.Constants
import com.mobile.gympraaktis.domain.extension.setLightStatusBar
import com.mobile.gympraaktis.domain.model.RoutinesList
import com.mobile.gympraaktis.ui.main.adapter.AnalysisExpandableAdapter
import com.mobile.gympraaktis.ui.main.adapter.AnalysisItem
import com.praaktis.exerciseengine.Engine.Outputs.DetailPoint
import com.praaktis.exerciseengine.Player.VideoReplayActivity
import java.util.*

class ExerciseResultFragment : Fragment() {

    companion object {
        const val TAG: String = "ExerciseResultFragment"
        const val RESULT = "RESULT"

        @JvmStatic
        fun newInstance(result: HashMap<String, Any>) =
            ExerciseResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RESULT, result)
                }
            }
    }

    private val result by lazy {
        requireArguments().getSerializable(RESULT) as HashMap<String, Any>
    }

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.setLightStatusBar()

        binding.btnBackHome.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnRate.setOnClickListener {
            RateRoutineDialogFragment.newInstance(result)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        binding.btnReplay.setOnClickListener {
            val intent = Intent(activity, VideoReplayActivity::class.java)
            intent.putExtra("PLAYER", 1)
            intent.putExtra("EXERCISE", Constants.ROUTINE_ID)
            startActivity(intent)
        }

        val routine = PraaktisApp.routine
        val detailPoints = routine?.detailPoints.orEmpty()

        binding.rvAnalysis.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)
                    ?.let { setDrawable(it) }
            }
        )

        val adapter =
            AnalysisExpandableAdapter<DetailPoint> {

            }
        binding.rvAnalysis.adapter = adapter

        adapter.submitList(collectDetailScores(detailPoints))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectDetailScores(detailPoints: List<RoutinesList.Routine.DetailPoint>): List<AnalysisItem<DetailPoint>> {
        val scoresMap =
            TreeMap<Int, AnalysisItem<DetailPoint>>()
        result.forEach { (key, value) ->
            when (value) {
                is DetailPoint -> {
                    scoresMap[value.priority] = AnalysisItem(
                        value.id,
                        key.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        value.value,
                        value.maxValue,
                        additionalText = detailPoints.find { value.id == it.id }?.helpText.orEmpty(),
                        returnItem = value,
                    )
                }
            }
        }
        return scoresMap.map {
            it.value
        }
    }
}