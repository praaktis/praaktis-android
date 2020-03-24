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
import androidx.lifecycle.ViewModelProviders
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
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
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
        fun getInstance(challengeItem: ChallengeItem): Fragment {
            val fragment = DetailAnalysisFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: DetailAnalysisFragmentViewModel
        get() = getViewModel { DetailAnalysisFragmentViewModel(activity.application) }

    private lateinit var mainViewModel: MainViewModel

    private val scoreDTO by lazy { arguments?.getSerializable("score") as ScoreDTO }
    private val challengeItem by lazy { arguments?.getSerializable("challengeItem") as ChallengeItem }
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }

    override fun initUI(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        initToolbar()
        if (arguments?.get("score") != null) {
            mViewModel.getDetailResult(scoreDTO.attemptId)
        } else {
            val transformData: (challenges: List<ChallengeDTO>) -> Unit = {
                val challenge = it.find { item ->
                    item.name.equals(challengeItem.label)
                }
                val detailScores = mutableListOf<DetailScoreDTO>()

                /* // old solution
                var i = 0
                for (detailPoint in challenge!!.detailPoints) {
                    val detailPoint = DetailPoint(0, detailPoint.label)
                    if (result != null)
                        detailScores.add(DetailScoreDTO(detailPoint, result[i++].toDouble()))
                    else
                        detailScores.add(DetailScoreDTO(detailPoint, 0.0))
                }*/

                // new one
                collectDetailScores().forEach { detailScore ->
                    detailScores.add(
                        DetailScoreDTO(
                            DetailPoint(0, detailScore.first),
                            detailScore.second.toDouble()
                        )
                    )
                }
                setDetail(detailScores)
            }

            if (mainViewModel.challengesEvent.value != null) {
                transformData(mainViewModel.challengesEvent.value!!)
            } else {
                mainViewModel.getChallenges()
                mainViewModel.challengesEvent.observe(this, Observer {
                    transformData(it)
                })
            }
        }
        mViewModel.detailResultEvent.observe(this, Observer {
            setDetail(it)
        })
    }

    private fun collectDetailScores(): MutableList<Pair<String, Float>> {
        val detailScores: MutableList<Pair<String, Float>> = mutableListOf()
        result?.forEach { (key, value) ->
            when (value) {
                is com.praaktis.exerciseengine.Engine.DetailPoint -> {
                    if (key != "Overall") {
                        detailScores.add(Pair(key, value.value)) // key = label, value = score
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
        else tvDragFlick.text = challengeItem.label
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