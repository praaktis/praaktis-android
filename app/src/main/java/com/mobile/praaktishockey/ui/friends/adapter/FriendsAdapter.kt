package com.mobile.praaktishockey.ui.friends.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.entities.FriendDTO
import com.mobile.praaktishockey.domain.extension.onClick

class FriendsAdapter (private val list: List<FriendDTO>,
                      private val listener: FriendsItemListner): RecyclerView.Adapter<FriendsAdapter.FriendsRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
        return FriendsRequestViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FriendsRequestViewHolder, position: Int) {
        with(holder) {
            tvPosition.text = "${position + 1}"
            tvFriendName.text = list[position].friendFirstName + " " + list[position].friendLastName
            ivDelete.onClick {
                listener.onDeleteFriendItem(list[position])
            }
        }
    }

    inner class FriendsRequestViewHolder (val v: View): RecyclerView.ViewHolder(v) {
        val tvPosition: TextView = v.findViewById(R.id.tvPosition)
        val tvFriendName: TextView = v.findViewById(R.id.tvFriendName)
        val ivDelete: ImageView = v.findViewById(R.id.ivDelete)
    }
}