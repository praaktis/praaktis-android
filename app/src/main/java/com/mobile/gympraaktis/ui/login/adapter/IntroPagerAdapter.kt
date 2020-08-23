package com.mobile.gympraaktis.ui.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.gympraaktis.R

class IntroPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout: View
        when (position) {
            0 -> {
                layout = LayoutInflater.from(container.context).inflate(R.layout.layout_intro_first, container, false)
            }
            1 -> {
                layout = LayoutInflater.from(container.context).inflate(R.layout.layout_intro_second, container, false)
            }
            else -> {
                layout = LayoutInflater.from(container.context).inflate(R.layout.layout_intro_third, container, false)
            }
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int = 3
}