package com.mobile.praaktishockey.domain.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.animateWeightChange
import com.mobile.praaktishockey.domain.extension.dp
import com.mobile.praaktishockey.domain.extension.dpToPx
import com.mobile.praaktishockey.domain.extension.updatePadding

class AnalysisLineChart(
    context: Context,
    val value: Float,
    val title: String,
    val progressBackground: Int
) :
    LinearLayout(context) {

    init {
        init()
    }

    private fun init() {
        orientation = VERTICAL

        val tvTitle = AppCompatTextView(context)
        tvTitle.text = title
        tvTitle.setTextSize(Dimension.SP, 18f)
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.purple_900_1))
        tvTitle.setPadding(0, 20.dp, 0, 0)
        tvTitle.isAllCaps = true
        addView(tvTitle)

        val lineView = LinearLayout(context)
        lineView.setBackgroundResource(R.drawable.shape_progress_bg)
        lineView.weightSum = 100F
        val lineLayoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15.dp)
        lineLayoutParams.setMargins(0, context.dpToPx(10), 0, 0)
        lineView.layoutParams = lineLayoutParams

        val progressView = View(context)
        progressView.setBackgroundResource(progressBackground)
        val progressLp = LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        progressLp.weight = value
        progressView.layoutParams = progressLp
        lineView.addView(progressView)

        val tvProgressValue = AppCompatTextView(context).apply {
            text = "0"
            setTextSize(Dimension.SP, 18f)
            setTextColor(ContextCompat.getColor(context, R.color.purple_900_1))
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            updatePadding(top = 5.dp)
        }

        progressView.animateWeightChange(0, value.toInt(), onValueChange = {
            tvProgressValue.text = "${it.toInt()}"
            tvProgressValue.translationX = progressView.width.toFloat()
        })

        addView(lineView)
        addView(tvProgressValue)

        /*val tvValue = AppCompatTextView(context)
        tvValue.setTextSize(Dimension.SP, 24f)
        tvValue.setTextColor(Color.WHITE)
        tvValue.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_500))
        tvValue.setPadding(context.dpToPx(10), 0, 0, 0)
        val lp = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lp.gravity = Gravity.CENTER_VERTICAL
        lp.weight = value
        tvValue.layoutParams = lp
        lineView.addView(tvValue)
        tvValue.animateWeightChange(0, value.toInt(), onValueChange = {
            tvValue.text = "${it.toInt()}"
        })*/

    }
}

val GRADIENT_PROGRESS_ARRAY = listOf<Int>(
    R.drawable.gradient_progress_2,
    R.drawable.gradient_progress,
    R.drawable.gradient_progress_3
)


