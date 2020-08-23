package com.mobile.gympraaktis.ui.settings.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.extension.dp
import com.mobile.gympraaktis.domain.extension.updatePadding
import timber.log.Timber

class LanguageAdapter(
    context: Context,
    private val objects: List<Pair<Int, String>>
) : ArrayAdapter<String>(context, 0, objects.map { it.second }) {

    private var selectedPosition: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        parent.updatePadding(top = 9.dp)
        var view = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_language, parent, false)
        }
        val item = objects[position]
        view?.findViewById<AppCompatTextView>(R.id.tv_name)?.text = item.second
        view?.findViewById<AppCompatImageView>(R.id.iv_icon)?.setImageResource(item.first)
        Timber.d("HELLOMI???")
        if (selectedPosition == position) {
            view?.setBackgroundColor(ContextCompat.getColor(context, R.color.white_alpha_50))
        } else {
            view?.setBackgroundColor(Color.TRANSPARENT)
        }

        return view!!
    }

    override fun getItem(position: Int): String? {
        return objects[position].second
    }

    fun invalidateOnSelected(position: Int) {
        selectedPosition = position
    }

}