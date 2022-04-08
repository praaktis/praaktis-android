package com.mobile.gympraaktis.ui.subscription_plans.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentSubscriptionPlansBinding
import com.mobile.gympraaktis.ui.details.adapter.HeaderAdapter
import com.mobile.gympraaktis.ui.details.vm.DetailsViewModel
import com.mobile.gympraaktis.ui.subscription_plans.vm.SubscriptionPlansViewModel

class SubscriptionPlansFragment(override val layoutId: Int = R.layout.fragment_subscription_plans) :
    BaseFragment<FragmentSubscriptionPlansBinding>() {

    companion object {
        const val TAG = "SubscriptionPlansFragment"

        @JvmStatic
        fun newInstance() =
            SubscriptionPlansFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override val mViewModel: SubscriptionPlansViewModel by viewModels()

    private val detailsViewModel: DetailsViewModel by activityViewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        detailsViewModel.changeTitle("Plans")
        binding.rvSubscriptionPlans.adapter = ConcatAdapter(
            HeaderAdapter("Practice Subscription", 20f, Color.WHITE),
            SubscriptionPlanAdapter().apply {
                submitList(mockPracticePlans)
            },
            HeaderAdapter("Club Subscription", 20f, Color.WHITE),
            SubscriptionPlanAdapter().apply {
                submitList(mockClubPlans)
            },
        )
    }
}