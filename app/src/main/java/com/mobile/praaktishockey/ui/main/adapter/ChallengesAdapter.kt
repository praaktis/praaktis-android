package com.mobile.praaktishockey.ui.main.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.onClick
import kotlinx.android.synthetic.main.item_challenge.view.*
import java.io.Serializable

class ChallengesAdapter(private val itemClick: (ChallengeDTO) -> Unit) :
    ListAdapter<ChallengeDTO, ChallengesAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.onClick {
            it.fl_overlay.animate().alpha(0.2f).setDuration(100).start()
            Handler().postDelayed({
                it.iv_image.animate().cancel()
                it.iv_image.animate().scaleX(1f).start()
                it.iv_image.animate().scaleY(1f).start()

                it.fl_overlay.animate().cancel()
                it.fl_overlay.animate().alpha(0.7f).setDuration(250).start()
                itemClick.invoke(getItem(position))
                it.clearAnimation()
            }, 100)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ChallengeDTO) {
//            itemView.iv_image.setImageResource(item.image)
            itemView.tv_title.text = item.name

            itemView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        itemView.fl_overlay.animate().alpha(0.2f).setDuration(150).start()
                        itemView.iv_image.animate().scaleX(1.2f).setDuration(150).start()
                        itemView.iv_image.animate().scaleY(1.2f).setDuration(150).start()
                    }
                    MotionEvent.ACTION_UP -> {
                        itemView.iv_image.animate().cancel()
                        itemView.iv_image.animate().scaleX(1f).start()
                        itemView.iv_image.animate().scaleY(1f).start()

                        itemView.fl_overlay.animate().cancel()
                        itemView.fl_overlay.animate().alpha(0.7f).setDuration(250).start()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        itemView.iv_image.animate().cancel()
                        itemView.iv_image.animate().scaleX(1f).start()
                        itemView.iv_image.animate().scaleY(1f).start()

                        itemView.fl_overlay.animate().cancel()
                        itemView.fl_overlay.animate().alpha(0.7f).setDuration(250).start()
                    }
                }
                false
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_challenge, parent, false)
                )
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChallengeDTO>() {
            override fun areItemsTheSame(oldItem: ChallengeDTO, newItem: ChallengeDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChallengeDTO, newItem: ChallengeDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}

/*
data class ChallengeItem(
    @StringRes val name: Int,
    @DrawableRes val image: Int,
    val label: String,
    val id: Int
) : Serializable
*/

/*
val challengesList: List<ChallengeItem> = listOf(
    ChallengeItem(
        R.string.stretching_arms_up,
        R.drawable.stretching_arms_up_card,
        "Stretching Arms Up",
        4
    ),
    ChallengeItem(R.string.squats, R.drawable.stretching_arms_up_card, "Squats", 5),
    ChallengeItem(R.string.curl, R.drawable.curl_card, "Stretching Arms Up", 6)
//        ChallengeItem(R.string.stretching_arms_up, R.drawable.challenge, "Stretching Arms Up", Exercise.SQUATS.ordinal)
//        ChallengeItem(R.string.low_backhand, R.drawable.img_low_backhand,  "Low backhand", 2),
//        ChallengeItem(R.string.trap, R.drawable.img_trap, "Trap",3)
)*/
