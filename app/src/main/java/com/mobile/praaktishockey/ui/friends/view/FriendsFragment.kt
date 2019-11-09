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
import com.mobile.praaktishockey.ui.friends.adapter.FriendsAdapter
import com.mobile.praaktishockey.ui.friends.adapter.FriendsItemListner
import com.mobile.praaktishockey.ui.friends.vm.FriendsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment constructor(override val layoutId: Int = R.layout.fragment_friends) : BaseFragment(),
    FriendsItemListner {

    companion object {
        val TAG = FriendsFragment::class.java.simpleName
        fun getInstance(): Fragment = FriendsFragment()
    }

    override val mViewModel: FriendsFragmentViewModel
        get() = getViewModel { FriendsFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        initViewModel()
        rvFriends.layoutManager = LinearLayoutManager(context!!)
    }

    private fun initViewModel() {
        mViewModel.getFriends()
        mViewModel.friendsEvent.observe(this, Observer {
            rvFriends.adapter = FriendsAdapter(it, this)
            if (it.isEmpty()) {
                tvEmpty.show()
                cvAddFriend.show()
                cvAddFriend.onClick {
                    InviteFriendsActivity.start(activity)
                }
            } else tvEmpty.hide()
        })
        mViewModel.deleteFriendEvent.observe(this, Observer {
            mViewModel.getFriends()
        })
    }

    override fun onDeleteFriendItem(friend: FriendDTO) {
        val dialog = InfoDialog(context!!, String.format(
            getString(R.string.are_you_sure_want_delete),
            "${friend.friendFirstName} ${friend.friendLastName}?"
        ),
            object : InfoDialog.InfoDialogListener {
                override fun onOkClicked() {
                    mViewModel.deleteFriend(friend.friendEmail)
                }
            })
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InviteFriendsActivity.INVITE_FRIEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.getFriends()
        }
    }

    fun checkData() {
        mViewModel.getFriends()
    }
}