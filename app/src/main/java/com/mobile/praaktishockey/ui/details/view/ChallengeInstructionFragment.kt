package com.mobile.praaktishockey.ui.details.view


import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.replaceFragment
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.main.adapter.ChallengeItem
import com.praaktis.exerciseengine.ExerciseEngineActivity
import kotlinx.android.synthetic.main.fragment_challenge_instruction.*

class ChallengeInstructionFragment(override val layoutId: Int = R.layout.fragment_challenge_instruction) :
    BaseFragment() {

    companion object {
        val TAG: String = ChallengeInstructionFragment::class.java.simpleName
        const val CHALLENGE_ITEM = "ANALYSIS_ITEM"
        const val CHALLENGE_RESULT = "CHALLENGE_RESULT"
        const val VIDEO_PATH = "VIDEO_PATH"

        fun getInstance(item: ChallengeItem) = ChallengeInstructionFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CHALLENGE_ITEM, item)
            }
        }
    }

    override val mViewModel: BaseViewModel
        get() = getViewModel { BaseViewModel(Application()) }

    private val challengeItem by lazy { arguments!!.getSerializable(CHALLENGE_ITEM) as ChallengeItem }
    private val autoStartAnimator by lazy { ValueAnimator.ofFloat(0f, 1f) }

    override fun initUI(savedInstanceState: Bundle?) {
        if (getActivity() is AppCompatActivity)
            (getActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
        tvtitle.text = getString(challengeItem.name)
        tv_start_challenge.setOnClickListener {
            autoStartAnimator.pause()
            startChallengeSteps()
        }
        initAutoStart()
    }

    private fun initAutoStart() {
        autoStartAnimator.duration = 7500
        autoStartAnimator.addUpdateListener {
            val v = it.animatedValue as Float
            val lp = LinearLayout.LayoutParams(0, 5, v)
            if (vAutoStart == null) autoStartAnimator.pause()
            vAutoStart?.layoutParams = lp
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
        if (requestCode == 333) {
            getActivity()?.finish()
            if (resultCode == Activity.RESULT_OK) {
                ChallengeActivity.start(getActivity()!!, challengeItem, data!!.getFloatArrayExtra("result"), data.getStringExtra(
                    VIDEO_PATH))
            }
        }
    }
}
