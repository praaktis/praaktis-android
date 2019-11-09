package com.mobile.praaktishockey.ui.details.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.entities.AnalysisDTO
import com.mobile.praaktishockey.domain.entities.ComparisonDTO
import com.mobile.praaktishockey.domain.entities.MeVsOtherChallenge
import com.mobile.praaktishockey.domain.extension.dpToPx
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.details.adapter.ScoresAdapter
import com.mobile.praaktishockey.ui.details.vm.ComparisonViewModel
import kotlinx.android.synthetic.main.fragment_me_vs_friends.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MeVsFriendsFragment constructor(override val layoutId: Int = R.layout.fragment_me_vs_friends)
    : BaseFragment() {

    companion object {
        @JvmField
        val TAG  = MeVsFriendsFragment::class.java.simpleName
        @JvmStatic
        fun getInstance(data: AnalysisDTO): Fragment {
            val fragment = MeVsFriendsFragment()
            val bundle = Bundle()
            bundle.putSerializable("data", data)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ComparisonViewModel
        get() = getViewModel { ComparisonViewModel(activity.application) }

    private val analysisDTO by lazy { arguments!!.getSerializable("data") as AnalysisDTO }
    private var comparisonData: ComparisonDTO? = null
    private var meVsFriendsChallenge: MeVsOtherChallenge? = null

    override fun initUI(savedInstanceState: Bundle?) {
        initViewMode()
    }

    private fun initViewMode() {
        mViewModel.getMeVsOthers()
        mViewModel.meVsOthersEvent.observe(this, androidx.lifecycle.Observer {
            comparisonData = it
            if (it.friends.challenges == null) {
                context?.makeToast("No data")
                fragmentManager?.popBackStackImmediate()
                progressLoadingDialog.dismiss()
                return@Observer
            }
            meVsFriendsChallenge = it.friends.challenges.find { it.name.equals(analysisDTO.name) }
            initInfo()
            initChart()
            initFriendsScoreList()
        })
    }

    private fun initInfo() {
        val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val decimalFormatter = numberFormat as DecimalFormat
        decimalFormatter.applyPattern("##.#")
        with(analysisDTO) {
            tvMeHighest.text = "${decimalFormatter.format(maxScore)}%"
            tvMeAverage.text = "${decimalFormatter.format(averageScore)}%"
        }
        with(meVsFriendsChallenge!!) {
            tvMeRank.text = "${rank}"
            tvHighestFriends.text = "${decimalFormatter.format(maxScore)}%"
            tvAverageFriends.text = "${decimalFormatter.format(avgScore)}%"
            tvLowestFriends.text = "${decimalFormatter.format(lowScore)}%"
        }
    }

    private fun initChart() {
        with(lineChart) {
            val desc = Description()
            desc .text = ""
            description = desc

            setBorderColor(Color.BLACK)
            isDoubleTapToZoomEnabled = false
            setPinchZoom(false)
            setTouchEnabled(false)
            invalidate()
        }

        with(lineChart.xAxis) {
            axisMinimum = 0f
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        with(lineChart.axisLeft) {
            isInverted = false
            axisMinimum = 0f // this replaces setStartAtZero(true)
            spaceMin = context!!.dpToPx(40).toFloat()
            labelCount = 4
            mAxisRange = 20f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLimitLinesBehindData(true)
            setDrawGridLinesBehindData(true)
        }

        with(lineChart.axisRight) {
            isEnabled = true
            axisLineColor = Color.BLACK
            setDrawLabels(false)
        }

        with(lineChart.axisLeft) {
            textColor = Color.WHITE
        }

        with(lineChart.legend) {
            isEnabled = false
        }

        if(analysisDTO.chartData.series.isNotEmpty() || meVsFriendsChallenge!!.chartData.series.isNotEmpty()) {
            lineChart.show()
            setChartData()
        } else {
            tvNoLineChartDataAvailable.show()
        }
    }

    private fun setChartData() {

        val entries = ArrayList<Entry>()

        for (i in 0 until analysisDTO.chartData.series.size) {
            val xVal = analysisDTO.chartData.keys[i]
            val yVal = analysisDTO.chartData.series[i]
            entries.add(Entry(xVal.toFloat(), yVal.toFloat()))
        }

        // sort by x-value
        Collections.sort(entries, EntryXComparator())

        // create a dataset and give it a type
        val set1 = LineDataSet(entries, "Your")

        with(set1) {
            lineWidth = 3f
            circleRadius = 6f
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            setCircleColor(Color.parseColor("#E81DEC"))
            color = Color.parseColor("#E81DEC")
            setDrawValues(false)
        }

        val entries2 = ArrayList<Entry>()

        for (i in 0 until meVsFriendsChallenge!!.chartData.keys.size) {
            val xVal = meVsFriendsChallenge!!.chartData.keys[i]
            val yVal = meVsFriendsChallenge!!.chartData.series[i]
            entries2.add(Entry(xVal.toFloat(), yVal.toFloat()))
        }

        // sort by x-value
        Collections.sort(entries2, EntryXComparator())

        // create a dataset and give it a type
        val set2 = LineDataSet(entries2, "Friends")
        with(set2) {
            lineWidth = 3f
            circleRadius = 6f
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            setCircleColor(Color.parseColor("#00CD14"))
            color = Color.parseColor("#00CD14")
            setDrawValues(false)
        }

        // create a data object with the data sets
        val data = LineData(set1, set2)

        // set data
        lineChart.data = data
    }

    private fun initFriendsScoreList() {
        rvFriendsScore.layoutManager = LinearLayoutManager(context)
        rvFriendsScore.adapter = ScoresAdapter(meVsFriendsChallenge!!.leaderboard)
    }
}