package com.mobile.gympraaktis.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.databinding.ActivityMainBinding
import com.mobile.gympraaktis.domain.extension.*
import timber.log.Timber

class MainActivity constructor(override val layoutId: Int = R.layout.activity_main) :
    BaseActivity<ActivityMainBinding>(), FragmentManager.OnBackStackChangedListener {

    companion object {
        @JvmField
        val TAG: String = MainActivity::class.java.simpleName

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

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }

        replaceFragment(StartupFragment.TAG) {
            replace(
                R.id.container,
                StartupFragment.newInstance(),
                StartupFragment.TAG
            )
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        val current = supportFragmentManager.findFragmentById(R.id.container)
        Timber.d("Current $current")
        when (current) {
            is ExerciseResultFragment -> {
                setLightStatusBar()
            }
            else -> {
                clearLightStatusBar()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }


}