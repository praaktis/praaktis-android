package com.mobile.praaktishockey.ui.challenge

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.data.entities.TimelineEntity
import com.mobile.praaktishockey.databinding.FragmentDetailedAnalysisBinding
import com.mobile.praaktishockey.databinding.LayoutTargetTimelineBinding
import com.mobile.praaktishockey.domain.common.AnalysisLineChart
import com.mobile.praaktishockey.domain.common.AppGuide
import com.mobile.praaktishockey.domain.common.GRADIENT_PROGRESS_ARRAY
import com.mobile.praaktishockey.domain.common.resettableLazy
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.entities.DetailPoint
import com.mobile.praaktishockey.domain.entities.DetailScoreDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.vm.DetailAnalysisFragmentViewModel
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.fragment_detailed_analysis.*

class DetailAnalysisFragment constructor(override val layoutId: Int = R.layout.fragment_detailed_analysis) :
    BaseFragment<FragmentDetailedAnalysisBinding>() {

    companion object {
        @JvmField
        val TAG = DetailAnalysisFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = DetailAnalysisFragment()

        @JvmStatic
        fun getInstance(score: TimelineEntity): Fragment {
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

    override val mViewModel: DetailAnalysisFragmentViewModel
        get() = getViewModel { DetailAnalysisFragmentViewModel(activity.application) }

    private val scoreDTO by lazy { arguments?.getSerializable("score") as TimelineEntity }
    private val challengeItem by lazy { arguments?.getSerializable("challengeItem") as ChallengeDTO }
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }

    override fun initUI(savedInstanceState: Bundle?) {
        initToolbar()
        if (arguments?.get("score") != null) {
            mViewModel.getDetailResult(scoreDTO.attemptId) // from remote
        } else {
            setDetail(collectDetailScores()) // from praaktis_sdk
        }
        mViewModel.detailResultEvent.observe(this, Observer {
            setDetail(it)
            startGuideIfNecessary(it.size)
        })
    }

    private fun collectDetailScores(): MutableList<DetailScoreDTO> {
        val detailScores: MutableList<DetailScoreDTO> = mutableListOf()
        result?.forEach { (key, value) ->
            when (value) {
                is com.praaktis.exerciseengine.Engine.DetailPoint -> {
                    if (key != "Overall") {
                        detailScores.add(
                            DetailScoreDTO(
                                DetailPoint(value.id, key),
                                value.value.toDouble()
                            )
                        )
                    }
                }
            }
        }
        return detailScores
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
        val tvDragFlick = AppCompatTextView(context)
        if (arguments?.getSerializable("score") != null) tvDragFlick.text = scoreDTO.challengeName
        else tvDragFlick.text = challengeItem.name
        tvDragFlick.isAllCaps = true
        tvDragFlick.setTextSize(Dimension.SP, 18f)
        tvDragFlick.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_900_1))
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.CENTER_HORIZONTAL
        lp.setMargins(0, 0, 0, 12.dp)
        tvDragFlick.layoutParams = lp
        llAnalysisContainer.addView(tvDragFlick)

        var gradientIterator = GRADIENT_PROGRESS_ARRAY.listIterator()

        for (i in detailScores.indices) {
            val progressBackground =
                if (gradientIterator.hasNext())
                    gradientIterator.next()
                else {
                    gradientIterator = GRADIENT_PROGRESS_ARRAY.listIterator()
                    gradientIterator.next()
                }

            val chart = AnalysisLineChart(
                requireContext(),
                detailScores[i].detailPointScore.toFloat(),
                detailScores[i].detailPoint.name,
                progressBackground
            )
            llAnalysisContainer.addView(chart)
        }
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
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
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
        target.customText.updateLayoutParams<ConstraintLayout.LayoutParams> { updateMargins(top = binding.toolbar.height + 340.dp) }
        target.customText.text =
            "Shows your performance on each of the key aspects of the Challenge"

        val viewLocation = IntArray(2)
        binding.toolbar.getLocationOnScreen(viewLocation)

        return Target.Builder()
            .setAnchor(
                viewLocation[0] + binding.llAnalysisContainer.width / 2f,
                viewLocation[1].toFloat() + 220.dp
            )
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    320.dp.toFloat(),
                    binding.llAnalysisContainer.width.toFloat() - 32.dp,
                    4.dp.toFloat()
                )
            )
            .build()
    }


}