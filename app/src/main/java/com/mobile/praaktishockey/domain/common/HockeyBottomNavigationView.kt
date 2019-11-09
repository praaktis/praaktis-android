package com.mobile.praaktishockey.domain.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.extension.dpToPx
import com.mobile.praaktishockey.domain.extension.onClick

class HockeyBottomNavigationView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initUI()
    }

    constructor(context: Context, attributeSet: AttributeSet, defaultStyle: Int) : super(
        context,
        attributeSet,
        defaultStyle
    ) {
        initUI()
    }

    private var listener: HockeyBottomNavigationListener? = null
    private var selectedItemPosition: Int = 0
    private val items: List<Pair<String, Int>> = listOf(
        Pair(resources.getString(R.string.dashboard), R.drawable.vector_dashboard),
        Pair(resources.getString(R.string.new_challenge), R.drawable.ic_add),
        Pair(resources.getString(R.string.timeline), R.drawable.ic_timeline),
        Pair(resources.getString(R.string.more), R.drawable.ic_menu)
    )

    private fun initUI() {
        removeAllViews()
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val padding = context.dpToPx(5)
        for(i in 0 until items.size) {
            val item = items[i]
            val container = LinearLayout(context)
            container.orientation = VERTICAL
            container.gravity = Gravity.CENTER_HORIZONTAL

            val ivMenu = ImageView(context)
            ivMenu.setImageResource(item.second)
            ivMenu.setColorFilter(
                if (i == selectedItemPosition) ContextCompat.getColor(context!!, R.color.white)
                else ContextCompat.getColor(context!!, R.color.grey_800)
            )

            val tvTitle = TextView(context)
            tvTitle.text = item.first
            tvTitle.setTextColor(
                if (i == selectedItemPosition) ContextCompat.getColor(context!!, R.color.white)
                else ContextCompat.getColor(context!!, R.color.grey_800)
            )
            tvTitle.gravity = Gravity.CENTER

            val attrs = intArrayOf(R.attr.selectableItemBackground)
            val typedArray = context.obtainStyledAttributes(attrs)
            val backgroundResource = typedArray.getResourceId(0, 0)
            container.setBackgroundResource(backgroundResource)
            typedArray.recycle()
            container.isClickable = true
            val lp = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 1f
            lp.gravity = Gravity.CENTER
            container.layoutParams = lp
            container.setPadding(padding, padding * 2, padding, padding * 2)
            if(i == selectedItemPosition) {
                container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.material_grey_900))
            }
            container.onClick {
                if(selectedItemPosition == i && selectedItemPosition != 3) return@onClick
                selectedItemPosition = i
                listener?.setOnNavigationItemSelected(i)
                initUI()
            }
            container.addView(ivMenu)
            container.addView(tvTitle)
            addView(container)
        }
    }

    fun setNavigationListener(listener: HockeyBottomNavigationListener) {
        this.listener = listener
    }

    interface HockeyBottomNavigationListener {
        fun setOnNavigationItemSelected(itemPosition: Int)
    }
}