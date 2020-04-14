package com.mobile.praaktishockey.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseActivity
import com.mobile.praaktishockey.databinding.ActivityMainBinding
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.friends.view.FriendsPagerFragment
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import com.mobile.praaktishockey.ui.settings.view.SettingsFragment
import com.mobile.praaktishockey.ui.timeline.view.TimelineItemFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity constructor(override val layoutId: Int = R.layout.activity_main) :
    BaseActivity<ActivityMainBinding>(), FragmentManager.OnBackStackChangedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

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
        transparentStatusAndNavigationBar()
        setLightNavigationBar()

        mViewModel?.getChallenges()
        mViewModel?.checkFcmToken()

        supportFragmentManager.addOnBackStackChangedListener(this)

        binding.bottomNavigation.apply {
            setOnNavigationItemSelectedListener(this@MainActivity)
            selectedItemId = R.id.menu_dashboard
        }
        setMoreItemBadge()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragment = getVisibleFragment()
        when (item.itemId) {
            R.id.menu_dashboard -> {
                if (currentFragment == null || currentFragment !is DashboardFragment) {
                    val tag = DashboardFragment.TAG
                    supportFragmentManager.switch(R.id.container, DashboardFragment(), tag)
                }
            }
            R.id.menu_new_challenge -> {
                if (currentFragment == null || currentFragment !is NewChallengeFragment) {
                    val tag = NewChallengeFragment.TAG
                    supportFragmentManager.switch(R.id.container, NewChallengeFragment.getInstance(), tag)
                }
            }
            R.id.menu_timeline -> {
                if (currentFragment == null || currentFragment !is TimelineItemFragment) {
                    val tag = TimelineItemFragment.TAG
                    supportFragmentManager.switch(R.id.container, TimelineItemFragment(), tag)
                }
            }
            R.id.menu_more -> {
                if (supportFragmentManager.findFragmentById(R.id.menu_container) !is MenuFragment) {
                    if (supportFragmentManager.findFragmentById(R.id.menu_container) is FriendsPagerFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is ProfileFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is SettingsFragment
                    ) {
                        onBackPressed()
                        return true
                    } else
                        addFragment {
                            add(
                                R.id.menu_container,
                                MenuFragment(),
                                MenuFragment.TAG
                            )
                            addToBackStack(MenuFragment.TAG)
                        }
                } else return true
            }
        }

        // close Menu after click
        if (supportFragmentManager.findFragmentById(R.id.menu_container) != null) {
            supportFragmentManager.popBackStackImmediate(
                MenuFragment.TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        return true
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
                    bottom_navigation.selectedItemId = R.id.menu_dashboard
                }
                is NewChallengeFragment -> {
                    bottom_navigation.selectedItemId = R.id.menu_new_challenge
                }
                else -> {
                    bottom_navigation.selectedItemId = R.id.menu_timeline
                }
            }
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }


}