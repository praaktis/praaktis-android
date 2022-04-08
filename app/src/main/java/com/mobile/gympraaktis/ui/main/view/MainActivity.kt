package com.mobile.gympraaktis.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMargins
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.VideoPreloadWorker
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.databinding.ActivityMainBinding
import com.mobile.gympraaktis.databinding.LayoutTargetBottomBinding
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.friends.view.FriendsPagerFragment
import com.mobile.gympraaktis.ui.main.vm.MainViewModel
import com.mobile.gympraaktis.ui.new_player.view.NewPlayerFragment
import com.mobile.gympraaktis.ui.new_player.view.NewPlayerProfileFragment
import com.mobile.gympraaktis.ui.settings.view.SettingsFragment
import com.mobile.gympraaktis.ui.timeline.view.TimelineItemFragment
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        fun startAndFinishAll(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    override val mViewModel: MainViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }

        mViewModel.getChallenges()
        mViewModel.checkFcmToken()

        supportFragmentManager.addOnBackStackChangedListener(this)

        binding.bottomNavigation.post {
            with(binding.bottomNavigation) {
                setOnNavigationItemSelectedListener(this@MainActivity)
                selectedItemId = R.id.menu_dashboard
            }
        }
    }

    fun schedulePreloadWork(videoUrl: String) {
        val workManager = WorkManager.getInstance(PraaktisApp.getApplication())
        val videoPreloadWorker = VideoPreloadWorker.buildWorkRequest(videoUrl)
        workManager.enqueueUniqueWork(
            videoUrl,
            ExistingWorkPolicy.KEEP,
            videoPreloadWorker
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragment = getVisibleFragment()
        if (currentFragment is DashboardFragment) {
            currentFragment.closeSpotlight()
        }

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
                    supportFragmentManager.switch(
                        R.id.container,
                        NewChallengeFragment.getInstance(),
                        tag
                    )
                }
            }
            R.id.menu_timeline -> {
                if (currentFragment == null || currentFragment !is TimelineItemFragment) {
                    val tag = TimelineItemFragment.TAG
                    supportFragmentManager.switch(
                        R.id.container,
                        TimelineItemFragment.getInstance(),
                        tag
                    )
                }
            }
            R.id.menu_more -> {
                if (supportFragmentManager.findFragmentById(R.id.menu_container) !is MenuFragment) {
                    if (supportFragmentManager.findFragmentById(R.id.menu_container) is FriendsPagerFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is ProfileFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is SettingsFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is NewPlayerFragment
                        || supportFragmentManager.findFragmentById(R.id.menu_container) is NewPlayerProfileFragment
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

    override fun onBackStackChanged() {

    }

    fun backToDashboardAndRefresh() {
        binding.bottomNavigation.selectedItemId = R.id.menu_dashboard
        lifecycleScope.launch {
            delay(500)
            mViewModel.fetchDashboardData()
        }
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
                    binding.bottomNavigation.selectedItemId = R.id.menu_dashboard
                }
                is NewChallengeFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.menu_new_challenge
                }
                else -> {
                    binding.bottomNavigation.selectedItemId = R.id.menu_timeline
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

    fun bottomNavTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.customText.text = "Use these buttons to move between different components of the App"
        target.closeTarget.updateLayoutParams<ConstraintLayout.LayoutParams> { updateMargins(bottom = binding.bottomNavigation.height + 20.dp) }
        target.closeTarget.setOnClickListener {
            val current = getVisibleFragment()
            if (current is DashboardFragment) {
                current.nextTarget()
            }
        }
        target.closeSpotlight.setOnClickListener {
            val current = getVisibleFragment()
            if (current is DashboardFragment) {
                current.closeSpotlight()
            }
        }

        return Target.Builder()
            .setAnchor(
                binding.bottomNavigation.x + binding.bottomNavigation.width / 2,
                (binding.bottomNavigation.y + binding.bottomNavigation.height / 2) - binding.bottomNavigation.paddingBottom / 2
            )
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.bottomNavigation.height.toFloat() - binding.bottomNavigation.paddingBottom,
                    binding.bottomNavigation.width.toFloat(),
                    4.dp.toFloat()
                )
            )
            .build()
    }

}