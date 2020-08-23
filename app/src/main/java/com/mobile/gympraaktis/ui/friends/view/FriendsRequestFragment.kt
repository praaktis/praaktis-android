package com.mobile.gympraaktis.ui.friends.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.temp.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentFriendsBinding
import com.mobile.gympraaktis.domain.entities.FriendDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.friends.adapter.FriendsRequestAdapter
import com.mobile.gympraaktis.ui.friends.adapter.FriendsRequestItemListener
import com.mobile.gympraaktis.ui.friends.vm.FriendsRequestFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsRequestFragment constructor(override val layoutId: Int = R.layout.fragment_friends) :
    BaseFragment<FragmentFriendsBinding>(), FriendsRequestItemListener {

    companion object {
        const val TAG = "FriendsRequestFragment"

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