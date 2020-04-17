package com.mobile.praaktishockey.ui.timeline.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.ui.timeline.vm.TimelineFragmentViewModel
import kotlinx.android.synthetic.main.fragment_timeline2.*

class TimelineFragment constructor(override val layoutId: Int = R.layout.fragment_timeline2) :
    BaseFragment() {

    companion object {
        val TAG = TimelineFragment::class.java.simpleName
        fun getInstance(): Fragment = TimelineFragment()
    }

    override val mViewModel: TimelineFragmentViewModel
        get() = getViewModel { TimelineFragmentViewModel(activity.application) }

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.getTimelineData()
    }

    private fun initPager() {
        tlTimeline.setupWithViewPager(vpTimeline)
        with(vpTimeline) {
//            vpTimeline.adapter = TimelinePagerAdapter(childFragmentManager, mViewModel.timelineDataEvent.value!!)
            vpTimeline.offscreenPageLimit = 4
            for (i in 0..tlTimeline.tabCount) {
                val tab = tlTimeline.getTabAt(i)
                tab?.setCustomView(R.layout.item_timeline_type)
                val tvTab = tab?.customView?.findViewById<TextView>(R.id.tvTimelineItem)
                val cvItem = tab?.customView?.findViewById<CardView>(R.id.cvItemTimeline)
                val tvItemTxt =
                    tab?.customView?.findViewById<AppCompatTextView>(R.id.tvTimelineItem)

                when (i) {
                    0 -> {
                        tvTab?.text = context.getString(R.string.all)
                        cvItem?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.grey_800
                            )
                        )
                        tvItemTxt?.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    }
                    1 -> tvTab?.text = getString(R.string.drag_flick)
                    2 -> tvTab?.text = getString(R.string.low_backhand)
                    else -> tvTab?.text = getString(R.string.trap)
                }
            }
            vpTimeline.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0..tlTimeline.tabCount) {
                        val tab = tlTimeline.getTabAt(i)
                        val cvItem = tab?.customView?.findViewById<CardView>(R.id.cvItemTimeline)
                        val tvItemTxt =
                            tab?.customView?.findViewById<AppCompatTextView>(R.id.tvTimelineItem)

                        cvItem?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                if (i == position) R.color.grey_800 else R.color.dark_grey
                            )
                        )

                        tvItemTxt?.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                if (i == position) R.color.white else R.color.grey_900_alpha_70
                            )
                        )
                    }
                }
            })
        }
    }
}