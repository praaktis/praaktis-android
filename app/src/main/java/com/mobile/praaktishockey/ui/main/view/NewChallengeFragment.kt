package com.mobile.praaktishockey.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.Constants
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.challenge.ChallengeVideoActivity
import com.mobile.praaktishockey.ui.main.adapter.ChallengesAdapter
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import com.mobile.praaktishockey.ui.main.vm.NewChallengeViewModel
import kotlinx.android.synthetic.main.fragment_new_challenge.*

class NewChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_new_challenge) :
    BaseFragment() {

    companion object {
        @JvmField
        val TAG: String = NewChallengeFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = NewChallengeFragment()
    }

    override val mViewModel: NewChallengeViewModel
        get() = getViewModel { NewChallengeViewModel(activity.application) }

    private lateinit var mainViewModel: MainViewModel

    override fun initUI(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
        mainViewModel.changeTitle(getString(R.string.new_challenge))

        val adapter = ChallengesAdapter {
            ChallengeVideoActivity.start(activity, it)
        }
        rv_challenges.adapter = adapter

        mainViewModel.challengesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

    }

}
