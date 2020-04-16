package com.mobile.praaktishockey.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.data.entities.AnalysisComplete
import com.mobile.praaktishockey.domain.extension.listen
import kotlinx.android.synthetic.main.item_analysis.view.*

class AnalysisAdapter(private val onItemClick: (AnalysisComplete) -> Unit) :
    ListAdapter<AnalysisComplete, AnalysisAdapter.ViewHolder>(DIFF_CALLBACK) {

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

        fun bind(item: AnalysisComplete) {
            view.tv_text.text = item.analysisEntity.name
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AnalysisComplete>() {
            override fun areItemsTheSame(
                oldItem: AnalysisComplete,
                newItem: AnalysisComplete
            ): Boolean {
                return oldItem.analysisEntity.name == newItem.analysisEntity.name
            }

            override fun areContentsTheSame(
                oldItem: AnalysisComplete,
                newItem: AnalysisComplete
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}