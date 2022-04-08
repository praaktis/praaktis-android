package com.mobile.gympraaktis.ui.subscription_plans.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.databinding.ItemPlanBinding
import com.mobile.gympraaktis.domain.extension.listen

class SubscriptionPlanAdapter :
    ListAdapter<SubscriptionPlan, SubscriptionPlanAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubscriptionPlan) {
            binding.tvTitle.text = item.name
            binding.tvPrice.text =
                "£${android.icu.text.NumberFormat.getInstance().format(item.price)}"
            binding.tvDetails.text =
                "${item.players} Player/Patient\n${if (item.attempts == -1) "Unlimited" else item.attempts} Attempts"
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SubscriptionPlan>() {
            override fun areItemsTheSame(
                oldItem: SubscriptionPlan,
                newItem: SubscriptionPlan
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SubscriptionPlan,
                newItem: SubscriptionPlan
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).listen { position, type ->

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

val mockPracticePlans = listOf(
    SubscriptionPlan(1, "Practice Basic", 1.99f, -1, 1),
    SubscriptionPlan(2, "Practice Premium", 4.99f, -1, 5),
)

val mockClubPlans = listOf(
    SubscriptionPlan(3, "Club Basic", 2.99f, 100, 15),
    SubscriptionPlan(4, "Club Premium", 6.99f, 300, 50)
)

data class SubscriptionPlan(
    val id: Long,
    val name: String,
    val price: Float,
    val attempts: Int,
    val players: Int,
)