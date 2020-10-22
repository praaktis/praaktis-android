package com.mobile.gympraaktis.ui.login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.mobile.gympraaktis.databinding.LayoutIntroFirstBinding
import com.mobile.gympraaktis.domain.extension.updatePadding
import kotlinx.android.synthetic.main.layout_intro_first.view.*

class IntroInfiniteAdapter(context: Context, private val list: List<Pair<String, Int>>) :
    LoopingPagerAdapter<Pair<String, Int>>(context, list, true) {

    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        val item = list[listPosition]
        convertView.tv_title.text = item.first
        convertView.iv_background_image.setImageResource(item.second)
    }

    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutIntroFirstBinding.inflate(
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