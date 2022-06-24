package com.mobile.gympraaktis.ui.subscription_plans.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.android.billingclient.api.ProductDetails
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.billing.BillingClientWrapper
import com.mobile.gympraaktis.databinding.FragmentSubscriptionPlansBinding
import com.mobile.gympraaktis.ui.details.adapter.HeaderAdapter
import com.mobile.gympraaktis.ui.details.vm.DetailsViewModel
import com.mobile.gympraaktis.ui.subscription_plans.vm.SubscriptionPlansViewModel
import timber.log.Timber

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

        val clubProductsAdapter = SubscriptionPlanAdapter {
            BillingClientWrapper.purchase(activity, it.skuDetails)
        }

        val practiceProductsAdapter = SubscriptionPlanAdapter {
            BillingClientWrapper.purchase(activity, it.skuDetails)
        }

        binding.rvSubscriptionPlans.adapter = ConcatAdapter(
            HeaderAdapter("Practice Subscription", 20f, Color.WHITE),
            practiceProductsAdapter,
            HeaderAdapter("Club Subscription", 20f, Color.WHITE),
            clubProductsAdapter,
        )

        BillingClientWrapper.queryPracticeProducts(object :
            BillingClientWrapper.OnQueryProductsListener {
            override fun onSuccess(products: List<ProductDetails>) {
                Timber.d("PRODUCTS")
                Timber.d(products.toString())

                val list = mutableListOf<SubscriptionPlan>()

                products.forEach {
                    list.add(SubscriptionPlan(it.productId, it.title, it.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice ?: "0", 0, 0, it))
                }

                practiceProductsAdapter.submitList(list)
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                Timber.d(error.debugMessage)
                Timber.d(error.responseCode.toString())
            }

        })

        BillingClientWrapper.queryClubProducts(object :
            BillingClientWrapper.OnQueryProductsListener {
            override fun onSuccess(products: List<ProductDetails>) {
                Timber.d("PRODUCTS")
                Timber.d(products.toString())

                val list = mutableListOf<SubscriptionPlan>()

                products.forEach {
                    list.add(SubscriptionPlan(it.productId, it.title, it.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice ?: "0", 0, 0, it))
                }

                clubProductsAdapter.submitList(list)
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                Timber.d(error.debugMessage)
                Timber.d(error.responseCode.toString())
            }

        })


    }
}