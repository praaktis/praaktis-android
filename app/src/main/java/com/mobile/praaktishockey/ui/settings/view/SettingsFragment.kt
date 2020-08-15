package com.mobile.praaktishockey.ui.settings.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentSettingsBinding
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.SplashScreenActivity
import com.mobile.praaktishockey.ui.settings.adapter.LanguageAdapter
import com.mobile.praaktishockey.ui.settings.vm.SettingsFragmentViewModel
import java.util.*

class SettingsFragment constructor(override val layoutId: Int = R.layout.fragment_settings) :
    BaseFragment<FragmentSettingsBinding>() {

    companion object {
        const val TAG = "SettingsFragment"

        @JvmStatic
        fun getInstance(): Fragment = SettingsFragment()
    }

    override val mViewModel: SettingsFragmentViewModel
        get() = getViewModel { SettingsFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        initLanguageSettings()
    }

    private fun initLanguageSettings() {
        binding.etLanguage.setDropDownBackgroundResource(R.color.blue_grey_50)

        val langs = listOf(
            Pair(R.drawable.flag_gb, getString(R.string.locale_en)),
            Pair(R.drawable.flag_fr, getString(R.string.locale_fr))
        )

        val adapter = LanguageAdapter(requireContext(), langs)

        binding.etLanguage.setAdapter(adapter)
        binding.etLanguage.setOnItemClickListener { parent, view, position, id ->
            adapter.invalidateOnSelected(position)
            val selectedLang = langs[position]
            binding.ivLangIcon.setImageResource(selectedLang.first)
            val localeKey = when (selectedLang.second) {
                getString(R.string.locale_en) -> "en"
                getString(R.string.locale_fr) -> "fr"
                else -> "en"
            }
            mViewModel.updateProfileLanguage(localeKey)
        }

        when (mViewModel.getLanguage()) {
            "en" -> {
                langs[0].apply {
                    binding.ivLangIcon.setImageResource(first)
                    binding.etLanguage.setText(second, false)
                    adapter.invalidateOnSelected(0)
                }
            }
            "fr" -> {
                langs[1].apply {
                    binding.ivLangIcon.setImageResource(first)
                    binding.etLanguage.setText(second, false)
                    adapter.invalidateOnSelected(1)
                }
            }
        }

        mViewModel.updateProfileLanguageEvent.observe(viewLifecycleOwner, Observer { localeKey ->
            mViewModel.saveLanguage(localeKey)
            setLocale()
        })

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