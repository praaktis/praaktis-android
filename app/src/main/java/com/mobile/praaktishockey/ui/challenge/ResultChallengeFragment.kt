package com.mobile.praaktishockey.ui.challenge

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentResultChallengeBinding
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.entities.DetailResult
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.challenge.vm.ResultChallengeFragmentViewModel
import com.mobile.praaktishockey.ui.details.view.ChallengeInstructionFragment
import com.praaktis.exerciseengine.Engine.DetailPoint
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import com.praaktis.exerciseengine.RawPlayer.H264RawPlayerActivity
import kotlinx.android.synthetic.main.fragment_result_challenge.*
import timber.log.Timber
import java.util.*

class ResultChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_result_challenge) :
    BaseFragment<FragmentResultChallengeBinding>() {

    companion object {
        @JvmField
        val TAG = ResultChallengeFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(challengeItem: ChallengeDTO): Fragment {
            val fragment = ResultChallengeFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ResultChallengeFragmentViewModel
        get() = getViewModel { ResultChallengeFragmentViewModel(activity.application) }

    private val challengeItem by lazy { requireArguments().getSerializable("challengeItem") as ChallengeDTO }
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }
    private val path by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.RAW_VIDEO_PATH) }
    private val pathTest by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.VIDEO_PATH) }

    private var mediaPlayer2: MediaPlayer? = MediaPlayer()

    override fun initUI(savedInstanceState: Bundle?) {
        initToolbar()
        initVideoView()
        initClicks()
    }

    private fun initToolbar() {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { activity.finish() }
    }

    private fun initVideoView() {

        if (result != null) {
            val detailResults = collectDetailResults()
            val scoreOverAll =
                getOverallScore()
            tvYourScore.text =
                "Your score: ${scoreOverAll.toInt()}"
            mViewModel.storeResult(
                challengeItem,
                score = scoreOverAll,
                detailResults = detailResults
            )
        } else {
            tvYourScore.text = "Your score: 0"
        }

    }

    private fun getOverallScore(): Float {
        val overall = result?.get("Overall") as DetailPoint?
        return overall?.value ?: 0f
    }

    private fun collectDetailResults(): MutableList<DetailResult> {
        val detailResults: MutableList<DetailResult> = mutableListOf()
        result?.forEach { (key, value) ->
            when (value) {
                is DetailPoint -> {
                    detailResults.add(
                        DetailResult(
                            value.id,
                            value.value
                        )
                    )
                }
            }
        }
        return detailResults
    }

    private fun initClicks() {
        Timber.d("FILEPATH " + path)
        Timber.d("FILEPATH_TEST " + pathTest)
        Timber.d("RESULT  " + Gson().toJson(result))

        // todo : sdk player integration
        binding.ivPlay.setOnClickListener {
            val intent = Intent(activity, H264RawPlayerActivity::class.java)
            intent.putExtra("FILE_NAME", path)
            intent.putExtra("PLAYER", result)
            startActivity(intent)
        }

        cvDetailAnalysis.onClick {
            if (result != null) {
                ivPlay.show()
                val tag = DetailAnalysisFragment.TAG
                activity.showOrReplace(tag) {
                    add(R.id.container, DetailAnalysisFragment.getInstance(challengeItem), tag)
                        .addToBackStack(tag)
                }
            } else {
                activity.makeToast("Failed exercise")
            }
        }
        cvTryAgain.onClick {
            val intent = Intent(context, ExerciseEngineActivity::class.java)
            intent.putExtra("LOGIN", mViewModel.getLogin())
            intent.putExtra("PASSWORD", mViewModel.getPassword())
            intent.putExtra("EXERCISE", challengeItem.id)
            requireActivity().startActivityForResult(intent, 333)
        }
    }

}