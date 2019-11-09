package com.mobile.praaktishockey.ui.settings.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.addFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.SplashScreenActivity
import com.mobile.praaktishockey.ui.challenge.ChallengeActivity
import com.mobile.praaktishockey.ui.login.view.CalibrateFragment
import com.mobile.praaktishockey.ui.settings.vm.SettingsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment constructor(override val layoutId: Int = R.layout.fragment_settings) : BaseFragment() {

    companion object {
        @JvmField
        val TAG = SettingsFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = SettingsFragment()
    }

    override val mViewModel: SettingsFragmentViewModel
        get() = getViewModel { SettingsFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        when (mViewModel.getLanguage()) {
            "en" -> tvLanguage.text = "English"
            "fr" -> tvLanguage.text = "French"
            else -> tvLanguage.text = "Spain"
        }
        llLanguage.onClick {
            openLanguageDialog()
        }
        cvRecalibrate.onClick {
            ChallengeActivity.start(activity)
        }
        mViewModel.updateProfileLanguageEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer { localeKey ->
            mViewModel.saveLanguage(localeKey)
            setLocale()
        })
    }

    private fun openLanguageDialog() {
        val dialog = Dialog(context!!, R.style.AppAlertDialogTheme)
        dialog.setContentView(R.layout.dialog_language)
        dialog.setCanceledOnTouchOutside(false)
        val rgLanguage = dialog.findViewById<RadioGroup>(R.id.rgLanguage)
        val tvCancel = dialog.findViewById<View>(R.id.tvCancel)
        val tvOk = dialog.findViewById<View>(R.id.tvOk)

        when (mViewModel.getLanguage()) {
            "en" -> rgLanguage.check(R.id.rbEnglish)
            "fr" -> rgLanguage.check(R.id.rbFrance)
            else -> rgLanguage.check(R.id.rbSpain)
        }

        tvOk.onClick {
            val localeKey = when (rgLanguage.checkedRadioButtonId) {
                R.id.rbEnglish -> {
                    tvLanguage.text = "English"
                    "en"
                }
                R.id.rbFrance -> {
                    tvLanguage.text = "French"
                    "fr"
                }
                else -> {
                    tvLanguage.text = "English"
                    "en"
                }
            }
            dialog.dismiss()
            mViewModel.updateProfileLanguage(localeKey)
        }
        tvCancel.onClick { dialog.dismiss() }
        dialog.show()
    }

    private fun setLocale() {
        activity.finish()
        val intent = Intent(activity, SplashScreenActivity::class.java)
        val locale = Locale(mViewModel.getLanguage())
        Locale.setDefault(locale)
        val config = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.startActivity(intent)
    }
}