package com.mobile.praaktishockey.ui.main.view

import android.app.Application
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.databinding.LayoutTargetTimelineBinding
import com.mobile.praaktishockey.domain.common.AppGuide
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.friends.view.FriendsPagerFragment
import com.mobile.praaktishockey.ui.login.view.LoginActivity
import com.mobile.praaktishockey.ui.main.vm.MenuViewModel
import com.mobile.praaktishockey.ui.settings.view.SettingsFragment
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment constructor(override val layoutId: Int = R.layout.fragment_menu) :
    BaseFragment() {

    companion object {
        val TAG = MenuFragment::class.java.simpleName
    }

    override val mViewModel: MenuViewModel
        get() = getViewModel { MenuViewModel(Application()) }

    override fun initUI(savedInstanceState: Bundle?) {
        menu_settings.onClick {
            closeSpotlight()
            activity.addFragment {
                add(
                    R.id.menu_container,
                    SettingsFragment.getInstance(),
                    SettingsFragment.TAG
                )
                addToBackStack(SettingsFragment.TAG)
            }
        }

        menu_friends.onClick {
            closeSpotlight()
            activity.addFragment {
                add(
                    R.id.menu_container,
                    FriendsPagerFragment.getInstance(),
                    FriendsPagerFragment.TAG
                )
                addToBackStack(FriendsPagerFragment.TAG)
            }
        }

        menu_my_profile.onClick {
            closeSpotlight()
            activity.addFragment {
                add(
                    R.id.menu_container,
                    ProfileFragment(),
                    ProfileFragment.TAG
                )
                addToBackStack(TAG)
            }
        }
        menu_logout.onClick {
            closeSpotlight()
            activity.materialAlert {
                setMessage(getString(R.string.are_you_sure_logout))
                setPositiveButton(R.string.ok) { dialog, which ->
                    mViewModel.logout()
                }
                setNegativeButton(R.string.cancel) { dialog, which -> }
            }.show()
        }

        mViewModel.logoutEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("HERELOGOUT", "LOGOUT")
                mViewModel.onLogoutSuccess()

                LoginActivity.startAndFinishAll(activity)
                activity.finish()
            }
        })

        startGuideIfNecessary()
    }

    private val spotlight by lazy { initGuide() }

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            ll_top_menu.doOnPreDraw {
                spotlight.start()
            }
        }
    }

    private fun closeSpotlight() {
        spotlight.finish()
    }

    override fun onDetach() {
        closeSpotlight()
        super.onDetach()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(menuTarget())
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
            .build()
    }

    private fun menuTarget(): Target {
        val target = LayoutTargetTimelineBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Check your profile, invite Friends to join you so you can compare performance, complete settings and Log Out at the end of your session "

        val rvLocation = IntArray(2)
        ll_top_menu.getLocationOnScreen(rvLocation)

        val rvVisibleRect = Rect()
        ll_top_menu.getLocalVisibleRect(rvVisibleRect)

        target.customText.updatePadding(top = rvLocation[1] + ll_top_menu.height)

        return Target.Builder()
            .setAnchor(ll_top_menu)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    ll_top_menu.height.toFloat(),
                    ll_top_menu.width.toFloat(),
                    4.dp.toFloat()
                )
            )
            .build()
    }
}