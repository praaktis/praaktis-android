package com.mobile.praaktishockey.ui.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.databinding.ItemTimelineBinding
import com.mobile.praaktishockey.domain.entities.ScoreDTO
import com.mobile.praaktishockey.domain.entities.TimelineChallengeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class TimelineAdapter(
    private val onItemClick: (ScoreDTO) -> Unit,
    private val isEmptySet: (Boolean) -> Unit
) :
    ListAdapter<ScoreDTO, TimelineAdapter.ViewHolder>(DIFF_CALLBACK) {

    val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun submitList(timelines: ArrayList<TimelineChallengeItem>) {
        adapterScope.launch {
            val result: MutableList<ScoreDTO> = mutableListOf()
            timelines.forEach { challenge -> // set challenge name to each item score
                if (challenge.latest.timePerformed != null) {
                    challenge.latest.name = challenge.name
                    result.add(challenge.latest)
                }
                val scoresWithName = challenge.scores.onEach {
                    it.name = challenge.name
                }
                result.addAll(scoresWithName)
            }
            result.sortByDescending { // sort scores by latest attempt
                it.attemptId
            }
            withContext(Dispatchers.Main) {
                isEmptySet.invoke(result.isEmpty())
                super.submitList(result)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("##.##")

        fun bind(item: ScoreDTO) {
            binding.tvChallengeName.text = item.name
            binding.tvPoints.text = item.points.toString()
            binding.tvScore.text = decimalFormat.format(item.score)
            binding.tvTimePerformed.text = item.timePerformed

            binding.tvDetail.setOnClickListener {
                onItemClick.invoke(item)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScoreDTO>() {
            override fun areItemsTheSame(oldItem: ScoreDTO, newItem: ScoreDTO): Boolean {
                return oldItem.attemptId == newItem.attemptId
            }

            override fun areContentsTheSame(oldItem: ScoreDTO, newItem: ScoreDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}