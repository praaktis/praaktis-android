package com.mobile.gympraaktis.ui.subscription_plans.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.android.billingclient.api.ProductDetails
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.billing.BillingClientWrapper
import com.mobile.gympraaktis.databinding.FragmentSubscriptionPlansBinding
import com.mobile.gympraaktis.domain.common.md5
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.ui.details.adapter.HeaderAdapter
import com.mobile.gympraaktis.ui.details.vm.DetailsViewModel
import com.mobile.gympraaktis.ui.subscription_plans.vm.SubscriptionPlansViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

        val userIdHash = SettingsStorage.instance.getProfile()!!.id.toString().md5()
        Timber.d("USER ID HASH ${SettingsStorage.instance.getProfile()!!.id} $userIdHash")

        val clubProductsAdapter = SubscriptionPlanAdapter {
            mViewModel.purchase(it, activity, userIdHash)
        }

        val practiceProductsAdapter = SubscriptionPlanAdapter {
            mViewModel.purchase(it, activity, userIdHash)
        }

        val concatAdapter = ConcatAdapter(
            HeaderAdapter("Practice Subscription", 20f, Color.WHITE),
            practiceProductsAdapter,
            HeaderAdapter("Club Subscription", 20f, Color.WHITE),
            clubProductsAdapter,
        )

        binding.rvSubscriptionPlans.adapter = concatAdapter

        val activePlanHeader = HeaderAdapter("Active Subscription", 20f, Color.WHITE)
        val activePlanAdapter = SubscriptionPlanAdapter {

        }

        lifecycleScope.launch {
            mViewModel.subscriptionDataFlows.collectLatest {
                lifecycleScope.launch(Dispatchers.Main) {
                    practiceProductsAdapter.submitList(it.practiceProducts)
                    clubProductsAdapter.submitList(it.clubProducts)
                    if (it.activeProducts.isNotEmpty()) {
                        activePlanAdapter.submitList(it.activeProducts)
                        concatAdapter.addAdapter(0, activePlanHeader)
                        concatAdapter.addAdapter(1, activePlanAdapter)
                        binding.rvSubscriptionPlans.scrollToPosition(0)

//                        mViewModel.updatePlan(it.activeProducts.first())
                    } else {
                        concatAdapter.removeAdapter(activePlanHeader)
                        concatAdapter.removeAdapter(activePlanAdapter)
                    }
                }
            }
        }

        mViewModel.updatePurchaseEvent.observe(viewLifecycleOwner) {
            activity.makeToast(it)
            detailsViewModel.fetchDashboardData()
        }

        BillingClientWrapper.queryPracticeProducts(object :
            BillingClientWrapper.OnQueryProductsListener {
            override fun onSuccess(products: List<ProductDetails>) {
                Timber.d("PRODUCTS")
                Timber.d(products.toString())
            }

            override fun onFailure(error: BillingClientWrapper.Error) {
                Timber.d(error.debugMessage)
                Timber.d(error.responseCode.toString())
            }

        })

        lifecycleScope.launch {
            delay(300)
            BillingClientWrapper.queryClubProducts(object :
                BillingClientWrapper.OnQueryProductsListener {
                override fun onSuccess(products: List<ProductDetails>) {
                    Timber.d("PRODUCTS")
                    Timber.d(products.toString())
                }

                override fun onFailure(error: BillingClientWrapper.Error) {
                    Timber.d(error.debugMessage)
                    Timber.d(error.responseCode.toString())
                }

            })
        }


    }
}