package com.mobile.gympraaktis.ui.friends.adapter

import com.mobile.gympraaktis.domain.entities.FriendDTO

interface FriendsItemListner {
    fun onDeleteFriendItem(friend: FriendDTO)
}
interface FriendsRequestItemListener {
    fun onResendClicked(friend: FriendDTO)
    fun onAcceptClicked(friend: FriendDTO)
    fun onRefuseClicked(friend: FriendDTO)
}