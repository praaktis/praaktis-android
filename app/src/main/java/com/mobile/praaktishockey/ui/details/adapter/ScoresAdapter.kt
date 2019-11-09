package com.mobile.praaktishockey.ui.details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.entities.Leader
import com.mobile.praaktishockey.domain.extension.show

class ScoresAdapter(val list: List<Leader>) : RecyclerView.Adapter<ScoresAdapter.ScoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoresViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_score, parent, false)
        return ScoresViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScoresViewHolder, position: Int) {
        with(holder) {
            tvPosition.text = "${position + 1}"
            tvName.text = list[position].firstName + list[position].lastName
            tvScore.text = "${list[position].maxScore}"
            vCircle.show()
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ScoresViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val tvPosition: TextView = v.findViewById(R.id.tvPosition)
        val tvScore: TextView = v.findViewById(R.id.tvScore)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val vCircle: View = v.findViewById(R.id.vCircle)
    }
}