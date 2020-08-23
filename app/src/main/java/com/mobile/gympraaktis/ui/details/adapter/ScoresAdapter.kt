package com.mobile.gympraaktis.ui.details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.entities.Leader
import com.mobile.gympraaktis.domain.extension.loadAvatar

class ScoresAdapter(val list: List<Leader>) :
    RecyclerView.Adapter<ScoresAdapter.ScoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoresViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_score, parent, false)
        return ScoresViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScoresViewHolder, position: Int) {
        with(holder) {
            tvPosition.text = "${position + 1}"
            tvName.text = list[position].firstName + list[position].lastName
            tvScore.text = "${list[position].maxScore}"
            ivAvatar.loadAvatar(list[position].imageUrl)

            // Zebra-striping color effect
            holder.itemView.setBackgroundResource(
                if (position % 2 == 0) R.color.white
                else R.color.blue_grey_50
            )
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ScoresViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val tvPosition: TextView = v.findViewById(R.id.tvPosition)
        val tvScore: TextView = v.findViewById(R.id.tvScore)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val ivAvatar: ShapeableImageView = v.findViewById(R.id.iv_avatar)
    }
}