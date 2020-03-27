package com.mobile.praaktishockey.ui.challenge

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.AnalysisLineChart
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.entities.DetailPoint
import com.mobile.praaktishockey.domain.entities.DetailScoreDTO
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.extension.dpToPx
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.challenge.vm.DetailAnalysisFragmentViewModel
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import kotlinx.android.synthetic.main.fragment_detailed_analysis.*

class DetailAnalysisFragment constructor(override val layoutId: Int = R.layout.fragment_detailed_analysis) :
    BaseFragment() {

    companion object {
        @JvmField
        val TAG = DetailAnalysisFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = DetailAnalysisFragment()

        @JvmStatic
        fun getInstance(score: ScoreDTO): Fragment {
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

    private val scoreDTO by lazy { arguments?.getSerializable("score") as ScoreDTO }
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
            if (fragmentManager?.backStackEntryCount!! >= 1)
                fragmentManager?.popBackStack()
            else activity.finish()
        }
    }

    private fun setDetail(detailScores: List<DetailScoreDTO>) {
        val tvDragFlick = AppCompatTextView(context)
        if (arguments?.getSerializable("score") != null) tvDragFlick.text = scoreDTO.name
        else tvDragFlick.text = challengeItem.name
        tvDragFlick.isAllCaps = true
        tvDragFlick.setTextSize(Dimension.SP, 17f)
        tvDragFlick.setTextColor(ContextCompat.getColor(context!!, R.color.black_text))
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.CENTER_HORIZONTAL
        lp.setMargins(0, context!!.dpToPx(5), 0, context!!.dpToPx(5))
        tvDragFlick.layoutParams = lp
        llAnalysisContainer.addView(tvDragFlick)

        for (i in 0 until detailScores.size) {
            val chart = AnalysisLineChart(
                context!!,
                detailScores[i].detailPointScore.toFloat(),
                detailScores[i].detailPoint.name
            )
            llAnalysisContainer.addView(chart)
        }
    }
}