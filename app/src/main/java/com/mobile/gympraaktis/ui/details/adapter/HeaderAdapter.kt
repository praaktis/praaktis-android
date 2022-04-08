package com.mobile.gympraaktis.ui.details.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.gympraaktis.R

class HeaderAdapter(
    private val text: String,
    private val textSize: Float? = null,
    private val textColor: Int? = null,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_header, parent, false
            )
        ) {}

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder.itemView as AppCompatTextView).apply {
            text = this@HeaderAdapter.text
            if (this@HeaderAdapter.textSize != null)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, this@HeaderAdapter.textSize)
            if (this@HeaderAdapter.textColor != null)
                setTextColor(this@HeaderAdapter.textColor)
        }
    }

    override fun getItemCount() = 1
}