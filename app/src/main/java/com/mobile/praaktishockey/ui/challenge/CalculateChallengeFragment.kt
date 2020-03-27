package com.mobile.praaktishockey.ui.challenge

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.countTo
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.showOrReplace
import com.mobile.praaktishockey.ui.challenge.vm.CalculateChallengeFragmentViewModel
import kotlinx.android.synthetic.main.fragment_calculate.*

class CalculateChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_calculate) :
    BaseFragment() {

    companion object {
        @JvmField
        val TAG = CalculateChallengeFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(challengeItem: ChallengeDTO): Fragment {
            val fragment = CalculateChallengeFragment()
            val bundle = Bundle()
            bundle.putSerializable("challengeItem", challengeItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: CalculateChallengeFragmentViewModel
        get() = getViewModel { CalculateChallengeFragmentViewModel(activity.application) }

    private val challengeItem by lazy { arguments!!.getSerializable("challengeItem") as ChallengeDTO }

    override fun initUI(savedInstanceState: Bundle?) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { activity.finish() }

        tvCalculateProcent.countTo(100, "%", 3000)
        Handler().postDelayed({
            val tag = ResultChallengeFragment.TAG
            activity.showOrReplace(tag) {
                replace(
                    R.id.container,
                    ResultChallengeFragment.getInstance(challengeItem), tag
                )
            }
//            (activity as ChallengeActivity).changeTitle("Your result")
        }, 3000)
    }
}