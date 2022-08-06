package com.mobile.gympraaktis.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.data.entities.AnalysisComplete
import com.mobile.gympraaktis.databinding.ItemExpandableProgressBinding
import com.mobile.gympraaktis.databinding.ItemProgressBinding
import com.mobile.gympraaktis.domain.entities.DetailScoreDTO
import com.mobile.gympraaktis.domain.extension.animateWeightChange
import com.mobile.gympraaktis.domain.extension.listen
import com.mobile.gympraaktis.domain.extension.updateLayoutParams
import timber.log.Timber
import java.io.Serializable
import java.lang.Float.max

class AnalysisAdapter<T>(private val onItemClick: (AnalysisItem<T>) -> Unit) :
    ListAdapter<AnalysisItem<T>, AnalysisAdapter<T>.ViewHolder>(AnalysisItemItemCallback()) {
    inner class ViewHolder(private val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnalysisItem<T>) {
            binding.tvTitle.text = item.title
            binding.vMaxProgress.weightSum = item.maxValue
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

}

class AnalysisExpandableAdapter<T>(private val onItemClick: (AnalysisItem<T>) -> Unit) :
    ListAdapter<AnalysisItem<T>, AnalysisExpandableAdapter<T>.ViewHolder>(AnalysisItemItemCallback()) {
    inner class ViewHolder(private val binding: ItemExpandableProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnalysisItem<T>) {
            itemView.setOnClickListener {
                binding.expandable.toggle()
            }
            binding.tvTitle.text = item.title
            binding.vMaxProgress.weightSum = item.maxValue
            binding.tvValue.text = item.value.toInt().toString()
            binding.tvAdditionalText.text = item.additionalText.toString()
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
            ItemExpandableProgressBinding.inflate(
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
    val additionalText: String? = null,
) : Serializable

fun DetailScoreDTO.toAnalysisItem(): AnalysisItem<DetailScoreDTO> {
    var maxValue = detailPoint.maxValue

    if (maxValue == 0f) {
        maxValue = 100f
    }

    maxValue = max(maxValue, detailPointScore)

    return AnalysisItem(
        detailPoint.name,
        detailPointScore,
        maxValue,
        returnItem = this,
        additionalText = detailPoint.helpText,
    )
}

fun AnalysisComplete.toAnalysisItem(title: String): AnalysisItem<AnalysisComplete> {
    var maxValue = analysisEntity.maxScore.toFloat()

    if (maxValue == 0f) {
        maxValue = 100f
    }

    maxValue = max(maxValue, analysisEntity.averageScore.toFloat())

    return AnalysisItem(
        title,
        analysisEntity.averageScore.toFloat(),
        maxValue,
        returnItem = this,
    )
}

