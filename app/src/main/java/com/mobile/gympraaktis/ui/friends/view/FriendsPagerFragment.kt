package com.mobile.gympraaktis.ui.friends.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.databinding.FragmentFriendsPagerBinding
import com.mobile.gympraaktis.databinding.LayoutTargetChallengesBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.extension.doOnPreDraw
import com.mobile.gympraaktis.domain.extension.hideInvisibleAnimWithScale
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.showAnimWithScale
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import kotlinx.android.synthetic.main.fragment_friends_pager.*

class FriendsPagerFragment constructor(override val layoutId: Int = R.layout.fragment_friends_pager) :
    BaseFragment<FragmentFriendsPagerBinding>() {

    companion object {
        const val TAG = "FriendsPagerFragment"
        fun getInstance(): Fragment = FriendsPagerFragment()
    }

    override val mViewModel: BaseViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        tlFriends.setupWithViewPager(vpFriends)
        vpFriends.adapter = FriendsPagerAdapter(childFragmentManager)

        ivAddFriends.onClick {
            InviteFriendsActivity.start(activity)
        }


        startGuideIfNecessary()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private inner class FriendsPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            if (position == 0)
                return FriendsFragment.getInstance()
            return FriendsRequestFragment.getInstance()
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0) return getString(R.string.my_friends).toUpperCase()
            return getString(R.string.requests).toUpperCase()
        }

    }


    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.root.doOnPreDraw {
                spotlight.start()
            }
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

    private fun closeSpotlight() {
        if (isGuideStarted)
            spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(challengeTarget())
            .setBackgroundColor(R.color.primaryColor_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                    binding.ivInfo.hideInvisibleAnimWithScale()
                }

                override fun onEnded() {
                    isGuideStarted = false
                    binding.ivInfo.showAnimWithScale()
                }
            })
            .build()
    }

    private fun challengeTarget(): Target {
        val target = LayoutTargetChallengesBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Invite Friends to join you by supplying their email address"

        return Target.Builder()
            .setOverlay(target.root)
            .build()
    }
}