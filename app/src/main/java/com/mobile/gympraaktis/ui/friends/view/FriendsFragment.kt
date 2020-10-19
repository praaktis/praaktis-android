package com.mobile.gympraaktis.ui.friends.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentFriendsBinding
import com.mobile.gympraaktis.domain.entities.FriendDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.friends.adapter.FriendsAdapter
import com.mobile.gympraaktis.ui.friends.adapter.FriendsItemListner
import com.mobile.gympraaktis.ui.friends.vm.FriendsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment constructor(override val layoutId: Int = R.layout.fragment_friends) :
    BaseFragment<FragmentFriendsBinding>(), FriendsItemListner {

    companion object {
        const val TAG = "FriendsFragment"
        fun getInstance(): Fragment = FriendsFragment()
    }

    override val mViewModel: FriendsFragmentViewModel by viewModels()

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