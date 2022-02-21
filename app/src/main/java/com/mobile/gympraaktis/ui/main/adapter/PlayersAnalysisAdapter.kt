package com.mobile.gympraaktis.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.data.entities.FriendEntity
import com.mobile.gympraaktis.domain.extension.listen
import kotlinx.android.synthetic.main.item_analysis.view.*

class PlayersAnalysisAdapter(private val onItemClick: (FriendEntity) -> Unit) :
    ListAdapter<FriendEntity, PlayersAnalysisAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent).listen { position, _ ->
            onItemClick.invoke(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val context = view.context

        fun bind(item: FriendEntity) {
            view.tv_text.text = item.fullName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_analysis, parent, false)
                )
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FriendEntity>() {
            override fun areItemsTheSame(
                oldItem: FriendEntity,
                newItem: FriendEntity
            ): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(
                oldItem: FriendEntity,
                newItem: FriendEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}