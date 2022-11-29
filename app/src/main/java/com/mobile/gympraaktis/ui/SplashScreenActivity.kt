package com.mobile.gympraaktis.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.databinding.ActivitySplashBinding
import com.mobile.gympraaktis.domain.extension.transparentStatusAndNavigationBar
import com.mobile.gympraaktis.ui.main.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity constructor(override val layoutId: Int = R.layout.activity_splash) :
    BaseActivity<ActivitySplashBinding>() {

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        lifecycleScope.launch {
            delay(splashScreenDuration)
            withContext(Dispatchers.Main) {
                MainActivity.startAndFinishAll(this@SplashScreenActivity)
            }
        }
    }

    private fun getSplashScreenDuration() = 1500L

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

}