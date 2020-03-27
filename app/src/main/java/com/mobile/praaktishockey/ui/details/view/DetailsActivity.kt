package com.mobile.praaktishockey.ui.details.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.entities.AnalysisDTO
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.entities.DashboardDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.replaceFragment
import com.mobile.praaktishockey.domain.extension.updateLayoutParams
import com.mobile.praaktishockey.ui.details.vm.DetailsViewModel
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity constructor(override val layoutId: Int = R.layout.activity_details) :
    BaseActivity() {

    companion object {

        const val INITIAL_FRAGMENT_TAG = "initialFragment"

        fun start(activity: AppCompatActivity, initialFragmentTag: String) =
            Intent(activity, DetailsActivity::class.java).apply {
                putExtra(INITIAL_FRAGMENT_TAG, initialFragmentTag)
            }
    }

    override val mViewModel: DetailsViewModel? get() = getViewModel { DetailsViewModel(application) }

    override fun initUI(savedInstanceState: Bundle?) {
        toolbar.setOnApplyWindowInsetsListener { v, insets ->
            v.updateLayoutParams<FrameLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            tv_title.updateLayoutParams<FrameLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop / 2
            }
            insets
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mViewModel?.let {
            it.title.observe(this, Observer { title ->
                changeTitle(title)
            })
        }

        when (intent.getStringExtra(INITIAL_FRAGMENT_TAG)) {
            ChallengeInstructionFragment.TAG -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                replaceFragment(ChallengeInstructionFragment.TAG) {
                    replace(
                        R.id.container,
                        ChallengeInstructionFragment.getInstance(
                            intent.getSerializableExtra(
                                ChallengeInstructionFragment.TAG
                            ) as ChallengeDTO
                        ),
                        ChallengeInstructionFragment.TAG
                    )
                }
                changeTitle(
                    (intent.getSerializableExtra(ChallengeInstructionFragment.TAG) as ChallengeDTO).name
                )
            }
            AnalysisFragment.TAG -> {
                replaceFragment(AnalysisFragment.TAG) {
                    replace(
                        R.id.container,
                        AnalysisFragment.getInstance(
                            intent.getSerializableExtra(
                                AnalysisFragment.TAG
                            ) as AnalysisDTO,
                            intent.getSerializableExtra(
                                AnalysisFragment.CHALLENGES
                            ) as DashboardDTO
                        ),
                        AnalysisFragment.TAG
                    )
                }
            }
            else -> throw IllegalArgumentException("Wrong fragment")
        }
    }

    private fun changeTitle(title: String) {
        tv_title.text = title
    }
}
