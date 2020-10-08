package com.mobile.gympraaktis.ui.login.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.gympraaktis.databinding.*
import com.mobile.gympraaktis.domain.extension.updatePadding

class IntroPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout: View = when (position) {
            0 -> {
                LayoutIntroFirstBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                ).apply {
                    layoutLogo.setOnApplyWindowInsetsListener { v, insets ->
                        v.updatePadding(top = insets.systemWindowInsetTop)
                        insets
                    }
                }.root
            }
            1 -> {
                LayoutIntroSecondBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                ).apply {
                    layoutLogo.setOnApplyWindowInsetsListener { v, insets ->
                        v.updatePadding(top = insets.systemWindowInsetTop)
                        insets
                    }
                }.root
            }
            2 -> {
                LayoutIntroThirdBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                ).apply {
                    layoutLogo.setOnApplyWindowInsetsListener { v, insets ->
                        v.updatePadding(top = insets.systemWindowInsetTop)
                        insets
                    }
                }.root
            }
            3 -> {
                LayoutIntroFourthBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                ).apply {
                    layoutLogo.setOnApplyWindowInsetsListener { v, insets ->
                        v.updatePadding(top = insets.systemWindowInsetTop)
                        insets
                    }
                }.root
            }
            else -> {
                LayoutIntroFifthBinding.inflate(
                    LayoutInflater.from(container.context),
                    container,
                    false
                ).apply {
                    layoutLogo.setOnApplyWindowInsetsListener { v, insets ->
                        v.updatePadding(top = insets.systemWindowInsetTop)
                        insets
                    }
                }.root
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

    override fun getCount(): Int = 5
}