package com.mobile.gympraaktis.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.data.entities.RoutineAnalysis
import com.mobile.gympraaktis.domain.extension.listen
import kotlinx.android.synthetic.main.item_analysis.view.*

class ExerciseAnalysisAdapter(private val onItemClick: (RoutineAnalysis) -> Unit) :
    ListAdapter<RoutineAnalysis, ExerciseAnalysisAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent).listen { position, _ ->
            onItemClick.invoke(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: RoutineAnalysis) {
            view.tv_text.text = item.routineEntity.name
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RoutineAnalysis>() {
            override fun areItemsTheSame(
                oldItem: RoutineAnalysis,
                newItem: RoutineAnalysis
            ): Boolean {
                return oldItem.routineEntity.id == newItem.routineEntity.id
            }

            override fun areContentsTheSame(
                oldItem: RoutineAnalysis,
                newItem: RoutineAnalysis
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}