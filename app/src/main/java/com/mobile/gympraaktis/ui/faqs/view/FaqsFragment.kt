package com.mobile.gympraaktis.ui.faqs.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentFaqsBinding
import com.mobile.gympraaktis.ui.faqs.vm.FaqsFragmentViewModel
import com.mobile.gympraaktis.ui.settings.vm.SettingsFragmentViewModel

class FaqsFragment constructor(override val layoutId: Int = R.layout.fragment_faqs) :
    BaseFragment<FragmentFaqsBinding>(){

    override val mViewModel: FaqsFragmentViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {

    }
}