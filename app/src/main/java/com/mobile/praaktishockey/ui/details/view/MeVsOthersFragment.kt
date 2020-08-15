package com.mobile.praaktishockey.ui.details.view

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.EntryXComparator
import com.github.mikephil.charting.utils.MPPointF
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.data.entities.AnalysisComplete
import com.mobile.praaktishockey.databinding.FragmentMeVsOthersBinding
import com.mobile.praaktishockey.domain.entities.AnalysisDTO
import com.mobile.praaktishockey.domain.entities.ComparisonDTO
import com.mobile.praaktishockey.domain.entities.MeVsOtherChallenge
import com.mobile.praaktishockey.domain.extension.dpToPx
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.hide
import com.mobile.praaktishockey.domain.extension.show
import com.mobile.praaktishockey.ui.details.vm.ComparisonViewModel
import kotlinx.android.synthetic.main.fragment_me_vs_others.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MeVsOthersFragment constructor(override val layoutId: Int = R.layout.fragment_me_vs_others) :
    BaseFragment<FragmentMeVsOthersBinding>() {

    companion object {
        const val TAG = "MeVsOthersFragment"

        @JvmStatic
        fun getInstance(data: AnalysisComplete): Fragment {
            val fragment = MeVsOthersFragment()
            val bundle = Bundle()
            bundle.putSerializable("data", data)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val mViewModel: ComparisonViewModel
        get() = getViewModel { ComparisonViewModel(activity.application) }

    private val analysisDTO by lazy { arguments!!.getSerializable("data") as AnalysisComplete }
    private var comparisonData: ComparisonDTO? = null
    private var meVsOtherChallenge: MeVsOtherChallenge? = null

    override fun initUI(savedInstanceState: Bundle?) {
        initViewMode()
    }

    private fun initViewMode() {
        mViewModel.getMeVsOthers()
        mViewModel.meVsOthersEvent.observe(this, androidx.lifecycle.Observer {
            comparisonData = it
            meVsOtherChallenge = it.others.challenges.find { it.id == analysisDTO.analysisEntity.id }
            initInfo()
            initChart()
            initPieChart()
        })
    }

    private fun initInfo() {
        val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val decimalFormatter = numberFormat as DecimalFormat
        decimalFormatter.applyPattern("##.#")
        with(analysisDTO) {
            tvMeHighest.text = "${decimalFormatter.format(analysisEntity.maxScore)}%"
            tvMeAverage.text = "${decimalFormatter.format(analysisEntity.averageScore)}%"
        }
        with(meVsOtherChallenge!!) {
            tvMeRank.text = "${rank}"
            tvHighestOthers.text = "${decimalFormatter.format(maxScore)}%"
            tvAverageOthers.text = "${decimalFormatter.format(avgScore)}%"
            tvLowestOthers.text = "${decimalFormatter.format(lowScore)}%"
        }
        with(comparisonData!!) {
            tvOthersInfo.text = "$ability / $gender / $ageGroup"
        }
    }

    private fun initChart() {
        with(lineChart) {
            val desc = com.github.mikephil.charting.components.Description()
            desc.text = ""
            description = desc

            setBorderColor(android.graphics.Color.BLACK)
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
            axisLineColor = android.graphics.Color.BLACK
            setDrawLabels(false)
        }

        with(lineChart.axisLeft) {
            textColor = android.graphics.Color.WHITE
        }

        with(lineChart.legend) {
            isEnabled = false
        }

        if (analysisDTO.chartData.series.isNotEmpty() || meVsOtherChallenge!!.chartData.series.isNotEmpty()) {
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

        for (i in 0 until meVsOtherChallenge!!.chartData.keys.size) {
            val xVal = meVsOtherChallenge!!.chartData.keys[i]
            val yVal = meVsOtherChallenge!!.chartData.series[i]
            entries2.add(Entry(xVal.toFloat(), yVal.toFloat()))
        }

        // sort by x-value
        Collections.sort(entries2, EntryXComparator())

        // create a dataset and give it a type
        val set2 = LineDataSet(entries2, "Friends")
        with(set2) {
            val chartLineColor = ContextCompat.getColor(requireContext(), R.color.green_500)

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
        lineChart.data = data
    }

    private fun initPieChart() {
        with(pieChart) {
            show()
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = false
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
        }

        with(pieChart.legend) {
            isEnabled = false
        }

        if (meVsOtherChallenge?.attemptChart!!.me != 0 && meVsOtherChallenge?.attemptChart!!.others != 0) {
            pieChart.show()
            setPieChartData()
        } else {
            pieChart.hide()
            tvNoPieChartDataAvailable.show()
        }
    }

    private fun setPieChartData() {
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(
            PieEntry(
                meVsOtherChallenge?.attemptChart!!.me.toFloat(),
                "Me"
//                    resources.getDrawable(R.drawable.star)
            )
        )
        entries.add(
            PieEntry(
                meVsOtherChallenge?.attemptChart?.others!!.toFloat(),
                "Others"
//                    resources.getDrawable(R.drawable.star)
            )
        )

//        for (i in 0 until 2) {
//            entries.add(
//                PieEntry(
//                    (Math.random() * range + range / 5).toFloat(),
//                    ""
////                    resources.getDrawable(R.drawable.star)
//                )
//            )
//        }

        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 0f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors

        val colors = ArrayList<Int>()

        val color1 = ContextCompat.getColor(requireContext(), R.color.primaryColor)
        val color2 = ContextCompat.getColor(requireContext(), R.color.green_500)

        colors.add(/*Color.parseColor("#00CD14")*/color2)
        colors.add(/*Color.parseColor("#E81DEC")*/color1)

//        for (c in ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c)
//
//        for (c in ColorTemplate.JOYFUL_COLORS)
//            colors.add(c)
//
//        for (c in ColorTemplate.COLORFUL_COLORS)
//            colors.add(c)
//
//        for (c in ColorTemplate.LIBERTY_COLORS)
//            colors.add(c)
//
//        for (c in ColorTemplate.PASTEL_COLORS)
//            colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)

        pieChart.invalidate()
    }
}