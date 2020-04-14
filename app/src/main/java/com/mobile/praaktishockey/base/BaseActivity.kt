package com.mobile.praaktishockey.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.common.ProgressLoadingDialog
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.extension.isTablet
import com.mobile.praaktishockey.domain.extension.makeToast

abstract class BaseActivity : AppCompatActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected open val mViewModel: BaseViewModel? = null
    val progressLoadingDialog by lazy { ProgressLoadingDialog(this) }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { SettingsStorage.setLocale(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestCorrectOrientation()
        super.onCreate(savedInstanceState)
        if (shouldOverridePendingTransition()) {
            enterPendingTransition()
        }
        setContentView(layoutId)
        initUI(savedInstanceState)

        mViewModel?.let {
            it.errorMessage.observe(this, Observer {
                if (it is String) makeToast(it)
                else if (it is Int) makeToast(it)
            })
            it.logoutEvent.observe(this, Observer {
//                makeToast("" + it)
            })

            it.showHideEvent.observe(this, Observer {
                if (it) showProgress()
                else hideProgress()
            })
        }
    }

    open fun showProgress() {
        progressLoadingDialog.show()
    }

    open fun hideProgress() {
        progressLoadingDialog.dismiss()
    }

    abstract fun initUI(savedInstanceState: Bundle?)

    open fun shouldOverridePendingTransition() = true
    open fun enterPendingTransition() = overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    open fun exitPendingTransition() = overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    protected fun requestCorrectOrientation() {
        requestedOrientation = if (isTablet()) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun finish() {
        super.finish()
        if (shouldOverridePendingTransition()) {
            exitPendingTransition()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel?.onDestroy()
    }
}