package com.mobile.gympraaktis.ui.details.view

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.AnalysisComplete
import com.mobile.gympraaktis.databinding.FragmentMeVsFriendsBinding
import com.mobile.gympraaktis.domain.entities.ComparisonDTO
import com.mobile.gympraaktis.domain.entities.MeVsOtherChallenge
import com.mobile.gympraaktis.domain.extension.dpToPx
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.domain.extension.show
import com.mobile.gympraaktis.ui.details.adapter.ScoresAdapter
import com.mobile.gympraaktis.ui.details.vm.ComparisonViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MeVsFriendsFragment constructor(override val layoutId: Int = R.layout.fragment_me_vs_friends) :
    BaseFragment<FragmentMeVsFriendsBinding>() {

    companion object {
        const val TAG = "MeVsFriendsFragment"

        @JvmStatic
        fun getInstance(data: AnalysisComplete): Fragment {
            val fragment = MeVsFriendsFragment()
            val bundle = Bundle()
            bundle.putSerializable("data", data)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ComparisonViewModel by viewModels()

    private val analysisDTO by lazy { requireArguments().getSerializable("data") as AnalysisComplete }
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
            meVsFriendsChallenge =
                it.friends.challenges.find { it.id == analysisDTO.analysisEntity.id }
            initInfo()
            initChart()
            initFriendsScoreList()
        })
    }

    private fun initInfo() {
        val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val decimalFormatter = numberFormat as DecimalFormat
        decimalFormatter.applyPattern("##.0")
        with(analysisDTO) {
            binding.tvMeHighest.text = decimalFormatter.format(analysisEntity.maxScore)
            binding.tvMeAverage.text = decimalFormatter.format(analysisEntity.averageScore)
        }
        with(meVsFriendsChallenge!!) {
            binding.tvMeRank.text = "$rank"
            binding.tvHighestFriends.text = decimalFormatter.format(maxScore)
            binding.tvAverageFriends.text = decimalFormatter.format(avgScore)
            binding.tvLowestFriends.text = decimalFormatter.format(lowScore)
        }
    }

    private fun initChart() {
        with(binding.lineChart) {
            val desc = Description()
            desc.text = ""
            description = desc

            setBorderColor(Color.BLACK)
            isDoubleTapToZoomEnabled = false
            setPinchZoom(false)
            setTouchEnabled(false)
            invalidate()
        }

        with(binding.lineChart.xAxis) {
            axisMinimum = 0f
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        with(binding.lineChart.axisLeft) {
            isInverted = false
            axisMinimum = 0f // this replaces setStartAtZero(true)
            spaceMin = requireContext().dpToPx(40).toFloat()
            labelCount = 4
            mAxisRange = 20f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLimitLinesBehindData(true)
            setDrawGridLinesBehindData(true)
        }

        with(binding.lineChart.axisRight) {
            isEnabled = true
            axisLineColor = Color.BLACK
            setDrawLabels(false)
        }

        with(binding.lineChart.axisLeft) {
            textColor = Color.WHITE
        }

        with(binding.lineChart.legend) {
            isEnabled = false
        }

        if (analysisDTO.chartData.series.isNotEmpty() || meVsFriendsChallenge!!.chartData.series.isNotEmpty()) {
            binding.lineChart.show()
            setChartData()
        } else {
            binding.tvNoLineChartDataAvailable.show()
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
            val chartLineColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)

            lineWidth = 3f
            circleRadius = 6f
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            setCircleColor(chartLineColor)
            color = chartLineColor
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
            val chartLineColor = ContextCompat.getColor(requireContext(), R.color.blue_light)

            lineWidth = 3f
            circleRadius = 6f
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            setCircleColor(chartLineColor)
            color = chartLineColor
            setDrawValues(false)
        }

        // create a data object with the data sets
        val data = LineData(set1, set2)

        // set data
        binding.lineChart.data = data
    }

    private fun initFriendsScoreList() {
        binding.rvFriendsScore.layoutManager = LinearLayoutManager(context)
        binding.rvFriendsScore.adapter = ScoresAdapter(meVsFriendsChallenge!!.leaderboard)
    }
}