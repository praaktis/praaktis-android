package com.mobile.gympraaktis.ui.details.view


import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.databinding.FragmentChallengeInstructionBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.challenge.ChallengeActivity
import com.mobile.gympraaktis.ui.login.view.LoginActivity
import com.mobile.gympraaktis.ui.main.vm.MenuViewModel
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.fragment_challenge_instruction.*
import timber.log.Timber

class ChallengeInstructionFragment(override val layoutId: Int = R.layout.fragment_challenge_instruction) :
    BaseFragment<FragmentChallengeInstructionBinding>() {

    companion object {
        const val TAG: String = "ChallengeInstructionFragment"
        const val CHALLENGE_ITEM = "ANALYSIS_ITEM"
        const val CHALLENGE_RESULT = "CHALLENGE_RESULT"
        const val RAW_VIDEO_PATH = "RAW_VIDEO_PATH"
        const val VIDEO_PATH = "VIDEO_PATH"
        const val SINGLE_USER_MODE = "SINGLE_USER_MODE"
        const val SERVER_NAME = "SERVER_NAME"
        const val VIDEO_ID = "VIDEO_ID"
        private const val AUTO_START_DURATION = 20000L

        fun getInstance(item: ChallengeDTO, player: PlayerEntity) =
            ChallengeInstructionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CHALLENGE_ITEM, item)
                    putSerializable("player", player)
                }
            }
    }

    override val mViewModel: MenuViewModel by viewModels()

    private val challengeItem by lazy { requireArguments().getSerializable(CHALLENGE_ITEM) as ChallengeDTO }
    private val player by lazy { requireArguments().getSerializable("player") as PlayerEntity }
    private val autoStartAnimator by lazy { ValueAnimator.ofFloat(0f, 1f) }

    override fun initUI(savedInstanceState: Bundle?) {
        if (getActivity() is AppCompatActivity)
            (getActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
        binding.tvTitle.text = challengeItem.name

        binding.tgMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                autoStartAnimator.start()
                changeInstruction(checkedId)
            }
        }
        val defaultCheckedMode =
            if (mViewModel.settingsStorage.cameraMode) R.id.btn_single else R.id.btn_multi
        binding.tgMode.check(defaultCheckedMode)

        tv_start_challenge.setOnClickListener {
            autoStartAnimator.pause()
            startChallengeSteps()
        }

        startGuideIfNecessary()

        mViewModel.logoutEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("HERELOGOUT", "LOGOUT")
                mViewModel.onLogoutSuccess()

                LoginActivity.startAndFinishAll(activity)
                activity.finish()
            }
        })

    }

    private fun changeInstruction(checkedId: Int) {
        when (checkedId) {
            R.id.btn_single -> {
                mViewModel.settingsStorage.cameraMode = true
                Timber.i("SINGLE")
                binding.viewInstructions.setInstructions(
                    challengeItem.instructions?.single ?: emptyList()
                )
            }
            R.id.btn_multi -> {
                mViewModel.settingsStorage.cameraMode = false
                Timber.i("MULTI")
                binding.viewInstructions.setInstructions(
                    challengeItem.instructions?.multiple ?: emptyList()
                )
            }
        }
    }

    private fun initAutoStart() {
        autoStartAnimator.duration = AUTO_START_DURATION
        autoStartAnimator.addUpdateListener {
            val v = it.animatedValue as Float
            if (vAutoStart == null)
                autoStartAnimator.pause()
            else
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
            val intent = Intent(context, ExerciseEngineActivity::class.java)
            intent.putExtra("EXERCISE", challengeItem.id)
            intent.putExtra("PLAYER", player.id)
            intent.putExtra(SINGLE_USER_MODE, mViewModel.settingsStorage.cameraMode)
            startActivityForResult(intent, ChallengeActivity.PRAAKTIS_SDK_REQUEST_CODE)
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
        if (requestCode == ChallengeActivity.PRAAKTIS_SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    getActivity()?.finish()
                    @Suppress("UNCHECKED_CAST")
                    val result = data!!.getSerializableExtra("result") as HashMap<String, Any>?
                    Timber.d("Result: $result")
                    ChallengeActivity.start(
                        requireActivity(),
                        challengeItem,
                        result,
                        data.getStringExtra(RAW_VIDEO_PATH),
                        data.getStringExtra(VIDEO_PATH),
                        data.getStringExtra(VIDEO_ID),
                        player
                    )
                }
                ExerciseEngineActivity.AUTHENTICATION_FAILED -> {
                    Timber.d("LOGOUT EVENT : AUTHENTICATION_FAILED")
                    mViewModel.logout()
                }
                ExerciseEngineActivity.CALIBRATION_FAILED -> {
                    showErrorDialog("Calibration failed, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.POOR_CONNECTION -> {
                    showErrorDialog("Poor connection, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.CANNOT_REACH_SERVER -> {
                    showErrorDialog("Cannot reach server, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                ExerciseEngineActivity.SMTH_WENT_WRONG -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
                Activity.RESULT_CANCELED -> {

                }
                else -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        materialAlert({
            setCancelable(false)
            setMessage(message)
            setPositiveButton(R.string.try_again) { dialog, which ->
                startChallengeSteps()
            }
            setNegativeButton(R.string.cancel) { dialog, which ->
                activity.finish()
            }
        }, {
            autoStartAnimator.pause()
        })?.show()
    }

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.tgMode.doOnPreDraw {
                spotlight.start()
            }
        } else {
            initAutoStart()
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

    private fun nextTarget() {
        spotlight.next()
    }

    private fun closeSpotlight() {
        if (isGuideStarted)
            spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(personModeTarget(), letMeGoTarget())
            .setBackgroundColor(R.color.primaryColor_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                    binding.ivInfo.hideAnimWithScale()
                    autoStartAnimator.pause()
                }

                override fun onEnded() {
                    isGuideStarted = false
                    binding.ivInfo.showAnimWithScale()
                    autoStartAnimator.start()
                    initAutoStart()
                }
            })
            .build()
    }

    private fun personModeTarget(): Target {
        val target = LayoutTargetBinding.inflate(layoutInflater)
        target.closeTarget.setOnClickListener { nextTarget() }
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.customText.text =
            "You can switch modes of the recording by selecting one person mode or two people mode"
        target.customText.updatePadding(top = binding.tgMode.y.toInt() + binding.tgMode.height)

        return Target.Builder()
            .setAnchor(binding.tgMode)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.tgMode.height.toFloat() + 4.dp,
                    binding.tgMode.width.toFloat() + 4.dp,
                    4.dp.toFloat()
                )
            )
            .build()
    }

    private fun letMeGoTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.closeTarget.setOnClickListener { nextTarget() }
        target.customText.text = "Start the exercise"
        target.root.updatePadding(bottom = binding.tvStartChallenge.height + 16.dp)

        return Target.Builder()
            .setAnchor(binding.tvStartChallenge)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.tvStartChallenge.height.toFloat(),
                    binding.tvStartChallenge.width.toFloat(),
                    24.dp.toFloat()
                )
            )
            .build()
    }

    override fun onDetach() {
        super.onDetach()
        closeSpotlight()
    }

}
