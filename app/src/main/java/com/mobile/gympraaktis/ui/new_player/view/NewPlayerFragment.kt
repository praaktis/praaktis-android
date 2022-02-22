package com.mobile.gympraaktis.ui.new_player.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentNewPlayerBinding
import com.mobile.gympraaktis.domain.extension.addFragment
import com.mobile.gympraaktis.ui.new_player.vm.NewPlayerViewModel
import com.mobile.gympraaktis.ui.settings.view.SettingsFragment

class NewPlayerFragment(override val layoutId: Int = R.layout.fragment_new_player) :
    BaseFragment<FragmentNewPlayerBinding>() {

    companion object {
        const val TAG: String = "NewPlayerFragment";

        @JvmStatic
        fun newInstance() =
            NewPlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override val mViewModel: NewPlayerViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            activity.onBackPressed()
        }

        binding.btnActivateNewPlayer.setOnClickListener {
            activity.addFragment {
                add(R.id.menu_container, NewPlayerProfileFragment.newInstance(), NewPlayerProfileFragment.TAG)
                addToBackStack(SettingsFragment.TAG)
            }
        }
    }

}