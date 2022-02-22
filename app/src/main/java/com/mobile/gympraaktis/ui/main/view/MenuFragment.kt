package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentMenuBinding
import com.mobile.gympraaktis.databinding.LayoutTargetTimelineBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.login.view.LoginActivity
import com.mobile.gympraaktis.ui.main.vm.MenuViewModel
import com.mobile.gympraaktis.ui.new_player.view.NewPlayerFragment
import com.mobile.gympraaktis.ui.settings.view.SettingsFragment
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle


class MenuFragment constructor(override val layoutId: Int = R.layout.fragment_menu) :
    BaseFragment<FragmentMenuBinding>() {

    companion object {
        const val TAG = "MenuFragment"
    }

    override val mViewModel: MenuViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.menuSettings.onClick {
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

        /*binding.menuFriends.onClick {
            closeSpotlight()
            activity.addFragment {
                add(
                    R.id.menu_container,
                    FriendsPagerFragment.getInstance(),
                    FriendsPagerFragment.TAG
                )
                addToBackStack(FriendsPagerFragment.TAG)
            }
        }*/

        binding.menuActivateNewPlayer.setOnClickListener {
            closeSpotlight()
            activity.addFragment {
                add(
                    R.id.menu_container,
                    NewPlayerFragment.newInstance(),
                    NewPlayerFragment.TAG
                )
                addToBackStack(TAG)
            }
        }

        binding.menuMyProfile.onClick {
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
        /*binding.menuLogout.onClick {
            closeSpotlight()
            activity.materialAlert {
                setMessage(getString(R.string.are_you_sure_logout))
                setPositiveButton(R.string.ok) { dialog, which ->
                    mViewModel.logout()
                }
                setNegativeButton(R.string.cancel) { dialog, which -> }
            }.show()
        }
*/
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
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.llTopMenu.doOnPreDraw {
                spotlight.start()
            }
        }
    }

    private fun closeSpotlight() {
        if (isGuideStarted)
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
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                }

                override fun onEnded() {
                    isGuideStarted = false
                }
            })
            .build()
    }

    private fun menuTarget(): Target {
        val target = LayoutTargetTimelineBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Edit your Profile, amend Settings and invite Friends to join you"

        val rvLocation = IntArray(2)
        binding.llTopMenu.getLocationOnScreen(rvLocation)

        target.customText.updatePadding(top = rvLocation[1] + binding.llTopMenu.height)

        return Target.Builder()
            .setAnchor(binding.llTopMenu)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.llTopMenu.height.toFloat(),
                    binding.llTopMenu.width.toFloat(),
                    4.dp.toFloat()
                )
            )
            .build()
    }
}