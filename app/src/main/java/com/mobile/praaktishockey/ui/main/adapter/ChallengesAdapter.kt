package com.mobile.praaktishockey.ui.main.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.onClick
import com.praaktis.exerciseengine.Exercise
import kotlinx.android.synthetic.main.item_challenge.view.*
import java.io.Serializable

class ChallengesAdapter(private val itemClick: (ChallengeItem) -> Unit) : RecyclerView.Adapter<ChallengesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = challengesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(challengesList[position])
        holder.itemView.onClick {
            it.fl_overlay.animate().alpha(0.2f).setDuration(100).start()
            Handler().postDelayed({
                it.iv_image.animate().cancel()
                it.iv_image.animate().scaleX(1f).start()
                it.iv_image.animate().scaleY(1f).start()

                it.fl_overlay.animate().cancel()
                it.fl_overlay.animate().alpha(0.7f).setDuration(250).start()
                itemClick.invoke(challengesList[position])
                it.clearAnimation()
            }, 100)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ChallengeItem) {
            itemView.iv_image.setImageResource(item.image)
            itemView.tv_title.setText(item.name)

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
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_challenge, parent, false))
            }
        }
    }
}

data class ChallengeItem(@StringRes val name: Int,
                         @DrawableRes val image: Int,
                         val label: String,
                         val id: Int) : Serializable

val challengesList: List<ChallengeItem> = listOf(
        ChallengeItem(R.string.stretching_arms_up, R.drawable.stretching_arms_up_card, "Stretching Arms Up", Exercise.SQUATS.ordinal)
//        ChallengeItem(R.string.stretching_arms_up, R.drawable.challenge, "Stretching Arms Up", Exercise.SQUATS.ordinal)
//        ChallengeItem(R.string.low_backhand, R.drawable.img_low_backhand,  "Low backhand", 2),
//        ChallengeItem(R.string.trap, R.drawable.img_trap, "Trap",3)
)