package com.mobile.praaktishockey.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.domain.common.Constants
import com.mobile.praaktishockey.domain.common.PraaktisBottomNavigationView
import com.mobile.praaktishockey.domain.entities.TimelineDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.friends.view.FriendsPagerFragment
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import com.mobile.praaktishockey.ui.settings.view.SettingsFragment
import com.mobile.praaktishockey.ui.timeline.view.TimelineFragment
import com.mobile.praaktishockey.ui.timeline.view.TimelineItemFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity constructor(override val layoutId: Int = R.layout.activity_main) : BaseActivity(),
    FragmentManager.OnBackStackChangedListener {

    companion object {
        @JvmField
        val TAG = MainActivity::class.java.simpleName

        @JvmStatic
        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override val mViewModel: MainViewModel?
        get() = getViewModel { MainViewModel(application) }

    private var notificationBadge: View? = null

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel?.getChallenges()
        mViewModel?.checkFcmToken()
//        mViewModel?.
//        toolbar.setOnApplyWindowInsetsListener { v, insets ->
//            v.updateLayoutParams<FrameLayout.LayoutParams> {
//                topMargin = insets.systemWindowInsetTop
//            }
//            tv_title.updateLayoutParams<FrameLayout.LayoutParams> {
//                topMargin = insets.systemWindowInsetTop / 2
//            }
//            insets
//        }

        supportFragmentManager.addOnBackStackChangedListener(this)
//        setSupportActionBar(toolbar)
//        supportActionBar?.apply {
//            setHomeButtonEnabled(false)
//            setDisplayHomeAsUpEnabled(false)
//            setDisplayShowHomeEnabled(false)
//        }
//        toolbar.setNavigationOnClickListener { onBackPressed() }

        mViewModel?.let {
            it.title.observe(this, Observer { title ->
                changeTitle(title)
            })
        }

//        bottom_navigation.apply {
//            setOnNavigationItemSelectedListener(this@MainActivity)
//            selectedItemId = R.id.menu_dashboard
//        }
        setMoreItemBadge()
        val tag = DashboardFragment.TAG
        showOrReplace(tag) {
            add(
                R.id.container,
                DashboardFragment(),
                tag
            )
        }
        bottomNavigation.setNavigationListener(object : PraaktisBottomNavigationView.HockeyBottomNavigationListener {
            override fun setOnNavigationItemSelected(itemPosition: Int) {
                val currentFragment = getVisibleFragment()
                when (itemPosition) {
                    3 -> {
                        if (supportFragmentManager.findFragmentById(R.id.menu_container) !is MenuFragment) {
                            if (supportFragmentManager.findFragmentById(R.id.menu_container) is FriendsPagerFragment
                                || supportFragmentManager.findFragmentById(R.id.menu_container) is ProfileFragment
                                || supportFragmentManager.findFragmentById(R.id.menu_container) is SettingsFragment
                            ) {
                                onBackPressed()
                                return
                            } else
                                addFragment {
                                    add(
                                        R.id.menu_container,
                                        MenuFragment(),
                                        MenuFragment.TAG
                                    )
                                    addToBackStack(MenuFragment.TAG)
                                }
                        } else return
                    }
                    0 -> {
                        if (currentFragment == null || currentFragment !is DashboardFragment) {
                            val tag = DashboardFragment.TAG
                            showOrReplace(tag, currentFragment) {
                                replace(
                                    R.id.container,
                                    DashboardFragment(),
                                    tag
                                )
                            }
                            changeTitle(getString(R.string.dashboard))
                        }
                    }
                    1 -> {
                        if (currentFragment == null || currentFragment !is NewChallengeFragment) {
                            val tag = NewChallengeFragment.TAG
                            showOrReplace(tag, currentFragment) {
                                replace(
                                    R.id.container,
                                    NewChallengeFragment.getInstance(),
                                    tag
                                )
                            }
                            changeTitle(getString(R.string.new_challenge))
                        }
                    }
                    2 -> {
                        if (currentFragment == null || currentFragment !is TimelineItemFragment) {
                            val tag = TimelineItemFragment.TAG
                            showOrReplace(tag, currentFragment) {
                                replace(
                                    R.id.container,
                                    TimelineItemFragment(),
                                    tag
                                )
                            }
                            changeTitle(getString(R.string.timeline))
                        }
                    }
//                    2 -> {
//                        if (currentFragment == null || currentFragment !is TimelineFragment) {
//                            val tag = TimelineFragment.TAG
//                            showOrReplace(tag, currentFragment) {
//                                replace(
//                                    R.id.container,
//                                    TimelineFragment(),
//                                    tag
//                                )
//                            }
//                            changeTitle(getString(R.string.timeline))
//                        }
//                    }
                }
                // close Menu after click
                if (supportFragmentManager.findFragmentById(R.id.menu_container) != null) {
                    supportFragmentManager.popBackStackImmediate(
                        MenuFragment.TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    if (itemPosition == 3) {
                        when (currentFragment) {
                            is DashboardFragment -> {
//                        bottom_navigation.selectedItemId = R.id.menu_dashboard
                            }
                            is NewChallengeFragment -> {
//                        bottom_navigation.selectedItemId = R.id.menu_new_challenge
                            }
                            else -> {
//                        bottom_navigation.selectedItemId = R.id.menu_timeline
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setMoreItemBadge() {
//        val menuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(3) as BottomNavigationItemView
//        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false)
//        itemView.addView(notificationBadge)
    }

    override fun onBackStackChanged() {

    }

    override fun onBackPressed() {
        val currentFragment = getVisibleFragment()
        if (supportFragmentManager.findFragmentById(R.id.menu_container) != null
            && supportFragmentManager.findFragmentById(R.id.menu_container) is MenuFragment
        ) {
            supportFragmentManager.popBackStackImmediate(
                MenuFragment.TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            return when (currentFragment) {
                is DashboardFragment -> {
//                    bottom_navigation.selectedItemId = R.id.menu_dashboard
                }
                is NewChallengeFragment -> {
//                    bottom_navigation.selectedItemId = R.id.menu_new_challenge
                }
                else -> {
//                    bottom_navigation.selectedItemId = R.id.menu_timeline
                }
            }
        }
        super.onBackPressed()
    }

    private fun changeTitle(title: String) {
        tv_title.text = title
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}