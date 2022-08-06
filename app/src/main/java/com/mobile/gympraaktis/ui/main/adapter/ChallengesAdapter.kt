package com.mobile.gympraaktis.ui.main.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.data.entities.RoutineEntity
import com.mobile.gympraaktis.databinding.ItemChallengeBinding
import com.mobile.gympraaktis.domain.extension.loadUrl
import com.mobile.gympraaktis.domain.extension.onClick

class ChallengesAdapter(private val itemClick: (RoutineEntity) -> Unit) :
    ListAdapter<RoutineEntity, ChallengesAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.vGradient.onClick {
            Handler().postDelayed({
                holder.binding.vGradient.animate().cancel()
                holder.binding.vGradient.animate().scaleX(1f).start()
                holder.binding.vGradient.animate().scaleY(1f).start()

                holder.binding.root.animate().cancel()
                holder.binding.root.animate().scaleX(1f).start()
                holder.binding.root.animate().scaleY(1f).start()

                itemClick.invoke(getItem(position))
                it.clearAnimation()
            }, 100)
        }
    }

    class ViewHolder(val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RoutineEntity) {
            binding.ivImage.loadUrl(item.imageUrl)
            binding.tvTitle.text = item.name

            binding.vGradient.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.vGradient.animate().scaleX(1.2f).setDuration(150).start()
                        binding.vGradient.animate().scaleY(1.2f).setDuration(150).start()

                        binding.root.animate().scaleX(0.95f).setDuration(150).start()
                        binding.root.animate().scaleY(0.95f).setDuration(150).start()
                    }
                    MotionEvent.ACTION_UP -> {
                        binding.vGradient.animate().cancel()
                        binding.root.animate().cancel()

                        binding.vGradient.animate().scaleX(1f).start()
                        binding.vGradient.animate().scaleY(1f).start()

                        binding.root.animate().scaleX(1f).start()
                        binding.root.animate().scaleY(1f).start()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        binding.vGradient.animate().cancel()
                        binding.root.animate().cancel()

                        binding.vGradient.animate().scaleX(1f).start()
                        binding.vGradient.animate().scaleY(1f).start()

                        binding.root.animate().scaleX(1f).start()
                        binding.root.animate().scaleY(1f).start()
                    }
                }
                false
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemChallengeBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RoutineEntity>() {
            override fun areItemsTheSame(oldItem: RoutineEntity, newItem: RoutineEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RoutineEntity, newItem: RoutineEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

