package com.mobile.praaktishockey.ui.details.view


import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentChallengeInstructionBinding
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.updateLayoutParams
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import kotlinx.android.synthetic.main.fragment_challenge_instruction.*

class ChallengeInstructionFragment(override val layoutId: Int = R.layout.fragment_challenge_instruction) :
    BaseFragment<FragmentChallengeInstructionBinding>() {

    companion object {
        val TAG: String = ChallengeInstructionFragment::class.java.simpleName
        const val CHALLENGE_ITEM = "ANALYSIS_ITEM"
        const val CHALLENGE_RESULT = "CHALLENGE_RESULT"
        const val VIDEO_PATH = "VIDEO_PATH"

        fun getInstance(item: ChallengeDTO) = ChallengeInstructionFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CHALLENGE_ITEM, item)
            }
        }
    }

    override val mViewModel: BaseViewModel
        get() = getViewModel { BaseViewModel(Application()) }

    private val challengeItem by lazy { arguments!!.getSerializable(CHALLENGE_ITEM) as ChallengeDTO }
    private val autoStartAnimator by lazy { ValueAnimator.ofFloat(0f, 1f) }

    override fun initUI(savedInstanceState: Bundle?) {
        if (getActivity() is AppCompatActivity)
            (getActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
        tvtitle.text = challengeItem.name
        binding.viewInstructions.setInstructions(
            listOf(
                getString(R.string.best_results_need_2_people_one_to_perform_the_challenge_one_to_record_video),
                getString(R.string.performer_should_stand_approx_4_metres_from_the_camera_performer_should_have_right_side_facing_camera_arms_in_front_horizontal_and_level_with_the_shoulder),
                getString(R.string.recorder_should_line_up_camera_so_that_performer_is_entirely_within_the_four_corners_identified_with_4_red_corner_markers),
                getString(R.string.within_5_seconds_the_red_markers_will_turn_green_recorder_should_then_tell_the_performer_to_begin_the_challenge),
                getString(R.string.in_order_to_get_most_accurate_results_it_is_recommended_that_the_performer_completes_5_10_repetitions_of_the_challenge_before_stopping_the_video)
            )
        )

        tv_start_challenge.setOnClickListener {
            autoStartAnimator.pause()
            startChallengeSteps()
        }
        initAutoStart()
    }

    private fun initAutoStart() {
        autoStartAnimator.duration = 10000
        autoStartAnimator.addUpdateListener {
            val v = it.animatedValue as Float
            if (vAutoStart == null) autoStartAnimator.pause()
            vAutoStart.updateLayoutParams<LinearLayout.LayoutParams> {
                weight = v
            }
        }
        autoStartAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                startChallengeSteps()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
    }

    private fun startChallengeSteps() {
        if (getActivity() != null) {
//            ChallengeActivity.start(getActivity()!!, challengeItem)
            val intent = Intent(context, ExerciseEngineActivity::class.java)
            intent.putExtra("LOGIN", mViewModel.getLogin())
            intent.putExtra("PASSWORD", mViewModel.getPassword())
            intent.putExtra("EXERCISE", challengeItem.id)
            startActivityForResult(intent, 333)
        }
    }

    override fun onStart() {
        super.onStart()
        autoStartAnimator.start()
    }

    override fun onStop() {
        super.onStop()
        autoStartAnimator.pause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("__RESULT", "Request: $requestCode")
        if (requestCode == 333) {
            getActivity()?.finish()
            if (resultCode == Activity.RESULT_OK) {
                @Suppress("UNCHECKED_CAST")
                val result = data!!.getSerializableExtra("result") as HashMap<String, Any>?
                Log.d("__RESULT", "Result: $result")
                ChallengeActivity.start(
                    getActivity()!!,
                    challengeItem,
                    result,
                    data.getStringExtra(VIDEO_PATH)
                )
            }
        }
    }
}
