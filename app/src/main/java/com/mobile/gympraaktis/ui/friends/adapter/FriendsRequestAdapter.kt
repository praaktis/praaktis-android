package com.mobile.gympraaktis.ui.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.databinding.ItemFriendsRequestBinding
import com.mobile.gympraaktis.domain.entities.FriendDTO
import com.mobile.gympraaktis.domain.extension.hide
import com.mobile.gympraaktis.domain.extension.loadAvatar
import com.mobile.gympraaktis.domain.extension.onClick
import com.mobile.gympraaktis.domain.extension.show

class FriendsRequestAdapter(
    private val list: List<FriendDTO>,
    private val listener: FriendsRequestItemListener
) : RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestViewHolder {
        return FriendsRequestViewHolder(
            ItemFriendsRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FriendsRequestViewHolder, position: Int) {
        with(holder) {
            if (list[position].friendFirstName.isNotEmpty())
                binding.tvName.text =
                    list[position].friendFirstName + " " + list[position].friendLastName
            else binding.tvName.text = list[position].friendEmail
            binding.ivAvatar.loadAvatar(list[position].friendImage)
            binding.btnResend.onClick { listener.onResendClicked(list[position]) }
            binding.btnAccept.onClick { listener.onAcceptClicked(list[position]) }
            binding.btnRefuse.onClick { listener.onRefuseClicked(list[position]) }
            if (list[position].requestedBy == "me") {
                binding.btnAccept.hide()
                binding.btnRefuse.hide()
                binding.tvStatus.show()
                binding.btnResend.show()
            } else {
                binding.tvStatus.hide()
                binding.btnResend.hide()
                binding.btnAccept.show()
                binding.btnRefuse.show()
            }
        }
    }

    inner class FriendsRequestViewHolder(val binding: ItemFriendsRequestBinding) :
        RecyclerView.ViewHolder(binding.root)
}