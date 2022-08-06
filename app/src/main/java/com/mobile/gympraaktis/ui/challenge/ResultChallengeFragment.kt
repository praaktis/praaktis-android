package com.mobile.gympraaktis.ui.challenge

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.data.entities.RoutineEntity
import com.mobile.gympraaktis.databinding.FragmentResultChallengeBinding
import com.mobile.gympraaktis.domain.entities.DetailResult
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.show
import com.mobile.gympraaktis.domain.extension.showOrReplace
import com.mobile.gympraaktis.ui.challenge.vm.ResultChallengeFragmentViewModel
import com.mobile.gympraaktis.ui.details.view.ChallengeInstructionFragment
import com.praaktis.exerciseengine.Engine.DetailPoint
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import com.praaktis.exerciseengine.Engine.Measurement
import com.praaktis.exerciseengine.Player.VideoReplayActivity
import kotlinx.android.synthetic.main.fragment_result_challenge.*
import timber.log.Timber

class ResultChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_result_challenge) :
    BaseFragment<FragmentResultChallengeBinding>() {

    companion object {
        const val TAG = "ResultChallengeFragment"

        @JvmStatic
        fun getInstance(challengeItem: RoutineEntity, player: PlayerEntity): Fragment {
            val fragment = ResultChallengeFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            bundle.putSerializable("player", player)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ResultChallengeFragmentViewModel by viewModels()

    private val challengeItem by lazy { requireArguments().getSerializable("challengeItem") as RoutineEntity }
    private val player by lazy { requireArguments().getSerializable("player") as PlayerEntity }
    private val result by lazy { activity.intent.getSerializableExtra(ChallengeInstructionFragment.CHALLENGE_RESULT) as HashMap<String, Any>? }
    private val path by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.RAW_VIDEO_PATH) }
    private val pathTest by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.VIDEO_PATH) }
    private val videoId by lazy { activity.intent.getStringExtra(ChallengeInstructionFragment.VIDEO_ID) }

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
            val measurements = collectMeasurements()
            val scoreOverAll =
                getOverallScore()
            tvYourScore.text =
                "Your score: ${scoreOverAll.toInt()}"
            Timber.d("STORE RESULTS CALLED")
            mViewModel.storeResult(
                challengeItem,
                score = scoreOverAll,
                detailResults = detailResults,
                videoId = videoId,
                player = player,
                measurements = measurements
            )
        } else {
            tvYourScore.text = "Your score: 0"
        }

    }

    private fun getOverallScore(): Float {
        val overall = result?.get("OVERALL") as DetailPoint?
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
                            value.value,
                            if (value.maxValue >= 0) value.maxValue else 100f
                        )
                    )
                }
            }
        }
        return detailResults
    }

    private fun collectMeasurements(): List<Measurement> {
        val measurements = mutableListOf<Measurement>()
        result?.forEach { (key, value) ->
            when (value) {
                is Measurement -> {
                    measurements.add(value)
                }
            }
        }
        return measurements
    }

    private fun initClicks() {
        binding.ivPlay.setOnClickListener {
            val intent = Intent(activity, VideoReplayActivity::class.java)
            intent.putExtra("PLAYER", 1)
            intent.putExtra("EXERCISE", challengeItem.id)
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
            startExercise()
        }
    }

    fun startExercise() {
        val intent = Intent(context, ExerciseEngineActivity::class.java)
        intent.putExtra("EXERCISE", challengeItem.id)
        intent.putExtra("PLAYER", player.id)
        intent.putExtra(
            ChallengeInstructionFragment.SINGLE_USER_MODE,
            mViewModel.settingsStorage.cameraMode
        )
        requireActivity().startActivityForResult(
            intent,
            ChallengeActivity.PRAAKTIS_SDK_REQUEST_CODE
        )
    }

}