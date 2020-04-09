package com.studio.praaktisinstructionsview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.google.android.material.card.MaterialCardView

class PraaktisInstructionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val instructions: MutableList<String> = mutableListOf()

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    fun setInstructions(list: List<String>) {
        instructions.clear()
        instructions.addAll(list)
        addInstructions()
    }

    private fun addInstructions() {
        instructions.forEachIndexed { index, text ->
            generateInstructionView((index + 1).toString(), text, index != instructions.size - 1)
        }
    }

    private fun generateInstructionView(
        circleText: String,
        labelText: String,
        addDivider: Boolean
    ) {
        val cardView = createMaterialCardView(cardViewSize.toInt())
        cardView.addView(createCardTextView(circleText))
        addView(cardView)
        addView(createInstructionTextView(labelText))
        if (addDivider)
            addView(createLineDivider())
    }

    private val cardViewSize: Float by lazy { resources.getDimension(R.dimen.cardview_size) }
    private val cardViewPadding: Float by lazy { resources.getDimension(R.dimen.cardview_padding) }
    private val cardViewStrokeWidth: Float by lazy { resources.getDimension(R.dimen.cardview_stroke_width) }

    private fun createMaterialCardView(size: Int): MaterialCardView {
        return MaterialCardView(context).apply {
            layoutParams = LayoutParams(size, size)
            radius = (size / 2).toFloat()
            cardViewPadding.toInt().let {
                setContentPadding(it, it, it, it)
            }
            strokeColor = ContextCompat.getColor(context, R.color.deep_purple_a400)
            strokeWidth = cardViewStrokeWidth.toInt()
        }
    }

    private fun createTextView(text: String): AppCompatTextView {
        return AppCompatTextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            setText(text)
        }
    }

    private fun createCardTextView(text: String): AppCompatTextView {
        return createTextView(text).apply {
            background = ContextCompat.getDrawable(context, R.drawable.gradient)
            setTextColor(Color.WHITE)
            setTextSize(Dimension.SP, 23f)
        }
    }

    private fun createInstructionTextView(text: String): AppCompatTextView {
        return createTextView(text).apply {
            setTextColor(ContextCompat.getColor(context, R.color.deep_purple_a400))
            setTextSize(Dimension.SP, 12f)
            isAllCaps = true
            updatePadding(
                top = resources.getDimension(R.dimen.instruction_text_top_padding).toInt()
            )
        }
    }

    private val lineWidth: Float by lazy { resources.getDimension(R.dimen.line_divider_width) }
    private val lineHeight: Float by lazy { resources.getDimension(R.dimen.line_divider_height) }
    private val lineMarginTop: Float by lazy { resources.getDimension(R.dimen.line_divider_top_margin) }

    private fun createLineDivider(): View {
        return View(context).apply {
            layoutParams = LayoutParams(lineWidth.toInt(), lineHeight.toInt()).apply {
                updateMargins(top = lineMarginTop.toInt(), bottom = lineMarginTop.toInt())
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.deep_purple_a400))
        }
    }

}