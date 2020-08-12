package com.mobile.praaktishockey.ui.details.view

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.data.entities.AnalysisComplete
import com.mobile.praaktishockey.databinding.FragmentAnalysisBinding
import com.mobile.praaktishockey.databinding.LayoutTargetBottomBinding
import com.mobile.praaktishockey.domain.common.AppGuide
import com.mobile.praaktishockey.domain.common.resettableLazy
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.details.vm.AnalysisViewModel
import com.mobile.praaktishockey.ui.details.vm.DetailsViewModel
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.fragment_analysis.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class AnalysisFragment constructor(override val layoutId: Int = R.layout.fragment_analysis) :
    BaseFragment<FragmentAnalysisBinding>() {

    companion object {
        val TAG: String = AnalysisFragment::class.java.simpleName
        const val ANALYSIS_ITEM = "ANALYSIS_ITEM"

        fun getInstance(item: AnalysisComplete) = AnalysisFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ANALYSIS_ITEM, item)
            }
        }
    }

    override val mViewModel: AnalysisViewModel
        get() = getViewModel { AnalysisViewModel(activity.application) }

    private lateinit var detailsViewModel: DetailsViewModel
    private val analysisData: AnalysisComplete by lazy {
        return@lazy arguments?.getSerializable(ANALYSIS_ITEM) as AnalysisComplete
    }

    override fun initUI(savedInstanceState: Bundle?) {
        detailsViewModel = ViewModelProvider(activity).get(DetailsViewModel::class.java)
        detailsViewModel.changeTitle(analysisData.analysisEntity.name)

        initInfoChallenge()
        initClicks()
        initLineChart()
        initBarChart()

        startGuideIfNecessary()
    }

    private fun initInfoChallenge() {
        with(analysisData) {
            val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
            val decimalFormatter = numberFormat as DecimalFormat
            decimalFormatter.applyPattern("##.#")
            tv_average_score.text = "${decimalFormatter.format(analysisEntity.averageScore)}%"
            tv_best_score.text = "${decimalFormatter.format(analysisEntity.maxScore)}%"
        }
    }

    private fun initClicks() {
        binding.btnMeVsFriends.onClick {
            closeSpotlight()

            val tag = MeVsFriendsFragment.TAG
            activity.showOrReplaceLast(tag) {
                add(R.id.container, MeVsFriendsFragment.getInstance(analysisData), tag)
                    .addToBackStack(tag)
            }
        }
        binding.btnMeVsOthers.onClick {
            val tag = MeVsOthersFragment.TAG
            activity.showOrReplaceLast(tag) {
                add(R.id.container, MeVsOthersFragment.getInstance(analysisData), tag)
                    .addToBackStack(tag)
            }
        }
    }

    private fun initLineChart() {
        with(lineChart) {
            val desc = Description()
            desc.text = ""
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

        if (analysisData.chartData.series.isNotEmpty()) {
            lineChart.show()
            setLineChartData(analysisData.chartData.series.size)
        } else {
            tvNoLineChartDataAvailable.show()
        }
    }

    private fun setLineChartData(count: Int) {

        val entries = ArrayList<Entry>()

        for (i in 0 until count) {
            val xVal = /*if (i != 0) (Math.random() * range).toFloat() else 0f*/
                analysisData.chartData.keys[i]
            val yVal = /*if (i != 0) (Math.random() * range).toFloat() else 0f*/
                analysisData.chartData.series[i]
            entries.add(Entry(xVal.toFloat(), yVal.toFloat()))
        }

        // sort by x-value
        Collections.sort(entries, EntryXComparator())

        // create a dataset and give it a type
        val set1 = LineDataSet(entries, "Your")

        with(set1) {
            val chartColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)
            lineWidth = 3f
            circleRadius = 6f
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            setCircleColor(chartColor)
            color = chartColor
            setDrawValues(false)
        }


        // create a data object with the data sets
        val data = LineData(set1)

        // set data
        lineChart.data = data
    }

    fun initBarChart() {
        with(barChart) {
            setMaxVisibleValueCount(100)
            setPinchZoom(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)

            val desc = Description()
            desc.text = ""
            description = desc
        }

        with(barChart.xAxis) {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f // only intervals of 1 day
            labelCount = 7
            setDrawLabels(false)
        }

        with(barChart.axisLeft) {
            setLabelCount(5, false)
            setPosition(YAxisLabelPosition.OUTSIDE_CHART)
            textColor = Color.WHITE
            spaceTop = 15f
            axisMinimum = 0f // this replaces setStartAtZero(true)
        }

        with(barChart.axisRight) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            spaceTop = 15f
            axisMinimum = 0f // this replaces setStartAtZero(true)
            setDrawLabels(false)
        }

        with(barChart.legend) {
            isEnabled = false
        }

        if (analysisData.attemptChart.series.isNotEmpty()) {
            barChart.show()
            setBarChartData(analysisData.attemptChart.series.size)
        } else {
            tvNoBarChartDataAvailable.show()
        }
    }

    private fun setBarChartData(count: Int) {
        val values = ArrayList<BarEntry>()

        for (i in 0 until count) {
            values.add(BarEntry(i.toFloat(), analysisData.attemptChart.series[i].toFloat()))
        }

        val set1: BarDataSet

        if (barChart.data != null && barChart.data.dataSetCount > 0) {
            set1 = barChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            barChart.data.notifyDataChanged()
            barChart.notifyDataSetChanged()

        } else {
            set1 = BarDataSet(values, "")

            set1.setDrawIcons(false)

            val startColor1 = ContextCompat.getColor(context!!, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(context!!, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(context!!, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(context!!, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(context!!, android.R.color.holo_red_light)

            set1.colors =
                mutableListOf(startColor1, startColor2, startColor3, startColor4, startColor5)
            set1.valueTextColor = Color.BLACK

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.5f

            barChart.data = data
        }
    }

    private val spotlightDelegate = resettableLazy { initAnalysisGuide() }
    private val spotlight by spotlightDelegate

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.btnMeVsFriends.doOnPreDraw {
                spotlight.start()
            }
        }
    }

    fun restartSpotlight() {
        if (spotlightDelegate.isInitialized())
            spotlightDelegate.reset()
        spotlight.start()
    }

    private fun nextTarget() {
        spotlight.next()
    }

    private fun closeSpotlight() {
        spotlight.finish()
    }

    private fun meVsFriendsTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.closeTarget.setOnClickListener { nextTarget() }

        target.customText.text =
            "Get detailed information on your performance and compare to your Friends"

        target.root.updatePadding(bottom = binding.cvAttempts.y.toInt())

        return Target.Builder()
            .setAnchor(binding.btnMeVsFriends)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.btnMeVsFriends.height.toFloat() + 10.dp,
                    binding.btnMeVsFriends.width.toFloat() + 10.dp,
                    25.dp.toFloat()
                )
            )
            .build()
    }

    private fun meVsOthersTarget(): Target {
        val target = LayoutTargetBottomBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }
        target.closeTarget.setOnClickListener { nextTarget() }

        target.customText.text =
            "Get detailed information on your performance and compare to against other Users like you"

        target.root.updatePadding(bottom = binding.cvAttempts.y.toInt())

        return Target.Builder()
            .setAnchor(binding.btnMeVsOthers)
            .setOverlay(target.root)
            .setShape(
                RoundedRectangle(
                    binding.btnMeVsOthers.height.toFloat() + 10.dp,
                    binding.btnMeVsOthers.width.toFloat() + 10.dp,
                    25.dp.toFloat()
                )
            )
            .build()
    }

    private fun initAnalysisGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(meVsFriendsTarget(), meVsOthersTarget())
            .setBackgroundColor(R.color.deep_purple_a400_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    (activity as DetailsActivity).hideInfo()
                }

                override fun onEnded() {
                    (activity as DetailsActivity).showInfo()
                }
            })
            .build()
    }

}