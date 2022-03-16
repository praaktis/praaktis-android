package com.mobile.gympraaktis.ui.challenge

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.AttemptEntity
import com.mobile.gympraaktis.databinding.FragmentDetailedAnalysisBinding
import com.mobile.gympraaktis.databinding.LayoutTargetTimelineBinding
import com.mobile.gympraaktis.domain.common.AnalysisLineChart
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.entities.DetailPoint
import com.mobile.gympraaktis.domain.entities.DetailScoreDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.challenge.vm.DetailAnalysisFragmentViewModel
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import kotlinx.android.synthetic.main.fragment_detailed_analysis.*
import timber.log.Timber
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class DetailAnalysisFragment constructor(override val layoutId: Int = R.layout.fragment_detailed_analysis) :
    BaseFragment<FragmentDetailedAnalysisBinding>() {

    companion object {
        const val TAG = "DetailAnalysisFragment"

        @JvmStatic
        fun getInstance(): Fragment = DetailAnalysisFragment()

        @JvmStatic
        fun getInstance(score: AttemptEntity): Fragment {
            val fragment = DetailAnalysisFragment()
            val bundle = Bundle()
            bundle.putSerializable("score", score)
            fragment.arguments = bundle
            return fragment
        }

        @JvmStatic
        fun getInstance(challengeItem: ChallengeDTO): Fragment {
            val fragment = DetailAnalysisFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: DetailAnalysisFragmentViewModel by viewModels()

    private val scoreDTO by lazy { arguments?.getSerializable("score") as AttemptEntity }
    private val challengeItem by lazy { arguments?.getSerializable("challengeItem") as ChallengeDTO }
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }

    override fun initUI(savedInstanceState: Bundle?) {
        initToolbar()
        if (arguments?.get("score") != null) {
            binding.cvSelectPlayer.show()
            mViewModel.getDetailResult(scoreDTO.attemptId) // from remote
        } else {
            binding.cvSelectPlayer.hide()
            setDetail(collectDetailScores()) // from praaktis_sdk
        }
        mViewModel.detailResultEvent.observe(viewLifecycleOwner) {
            setDetail(it)
            startGuideIfNecessary(it.size)
        }
    }

    private fun collectDetailScores(): List<DetailScoreDTO> {
        val scoresMap = TreeMap<Int, DetailScoreDTO>()
//        val detailScores: MutableList<DetailScoreDTO> = mutableListOf()

        result?.forEach { (key, value) ->
            when (value) {
                is com.praaktis.exerciseengine.Engine.DetailPoint -> {
                    if (key != "Overall") {
                        scoresMap[value.priority] = DetailScoreDTO(
                            DetailPoint(value.id, key),
                            value.value,
                            if(value.maxValue != null) value.maxValue else 100f,
                        )
                        /*detailScores.add(
                            DetailScoreDTO(
                                DetailPoint(value.id, key),
                                value.value.toDouble()
                            )
                        )*/
                    }
                }
            }
        }

        return scoresMap.map { it.value }
    }

    private fun initToolbar() {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            if (parentFragmentManager.backStackEntryCount >= 1)
                parentFragmentManager.popBackStack()
            else activity.finish()
        }
    }

    private fun setDetail(detailScores: List<DetailScoreDTO>) {
        if (arguments?.getSerializable("score") != null) binding.tvExerciseName.text = scoreDTO.challengeName
        else binding.tvExerciseName.text = challengeItem.name

//        var gradientIterator = GRADIENT_PROGRESS_ARRAY.listIterator()

        for (i in detailScores.indices) {
//            val progressBackground =
//                if (gradientIterator.hasNext())
//                    gradientIterator.next()
//                else {
//                    gradientIterator = GRADIENT_PROGRESS_ARRAY.listIterator()
//                    gradientIterator.next()
//                }

            Timber.d(detailScores.toString())

            val chart = AnalysisLineChart(
                requireContext(),
                value = detailScores[i].detailPointScore,
                title = detailScores[i].detailPoint.name,
                progressBackground = R.drawable.gradient_progress,
                maxValue = detailScores[i].maxValue
            )
            llAnalysisContainer.addView(chart)
        }

        startGuideIfNecessary(detailScores.size)
    }

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate

    private fun startGuideIfNecessary(listSize: Int) {
        if (!AppGuide.isGuideDone(TAG)) {
            if (listSize > 0) {
                AppGuide.setGuideDone(TAG)
                binding.topPanel.doOnPreDraw {
                    spotlight.start()
                }
            }
        }
        binding.ivInfo.setOnClickListener {
            restartSpotlight()
        }
    }

    private fun restartSpotlight() {
        if (spotlightDelegate.isInitialized())
            spotlightDelegate.reset()
        spotlight.start()
    }

    private fun closeSpotlight() {
        spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(detailsTarget())
            .setBackgroundColor(R.color.primaryColor_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    binding.ivInfo.showAnimWithScale()
                }
            })
            .build()
    }

    private fun detailsTarget(): Target {
        val target = LayoutTargetTimelineBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
//        target.customText.updateLayoutParams<ConstraintLayout.LayoutParams> { updateMargins(top = binding.toolbar.height + 340.dp) }
        target.customText.text =
            "Shows your Score for each of the Key Aspects of the Challenge, as well as your Overall Score and the Count of your repetitions"

        val viewLocation = IntArray(2)
        binding.toolbar.getLocationOnScreen(viewLocation)

        return Target.Builder()
            /*.setAnchor(
                viewLocation[0] + binding.llAnalysisContainer.width / 2f,
                viewLocation[1].toFloat() + 220.dp
            )*/
            .setOverlay(target.root)
            /*.setShape(
                RoundedRectangle(
                    320.dp.toFloat(),
                    binding.llAnalysisContainer.width.toFloat() - 32.dp,
                    4.dp.toFloat()
                )
            )*/
            .build()
    }

    override fun onDetach() {
        closeSpotlight()
        super.onDetach()
    }


}