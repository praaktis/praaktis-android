package com.mobile.gympraaktis.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.common.ProgressLoadingDialog
import com.mobile.gympraaktis.domain.extension.isTablet
import com.mobile.gympraaktis.domain.extension.makeToast

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected lateinit var binding: B

    protected open val mViewModel: BaseViewModel? = null
    val progressLoadingDialog by lazy { ProgressLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestCorrectOrientation()
        super.onCreate(savedInstanceState)

        if (shouldOverridePendingTransition()) {
            enterPendingTransition()
        }
        binding = DataBindingUtil.setContentView(this, layoutId)
        initUI(savedInstanceState)

        mViewModel?.let {
            it.errorMessage.observe(this) {
                if (it is String) makeToast(it)
                else if (it is Int) makeToast(it)
            }
            it.logoutEvent.observe(this) {
                makeToast("" + it)
            }

            it.showHideEvent.observe(this) {
                if (it) showProgress()
                else hideProgress()
            }
        }
    }

    private var isConnected: Boolean = false

    fun isConnected() = isConnected

    abstract fun initUI(savedInstanceState: Bundle?)

    open fun shouldOverridePendingTransition() = true
    open fun enterPendingTransition() = overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    open fun exitPendingTransition() = overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    open fun showProgress() {
        progressLoadingDialog.show()
    }

    open fun hideProgress() {
        progressLoadingDialog.dismiss()
    }

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
}