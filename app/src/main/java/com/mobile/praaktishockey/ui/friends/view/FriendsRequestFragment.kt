package com.mobile.praaktishockey.ui.friends.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.InfoDialog
import com.mobile.praaktishockey.domain.entities.FriendDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.friends.adapter.FriendsRequestAdapter
import com.mobile.praaktishockey.ui.friends.adapter.FriendsRequestItemListener
import com.mobile.praaktishockey.ui.friends.vm.FriendsFragmentViewModel
import com.mobile.praaktishockey.ui.friends.vm.FriendsRequestFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsRequestFragment constructor(override val layoutId: Int = R.layout.fragment_friends) :
    BaseFragment(),
    FriendsRequestItemListener {

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
        rvFriends.layoutManager = LinearLayoutManager(context!!)
        initEvents()
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
        val dialog =
            InfoDialog(context!!,
                String.format(
                    getString(R.string.are_you_sure_want_accept),
                    "${friend.friendFirstName} ${friend.friendLastName}?"
                ), object : InfoDialog.InfoDialogListener {
                    override fun onOkClicked() {
                        mViewModel.confirmFriend(friend.friendEmail)
                    }
                })
        dialog.show()
    }

    override fun onRefuseClicked(friend: FriendDTO) {
        val dialog =
            InfoDialog(context!!, String.format(
                getString(R.string.are_you_sure_want_refuse),
                "${friend.friendFirstName} ${friend.friendLastName}?"
            ), object : InfoDialog.InfoDialogListener {
                override fun onOkClicked() {
                    mViewModel.refuseFriend(friend.friendEmail)
                }
            })
        dialog.show()
    }

    override fun onResendClicked(friend: FriendDTO) {
        val dialog = InfoDialog(context!!,
            String.format(
                getString(R.string.are_you_sure_want_resend),
                "${
                if (friend.friendFirstName.isEmpty()) friend.friendEmail
                else friend.friendFirstName + " " + friend.friendLastName}?"
            ), object : InfoDialog.InfoDialogListener {
                override fun onOkClicked() {
                    mViewModel.inviteFriend(friend.friendEmail)
                }
            })
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InviteFriendsActivity.INVITE_FRIEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.getFriendRequests()
        }
    }
}