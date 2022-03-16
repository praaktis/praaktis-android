package com.mobile.gympraaktis.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.databinding.ItemProgressBinding
import com.mobile.gympraaktis.domain.extension.animateWeightChange
import com.mobile.gympraaktis.domain.extension.listen
import com.mobile.gympraaktis.domain.extension.updateLayoutParams
import timber.log.Timber
import java.io.Serializable

class AnalysisAdapter<T>(private val onItemClick: (AnalysisItem<T>) -> Unit) :
    ListAdapter<AnalysisItem<T>, AnalysisAdapter<T>.ViewHolder>(AnalysisItemItemCallback()) {
    inner class ViewHolder(private val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnalysisItem<T>) {
            binding.tvTitle.text = item.title
            binding.tvValue.text = item.value.toInt().toString()
            Timber.d(item.value.toInt().toString())
            if (item.animated != true) {
                binding.vProgress.animateWeightChange(0, item.value.toInt(), onValueChange = {
                    binding.tvValue.translationX = binding.vProgress.width.toFloat()
                })
                item.animated = true
            } else {
                binding.vProgress.updateLayoutParams<LinearLayout.LayoutParams> {
                    weight = item.value
                }
                binding.tvValue.translationX = binding.vProgress.width.toFloat()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProgressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).listen { position, _ ->
            onItemClick.invoke(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AnalysisItem<*>>() {
            override fun areItemsTheSame(
                oldItem: AnalysisItem<*>,
                newItem: AnalysisItem<*>
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: AnalysisItem<*>,
                newItem: AnalysisItem<*>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class AnalysisItemItemCallback<T> : DiffUtil.ItemCallback<AnalysisItem<T>>() {
    override fun areItemsTheSame(
        oldItem: AnalysisItem<T>,
        newItem: AnalysisItem<T>
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: AnalysisItem<T>,
        newItem: AnalysisItem<T>
    ): Boolean {
        return oldItem == newItem
    }
}

data class AnalysisItem<T>(
    val title: String,
    val value: Float,
    val maxValue: Float,
    var animated: Boolean? = null,
    val returnItem: T,
) : Serializable

//val analysisMockData = listOf(
//    AnalysisItem("Player 1", 66f, 100f),
//    AnalysisItem("Player 2", 74f, 100f),
//    AnalysisItem("Player 3", 90f, 100f),
//    AnalysisItem("Player 4", 2f, 100f),
//    AnalysisItem("Player 5", 43f, 100f),
//    AnalysisItem("Player 6", 30f, 100f),
//    AnalysisItem("Player 7", 60f, 100f),
//    AnalysisItem("Player 8", 80f, 100f),
//)