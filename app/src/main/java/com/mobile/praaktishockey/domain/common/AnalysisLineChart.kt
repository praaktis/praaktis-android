package com.mobile.praaktishockey.domain.common

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.animateWeightChange
import com.mobile.praaktishockey.domain.extension.dpToPx

class AnalysisLineChart(context: Context, val value: Float, val title: String) : LinearLayout(context) {

    init {
        init()
    }

    private fun init() {
        orientation = VERTICAL

        val tvTitle = AppCompatTextView(context)
        tvTitle.text = title
        tvTitle.setTextSize(Dimension.SP, 17f)
        tvTitle.setTextColor(ContextCompat.getColor(context!!, R.color.white_transparent))
        tvTitle.setPadding(0, context.dpToPx(10), 0, 0)
        tvTitle.isAllCaps = true
        addView(tvTitle)

        val lineView = LinearLayout(context)
        lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.indigo_300))
        lineView.weightSum = 100F
        val lineLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.dpToPx(34))
        lineLayoutParams.setMargins(0, context.dpToPx(10), 0, 0)
        lineView.layoutParams = lineLayoutParams

        val tvValue = AppCompatTextView(context)
        tvValue.setTextSize(Dimension.SP, 24f)
        tvValue.setTextColor(Color.WHITE)
        tvValue.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_500))
        tvValue.setPadding(context.dpToPx(10), 0, 0, 0)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.CENTER_VERTICAL
        lp.weight = value
        tvValue.layoutParams = lp
        lineView.addView(tvValue)
        tvValue.animateWeightChange(0, value.toInt(), onValueChange = {
            tvValue.text = "${it.toInt()}"
        })

        addView(lineView)
    }
}