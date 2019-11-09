package com.mobile.praaktishockey.ui.friends.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.entities.FriendDTO
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.listen
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.domain.extension.show

class FriendsRequestAdapter(
    private val list: List<FriendDTO>,
    private val listener: FriendsRequestItemListener
) : RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_friends_request, parent, false)
        return FriendsRequestViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FriendsRequestViewHolder, position: Int) {
        with(holder) {
            if (list[position].friendFirstName != null && list[position].friendFirstName.isNotEmpty())
                tvFriendName.text = list[position].friendFirstName + " " + list[position].friendLastName
            else tvFriendName.text = list[position].friendEmail
            tvResend.onClick { listener.onResendClicked(list[position]) }
            cvAccept.onClick { listener.onAcceptClicked(list[position]) }
            tvRefuse.onClick { listener.onRefuseClicked(list[position]) }
            if (list[position].requestedBy.equals("me")) {
                cvAccept.hide()
                tvRefuse.hide()
                tvPendingStatus.show()
                tvResend.show()
            } else {
                cvAccept.show()
                tvRefuse.show()
                tvPendingStatus.hide()
                tvResend.hide()
            }
        }
    }

    inner class FriendsRequestViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val tvFriendName: TextView = v.findViewById(R.id.tvFriendName)
        val tvPendingStatus: TextView = v.findViewById(R.id.tvPendingStatus)
        val tvResend: TextView = v.findViewById(R.id.tvResend)
        val cvAccept: CardView = v.findViewById(R.id.cvAccept)
        val tvRefuse: TextView = v.findViewById(R.id.tvRefuse)
    }
}