package com.mobile.gympraaktis.ui.main.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.databinding.ItemProgressBinding
import java.io.Serializable

class AnalysisExpandableAdapter<T>(private val onItemClick: (AnalysisItem<T>) -> Unit) :
    ListAdapter<AnalysisItem<T>, AnalysisExpandableAdapter<T>.ViewHolder>(AnalysisItemItemCallback()) {
    inner class ViewHolder(private val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnalysisItem<T>) {
            itemView.setOnClickListener {
                binding.expandable.toggle()
            }
            binding.tvTitle.text = item.title
            binding.tvValue.text = item.value.toInt().toString()
            binding.tvAdditionalText.text = item.additionalText.toString()

            var progressValue = item.value
            var weightSum = item.maxValue
            if (item.value == 0f) {
                progressValue = 5f
                weightSum = 100f
            }

            binding.vMaxProgress.weightSum = weightSum

            if (item.animated != true) {
                binding.vProgress.animateWeightChange(0, progressValue.toInt(), onValueChange = {

                })
                item.animated = true
            } else {
                binding.vProgress.updateLayoutParams<LinearLayout.LayoutParams> {
                    weight = progressValue
                }

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

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

fun View.animateWeightChange(
    from: Int,
    to: Int,
    duration: Long = 600,
    startDelay: Long = 100,
    init: (ValueAnimator.() -> Unit)? = null,
    onValueChange: (Float) -> Unit
) {
    val valueAnimator =
        ValueAnimator.ofFloat(from.toFloat(), to.toFloat())
    valueAnimator.duration = duration
    valueAnimator.startDelay = startDelay
    valueAnimator.interpolator = FastOutSlowInInterpolator()
    init?.let { valueAnimator.it() }
    valueAnimator.addUpdateListener {
        this.updateLayoutParams<LinearLayout.LayoutParams> {
            weight = it.animatedValue as Float
        }
        onValueChange.invoke(it.animatedValue as Float)
        this.requestLayout()
    }
    valueAnimator.start()
}
