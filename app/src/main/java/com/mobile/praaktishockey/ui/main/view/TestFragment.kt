package com.mobile.praaktishockey.ui.main.view

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentTestBinding
import com.mobile.praaktishockey.domain.common.shape.CurvedEdgeTreatment
import com.mobile.praaktishockey.ui.main.vm.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestFragment(override val layoutId: Int = R.layout.fragment_test) :
    BaseFragment<FragmentTestBinding>(), AppBarLayout.OnOffsetChangedListener {

    override val mViewModel: BaseViewModel
        get() = DashboardViewModel(activity.application)

    override fun initUI(savedInstanceState: Bundle?) {

        binding.appbar.addOnOffsetChangedListener(this)

        setupCurvedLayout()

    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val percentage =
            Math.abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
        Log.d("TestFragment", percentage.toString())
        Log.d("TestFragment", verticalOffset.toString())

        if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
            //  Collapsed
            Log.d("TestFragment", "Collapsed")

        } else {
            //Expanded
            Log.d("TestFragment", "Expanded")
        }

        changeShapeOfCurvedLayout(percentage)
    }

    private fun changeShapeOfCurvedLayout(percentage: Float) {
        lifecycleScope.launch(Dispatchers.Default) {
            val p = 1 - percentage
            val curveSize = binding.root.width * 0.20f

            binding.cvCurvedLayout.apply {
//                clipToOutline = false
//                setContentPadding(0, curveSize.toInt(), 0, 0)

                val shapeAppearanceModel = ShapeAppearanceModel.Builder()
                    .setTopEdge(CurvedEdgeTreatment(curveSize * p))
                    .build()

                /*val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                    setTint(Color.WHITE)
                    setUseTintColorForShadow(false)
                    paintStyle = Paint.Style.FILL
                    shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
                    elevation = 8.dp.toFloat()
                }*/

                withContext(Dispatchers.Main) {
                    binding.cvCurvedLayout.shapeAppearanceModel = shapeAppearanceModel
//                    background = shapeDrawable
                }
            }
        }
    }

    private fun setupCurvedLayout() {
        binding.root.post {
            binding.cvCurvedLayout.apply {
                val curveSize = binding.root.width * 0.22f
                clipToOutline = false
                setContentPadding(0, curveSize.toInt(), 0, 0)

                val shapeAppearanceModel = ShapeAppearanceModel.Builder()
                    .setTopEdge(CurvedEdgeTreatment(curveSize))
                    .build()

                /*val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                    initializeElevationOverlay(context)
                    setTint(Color.WHITE)
                    setUseTintColorForShadow(false)
                    paintStyle = Paint.Style.FILL
                    shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
                    elevation = 8.dp.toFloat()
                }
                background = shapeDrawable*/
                binding.cvCurvedLayout.shapeAppearanceModel = shapeAppearanceModel
            }
        }
    }


}
