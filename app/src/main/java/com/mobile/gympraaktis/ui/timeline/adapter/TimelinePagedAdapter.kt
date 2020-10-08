package com.mobile.gympraaktis.ui.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.data.entities.AttemptEntity
import com.mobile.gympraaktis.databinding.ItemTimelineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat
import java.util.*

class TimelinePagedAdapter(
    private val onItemClick: (AttemptEntity) -> Unit
) :
    PagedListAdapter<AttemptEntity, TimelinePagedAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("##.##")

        fun bind(item: AttemptEntity) {
            binding.tvChallengeName.text = item.challengeName
            binding.tvPoints.text = item.points.toString()
            binding.tvScore.text = decimalFormat.format(item.score)

            val dateTime = LocalDateTime.parse(
                item.timePerformed,
                DateTimeFormatter.ISO_DATE_TIME
            )

            binding.tvDate.text = dateTime.format(DateTimeFormatter.ofPattern("E d MMM yyyy"))
                .toLowerCase(Locale.getDefault())
            binding.tvTime.text = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            binding.tvDetail.setOnClickListener {
                onItemClick.invoke(item)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AttemptEntity>() {
            override fun areItemsTheSame(
                oldItem: AttemptEntity,
                newItem: AttemptEntity
            ): Boolean {
                return oldItem.attemptId == newItem.attemptId
            }

            override fun areContentsTheSame(
                oldItem: AttemptEntity,
                newItem: AttemptEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}