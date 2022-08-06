package com.mobile.gympraaktis.ui.subscription_plans.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.gson.annotations.SerializedName
import com.mobile.gympraaktis.base.BaseActivity
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.billing.BillingClientWrapper
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.ui.subscription_plans.view.SubscriptionPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

class SubscriptionPlansViewModel(app: Application) : BaseViewModel(app) {

    companion object {
        private const val MAX_CURRENT_PURCHASES_ALLOWED = 1
    }

    val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    val subscriptionDataFlows = combine(
        BillingClientWrapper.practiceProductWithProductDetails,
        BillingClientWrapper.clubProductWithProductDetails,
        BillingClientWrapper.purchases,
    ) { practiceProducts, clubProducts, purchases ->
        val activePlans = mutableListOf<SubscriptionPlan>()

        val practicePlans = practiceProducts.map {
            SubscriptionPlan(
                it.productId,
                it.name,
                it.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice
                    ?: "0",
                it.description.split("\\R".toRegex()),
                it,
                purchases.find { purchase ->
                    purchase.products.contains(it.productId)
                } != null,
                BillingClientWrapper.plans.find { plans ->
                    plans.iapKey == it.productId
                }?.praaktisKey ?: BillingClientWrapper.trialPlan.praaktisKey
            ).apply {
                if (isActive) activePlans.add(this)
            }
        }

        val clubPlans = clubProducts.map {
            SubscriptionPlan(
                it.productId,
                it.name,
                it.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice
                    ?: "0",
                it.description.split("\\R".toRegex()),
                it,
                purchases.find { purchase ->
                    purchase.products.contains(it.productId)
                } != null,
                BillingClientWrapper.plans.find { plans ->
                    plans.iapKey == it.productId
                }?.praaktisKey ?: BillingClientWrapper.trialPlan.praaktisKey
            ).apply {
                if (isActive) activePlans.add(this)
            }
        }

        if ((practicePlans.isNotEmpty() || clubPlans.isNotEmpty()) && activePlans.isEmpty()) {
            activePlans.add(
                SubscriptionPlan(
                    BillingClientWrapper.trialPlan.iapKey,
                    "Trial",
                    "$0.00",
                    listOf("5 Player/Patient\n100 Attempts"),
                    null,
                    true,
                    BillingClientWrapper.trialPlan.praaktisKey
                )
            )
        }

        SubscriptionData(
            practicePlans,
            clubPlans,
            activePlans,
            purchases,
        )
    }

    fun purchase(
        subscriptionPlan: SubscriptionPlan,
        activity: BaseActivity<*>,
        userIdHash: String
    ) {
        viewModelScope.launch {
            val oldPurchaseToken: String

            val offerToken =
                subscriptionPlan.skuDetails?.subscriptionOfferDetails?.first()?.offerToken

            val currentPurchases = BillingClientWrapper.purchases.value

            // Get current purchase. In this app, a user can only have one current purchase at
            // any given time.
            if (currentPurchases.isNotEmpty() &&
                currentPurchases.size == MAX_CURRENT_PURCHASES_ALLOWED
            ) {
                // This either an upgrade, downgrade, or conversion purchase.
                val currentPurchase = currentPurchases.first()

                // Get the token from current purchase.
                oldPurchaseToken = currentPurchase.purchaseToken

                // if its the same product
                if (subscriptionPlan.skuDetails?.productId == currentPurchase.products.first()) {
                    return@launch
                }

                val billingParams = offerToken?.let {
                    upDowngradeBillingFlowParamsBuilder(
                        productDetails = subscriptionPlan.skuDetails,
                        offerToken = it,
                        oldToken = oldPurchaseToken,
                        userIdHash,
                    )
                }

                if (billingParams != null) {
                    BillingClientWrapper.purchase(
                        activity,
                        billingParams,
                        userIdHash,
                    )
                }
            } else if (currentPurchases.isEmpty()) {
                // This is a normal purchase.
                subscriptionPlan.skuDetails?.let {
                    BillingClientWrapper.purchase(
                        activity,
                        it,
                        userIdHash,
                    )
                }
            } else if (currentPurchases.isNotEmpty() &&
                currentPurchases.size > MAX_CURRENT_PURCHASES_ALLOWED
            ) {
                // The developer has allowed users  to have more than 1 purchase, so they need to
                /// implement a logic to find which one to use.
                Timber.d("User has more than 1 current purchase.")
            }


        }
    }

    private fun upDowngradeBillingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String,
        oldToken: String,
        userIdHash: String
    ): BillingFlowParams {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        ).setSubscriptionUpdateParams(
            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                .setOldPurchaseToken(oldToken)
                .setReplaceProrationMode(
                    BillingFlowParams.ProrationMode.IMMEDIATE_AND_CHARGE_FULL_PRICE
                )
                .build()
        )
            .setObfuscatedProfileId(userIdHash)
            .setObfuscatedAccountId(userIdHash)
            .build()
    }

    val updatePurchaseEvent: LiveEvent<String> = LiveEvent()

    fun updatePlan(plan: SubscriptionPlan) {
        userRepository.updatePurchase(plan.praaktisKey)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({

                val json = JSONObject(it.string())
                val message = json.optString("message")
                updatePurchaseEvent.postValue(message)
            }, ::onError)

    }

    val purchases: Flow<List<Purchase>> = BillingClientWrapper.purchases

}

data class SubscriptionData(
    val practiceProducts: List<SubscriptionPlan>,
    val clubProducts: List<SubscriptionPlan>,
    val activeProducts: List<SubscriptionPlan>,
    val purchases: List<Purchase>
)

data class UpdatePurchaseBody(
    @SerializedName("purchased_plan")
    val purchasedPlanId: Int,
)