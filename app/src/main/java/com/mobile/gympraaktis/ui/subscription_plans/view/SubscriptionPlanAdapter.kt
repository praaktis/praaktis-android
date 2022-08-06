package com.mobile.gympraaktis.ui.subscription_plans.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.mobile.gympraaktis.data.billing.BillingClientWrapper
import com.mobile.gympraaktis.databinding.ItemPlanBinding
import com.mobile.gympraaktis.domain.extension.listen

class SubscriptionPlanAdapter(private val onItemClick: (SubscriptionPlan) -> Unit) :
    ListAdapter<SubscriptionPlan, SubscriptionPlanAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubscriptionPlan) {
            binding.tvTitle.text = item.name + if (item.isActive) " (active)" else ""
            binding.tvPrice.text = item.price
            binding.tvDetails.text = item.benefits.joinToString("\n")
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
            onItemClick.invoke(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: String,
    val benefits: List<String>,
    val skuDetails: ProductDetails?,
    val isActive: Boolean,
    val praaktisKey: Int = BillingClientWrapper.trialPlan.praaktisKey,
)