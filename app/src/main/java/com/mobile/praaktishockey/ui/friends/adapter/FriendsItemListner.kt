package com.mobile.praaktishockey.ui.friends.adapter

import com.mobile.praaktishockey.domain.entities.FriendDTO

interface FriendsItemListner {
    fun onDeleteFriendItem(friend: FriendDTO)
}
interface FriendsRequestItemListener {
    fun onResendClicked(friend: FriendDTO)
    fun onAcceptClicked(friend: FriendDTO)
    fun onRefuseClicked(friend: FriendDTO)
}