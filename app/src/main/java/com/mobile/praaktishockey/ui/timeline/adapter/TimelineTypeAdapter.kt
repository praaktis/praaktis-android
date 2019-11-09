package com.mobile.praaktishockey.ui.timeline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.onClick

class TimelineTypeAdapter : RecyclerView.Adapter<TimelineTypeAdapter.TimelineViewHolder>() {

    private var selectedItem: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_type, parent, false)
        return TimelineViewHolder(v)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        if (position == selectedItem)
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemCard.context, R.color.white_transparent))
        else
            holder.itemCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemCard.context, R.color.grey_800))
        holder.itemCard.onClick {
            selectedItem = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = 4

    inner class TimelineViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemCard: CardView

        init {
            itemCard = view as CardView
        }
    }
}