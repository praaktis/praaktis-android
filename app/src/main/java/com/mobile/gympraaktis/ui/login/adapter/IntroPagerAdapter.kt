package com.mobile.gympraaktis.ui.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.databinding.LayoutIntroFirstBinding
import com.mobile.gympraaktis.databinding.LayoutIntroSecondBinding
import com.mobile.gympraaktis.databinding.LayoutIntroThirdBinding

class IntroPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout: View = when (position) {
            0 -> {
                LayoutIntroFirstBinding.inflate(LayoutInflater.from(container.context), container, false).root
            }
            1 -> {
                LayoutIntroSecondBinding.inflate(LayoutInflater.from(container.context), container, false).root
            }
            else -> {
                LayoutIntroThirdBinding.inflate(LayoutInflater.from(container.context), container, false).root
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