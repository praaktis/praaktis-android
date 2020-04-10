package com.mobile.praaktishockey.ui.friends.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentFriendsBinding
import com.mobile.praaktishockey.domain.entities.FriendDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.friends.adapter.FriendsRequestAdapter
import com.mobile.praaktishockey.ui.friends.adapter.FriendsRequestItemListener
import com.mobile.praaktishockey.ui.friends.vm.FriendsRequestFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsRequestFragment constructor(override val layoutId: Int = R.layout.fragment_friends) :
    BaseFragment<FragmentFriendsBinding>(), FriendsRequestItemListener {

    companion object {
        @JvmField
        val TAG = FriendsRequestFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = FriendsRequestFragment()
    }

    override val mViewModel: FriendsRequestFragmentViewModel
        get() = getViewModel { FriendsRequestFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.getFriendRequests()
        initEvents()

        binding.rvFriends.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)
                    ?.let { setDrawable(it) }
            }
        )
    }

    private fun initEvents() {
        mViewModel.friendRequestEvent.observe(this, Observer {
            rvFriends.adapter = FriendsRequestAdapter(it, this)
            if (it.isEmpty()) {
                tvEmpty.show()
                cvAddFriend.show()
                cvAddFriend.onClick {
                    InviteFriendsActivity.start(activity)
                }
            } else tvEmpty.hide()
            tvEmpty.text = getString(R.string.no_friend_requests)
        })
        mViewModel.refuseFriendEvent.observe(this, Observer {
            mViewModel.getFriendRequests()
        })
        mViewModel.confirmFriendEvent.observe(this, Observer {
            mViewModel.getFriendRequests()
            for (fragment in fragmentManager?.fragments!!) {
                if (fragment is FriendsFragment)
                    fragment.checkData()
            }
        })
        mViewModel.inviteFriendEvent.observe(this, Observer {
            mViewModel.getFriendRequests()
        })
    }

    override fun onAcceptClicked(friend: FriendDTO) {
        materialAlert {
            setMessage(
                String.format(
                    getString(R.string.are_you_sure_want_accept),
                    "${friend.friendFirstName} ${friend.friendLastName}?"
                )
            )
            setPositiveButton(R.string.ok) { dialog, which ->
                mViewModel.confirmFriend(friend.friendEmail)
            }
            setNegativeButton(R.string.cancel) { dialog, which -> }
        }?.show()
    }

    override fun onRefuseClicked(friend: FriendDTO) {
        materialAlert {
            setMessage(
                String.format(
                    getString(R.string.are_you_sure_want_refuse),
                    "${friend.friendFirstName} ${friend.friendLastName}?"
                )
            )
            setPositiveButton(R.string.ok) { dialog, which ->
                mViewModel.refuseFriend(friend.friendEmail)
            }
            setNegativeButton(R.string.cancel) { dialog, which -> }
        }?.show()
    }

    override fun onResendClicked(friend: FriendDTO) {
        materialAlert {
            setMessage(
                String.format(
                    getString(R.string.are_you_sure_want_resend),
                    "${
                    if (friend.friendFirstName.isEmpty()) friend.friendEmail
                    else friend.friendFirstName + " " + friend.friendLastName}?"
                )
            )
            setPositiveButton(R.string.ok) { dialog, which ->
                mViewModel.inviteFriend(friend.friendEmail)
            }
            setNegativeButton(R.string.cancel) { dialog, which -> }
        }?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InviteFriendsActivity.INVITE_FRIEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.getFriendRequests()
        }
    }
}