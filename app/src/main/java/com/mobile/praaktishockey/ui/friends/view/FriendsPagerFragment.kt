package com.mobile.praaktishockey.ui.friends.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentFriendsPagerBinding
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.friends.vm.FriendsPagerFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends_pager.*

class FriendsPagerFragment constructor(override val layoutId: Int = R.layout.fragment_friends_pager) :
    BaseFragment<FragmentFriendsPagerBinding>() {

    companion object {
        const val TAG = "FriendsPagerFragment"
        fun getInstance(): Fragment = FriendsPagerFragment()
    }

    override val mViewModel: BaseViewModel
        get() = getViewModel { FriendsPagerFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        tlFriends.setupWithViewPager(vpFriends)
        vpFriends.adapter = FriendsPagerAdapter(childFragmentManager)

        ivAddFriends.onClick {
            InviteFriendsActivity.start(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private inner class FriendsPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            if (position == 0)
                return FriendsFragment.getInstance()
            return FriendsRequestFragment.getInstance()
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0) return getString(R.string.my_friends).toUpperCase()
            return getString(R.string.requests).toUpperCase()
        }

    }
}