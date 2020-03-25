package com.mobile.praaktishockey.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.entities.AnalysisDTO
import com.mobile.praaktishockey.domain.extension.listen
import kotlinx.android.synthetic.main.item_analysis.view.*
import java.io.Serializable

class AnalysisAdapter(private val onItemClick: (AnalysisDTO) -> Unit) :
    ListAdapter<AnalysisDTO, AnalysisAdapter.ViewHolder>(DIFF_CALLBACK) {

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

        fun bind(item: AnalysisDTO) {
            view.tv_text.text = item.name
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AnalysisDTO>() {
            override fun areItemsTheSame(oldItem: AnalysisDTO, newItem: AnalysisDTO): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: AnalysisDTO, newItem: AnalysisDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}


data class AnalysisItem(
    @StringRes val title: Int,
    val name: String
) : Serializable

val analysisList: List<AnalysisItem> = listOf(
    AnalysisItem(R.string.stretching_arms_up, "Stretching Arms Up")
//    AnalysisItem(R.string.low_backhand,  "Low backhand"),
//    AnalysisItem(R.string.trap, "Trap")
)