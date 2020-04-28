package com.mobile.praaktishockey.ui.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.data.entities.TimelineEntity
import com.mobile.praaktishockey.databinding.ItemTimelineBinding
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.entities.TimelineChallengeItem
import com.mobile.praaktishockey.domain.extension.removeDuplicateWhiteSpaces
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class TimelineAdapter(
    private val onItemClick: (TimelineEntity) -> Unit
) :
    ListAdapter<TimelineEntity, TimelineAdapter.ViewHolder>(DIFF_CALLBACK) {

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
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("##.##")

        fun bind(item: TimelineEntity) {
            binding.tvChallengeName.text = item.challengeName
            binding.tvPoints.text = item.points.toString()
            binding.tvScore.text = decimalFormat.format(item.score)

            val dateTime = LocalDateTime.parse(
                item.timePerformed?.removeDuplicateWhiteSpaces(),
                DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy", Locale.ENGLISH)
            )

            binding.tvDate.text =
                dateTime.format(DateTimeFormatter.ofPattern("E d MMM yyyy")).toLowerCase(Locale.getDefault())
            binding.tvTime.text = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            binding.tvDetail.setOnClickListener {
                onItemClick.invoke(item)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineEntity>() {
            override fun areItemsTheSame(oldItem: TimelineEntity, newItem: TimelineEntity): Boolean {
                return oldItem.attemptId == newItem.attemptId
            }

            override fun areContentsTheSame(oldItem: TimelineEntity, newItem: TimelineEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}