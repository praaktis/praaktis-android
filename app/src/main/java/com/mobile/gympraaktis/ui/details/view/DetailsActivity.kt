package com.mobile.gympraaktis.ui.details.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.data.entities.AnalysisComplete
import com.mobile.gympraaktis.data.entities.PlayerAnalysis
import com.mobile.gympraaktis.data.entities.RoutineAnalysis
import com.mobile.gympraaktis.databinding.ActivityDetailsBinding
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.details.vm.DetailsViewModel
import com.mobile.gympraaktis.ui.subscription_plans.view.SubscriptionPlansFragment
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

    override val mViewModel: DetailsViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mViewModel.let {
            it.title.observe(this) { title ->
                changeTitle(title)
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

            if (currentFragment is AnalysisFragment) {
                binding.ivInfo.setOnClickListener {
                    currentFragment.restartSpotlight()
                }
                binding.ivInfo.showAnimWithScale()
            } else {
                binding.ivInfo.setOnClickListener(null)
                binding.ivInfo.hide()
            }
        }

        when (intent.getStringExtra(INITIAL_FRAGMENT_TAG)) {
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

                binding.ivInfo.setOnClickListener {
                    fragment.restartSpotlight()
                }
                binding.ivInfo.showAnimWithScale()
            }
            ExerciseAnalysisDetailsFragment.TAG -> {
                replaceFragment(ExerciseAnalysisDetailsFragment.TAG) {
                    replace(
                        R.id.container,
                        ExerciseAnalysisDetailsFragment.newInstance(
                            intent.getSerializableExtra(ExerciseAnalysisDetailsFragment.TAG) as RoutineAnalysis
                        )
                    )
                }
            }
            PlayerAnalysisDetailsFragment.TAG -> {
                replaceFragment(PlayerAnalysisDetailsFragment.TAG) {
                    replace(
                        R.id.container,
                        PlayerAnalysisDetailsFragment.newInstance(
                            intent.getSerializableExtra(PlayerAnalysisDetailsFragment.TAG) as PlayerAnalysis
                        )
                    )
                }
            }
            SubscriptionPlansFragment.TAG -> {
                replaceFragment(SubscriptionPlansFragment.TAG) {
                    replace(
                        R.id.container,
                        SubscriptionPlansFragment.newInstance(),
                        SubscriptionPlansFragment.TAG
                    )
                }
            }
            else -> throw IllegalArgumentException("Wrong fragment")
        }
    }

    fun hideInfo() {
        binding.ivInfo.hideAnimWithScale()
    }

    fun showInfo() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is AnalysisFragment)
            binding.ivInfo.showAnimWithScale()
    }

    private fun changeTitle(title: String) {
        tv_title.text = title
    }
}