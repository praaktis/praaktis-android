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
import com.mobile.praaktishockey.ui.friends.adapter.FriendsAdapter
import com.mobile.praaktishockey.ui.friends.adapter.FriendsItemListner
import com.mobile.praaktishockey.ui.friends.vm.FriendsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment constructor(override val layoutId: Int = R.layout.fragment_friends) :
    BaseFragment<FragmentFriendsBinding>(), FriendsItemListner {

    companion object {
        val TAG = FriendsFragment::class.java.simpleName
        fun getInstance(): Fragment = FriendsFragment()
    }

    override val mViewModel: FriendsFragmentViewModel
        get() = getViewModel { FriendsFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        initViewModel()

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
        materialAlert {
            setMessage(
                String.format(
                    getString(R.string.are_you_sure_want_delete),
                    "${friend.friendFirstName} ${friend.friendLastName}?"
                )
            )
            setPositiveButton(R.string.ok) { dialog, which ->
                mViewModel.deleteFriend(friend.friendEmail)
            }
            setNegativeButton(R.string.cancel) { dialog, which -> }
        }?.show()
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