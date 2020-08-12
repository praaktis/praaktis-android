package com.mobile.praaktishockey.ui.details.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseActivity
import com.mobile.praaktishockey.data.entities.AnalysisComplete
import com.mobile.praaktishockey.databinding.ActivityDetailsBinding
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.details.vm.DetailsViewModel
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity constructor(override val layoutId: Int = R.layout.activity_details) :
    BaseActivity<ActivityDetailsBinding>() {

    companion object {

        const val INITIAL_FRAGMENT_TAG = "initialFragment"

        fun start(activity: AppCompatActivity, initialFragmentTag: String) =
            Intent(activity, DetailsActivity::class.java).apply {
                putExtra(INITIAL_FRAGMENT_TAG, initialFragmentTag)
            }
    }

    override val mViewModel: DetailsViewModel? get() = getViewModel { DetailsViewModel(application) }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
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
                val fragment = AnalysisFragment.getInstance(
                    intent.getSerializableExtra(
                        AnalysisFragment.TAG
                    ) as AnalysisComplete
                )
                replaceFragment(AnalysisFragment.TAG) {
                    replace(
                        R.id.container,
                        fragment,
                        AnalysisFragment.TAG
                    )
                }

                binding.ivInfo.show()
                binding.ivInfo.setOnClickListener {
                    fragment.restartSpotlight()
                }
            }
            else -> throw IllegalArgumentException("Wrong fragment")
        }
    }

    fun hideInfo() {
        binding.ivInfo.hideAnimWithScale()
    }

    fun showInfo() {
        binding.ivInfo.showAnimWithScale()
    }

    private fun changeTitle(title: String) {
        tv_title.text = title
    }
}
