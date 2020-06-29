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
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.PraaktisApp
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentChallengeInstructionBinding
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.updateLayoutParams
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.login.view.LoginActivity
import com.mobile.praaktishockey.ui.main.vm.MenuViewModel
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import kotlinx.android.synthetic.main.fragment_challenge_instruction.*
import timber.log.Timber

class ChallengeInstructionFragment(override val layoutId: Int = R.layout.fragment_challenge_instruction) :
    BaseFragment<FragmentChallengeInstructionBinding>() {

    companion object {
        val TAG: String = ChallengeInstructionFragment::class.java.simpleName
        const val CHALLENGE_ITEM = "ANALYSIS_ITEM"
        const val CHALLENGE_RESULT = "CHALLENGE_RESULT"
        const val RAW_VIDEO_PATH = "RAW_VIDEO_PATH"
        const val VIDEO_PATH = "VIDEO_PATH"
        const val SINGLE_USER_MODE = "SINGLE_USER_MODE"
        const val SERVER_NAME = "SERVER_NAME"
        private const val AUTO_START_DURATION = 20000L

        fun getInstance(item: ChallengeDTO) = ChallengeInstructionFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CHALLENGE_ITEM, item)
            }
        }
    }

    override val mViewModel: MenuViewModel get() = getViewModel { MenuViewModel(PraaktisApp.getApplication()) }

    private val challengeItem by lazy { requireArguments().getSerializable(CHALLENGE_ITEM) as ChallengeDTO }
    private val autoStartAnimator by lazy { ValueAnimator.ofFloat(0f, 1f) }

    override fun initUI(savedInstanceState: Bundle?) {
        if (getActivity() is AppCompatActivity)
            (getActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
        tvtitle.text = challengeItem.name

        binding.tgMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked)
                changeInstruction(checkedId)
        }
        val defaultCheckedMode = if (mViewModel.settingsStorage.cameraMode) R.id.btn_single else R.id.btn_multi
        binding.tgMode.check(defaultCheckedMode)

        tv_start_challenge.setOnClickListener {
            autoStartAnimator.pause()
            startChallengeSteps()
        }
        initAutoStart()

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
            intent.putExtra(SINGLE_USER_MODE, mViewModel.settingsStorage.cameraMode)
//            intent.putExtra(SERVER_NAME, mViewModel.settingsStorage.praaktisServerName)
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
        Log.d("__RESULT", "Request: $requestCode")
        if (requestCode == ChallengeActivity.PRAAKTIS_SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    getActivity()?.finish()
                    @Suppress("UNCHECKED_CAST")
                    val result = data!!.getSerializableExtra("result") as HashMap<String, Any>?
                    Log.d("__RESULT", "Result: $result")
                    ChallengeActivity.start(
                        requireActivity(),
                        challengeItem,
                        result,
                        data.getStringExtra(RAW_VIDEO_PATH),
                        data.getStringExtra(VIDEO_PATH)
                    )
                }
                ExerciseEngineActivity.AUTHENTICATION_FAILED -> {
                    Timber.d("LOGOUT EVENT : AUTHENTICATION_FAILED")
                    mViewModel.logout()
                }
                ExerciseEngineActivity.CALIBRATION_FAILED, ExerciseEngineActivity.POOR_CONNECTION -> {
                    getActivity()?.finish()
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK ${data?.getSerializableExtra("result")}")
                }
                else -> {
                    getActivity()?.finish()
                    Log.d("__RESULT", "Result NOT OK $resultCode")
                    Log.d("__RESULT", "Result NOT OK ${data?.getSerializableExtra("result")}")
                }
            }
        }
    }
}
