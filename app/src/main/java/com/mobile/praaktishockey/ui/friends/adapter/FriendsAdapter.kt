package com.mobile.praaktishockey.ui.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.databinding.ItemFriendsBinding
import com.mobile.praaktishockey.domain.entities.FriendDTO
import com.mobile.praaktishockey.domain.extension.loadAvatar
import com.mobile.praaktishockey.domain.extension.onClick

class FriendsAdapter(
    private val list: List<FriendDTO>,
    private val listener: FriendsItemListner
) : RecyclerView.Adapter<FriendsAdapter.FriendsRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestViewHolder {
        return FriendsRequestViewHolder(
            ItemFriendsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FriendsRequestViewHolder, position: Int) {
        with(holder) {
            binding.tvName.text =
                list[position].friendFirstName + " " + list[position].friendLastName
            binding.ivAvatar.loadAvatar(list[position].friendImage)
            binding.btnDelete.onClick {
                listener.onDeleteFriendItem(list[position])
            }
        }
    }

    inner class FriendsRequestViewHolder(val binding: ItemFriendsBinding) :
        RecyclerView.ViewHolder(binding.root)
}