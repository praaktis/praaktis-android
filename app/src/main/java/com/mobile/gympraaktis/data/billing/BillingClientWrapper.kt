package com.mobile.gympraaktis.data.billing

import android.app.Activity
import com.android.billingclient.api.*
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.domain.common.md5
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

object BillingClientWrapper : PurchasesUpdatedListener {

    val trialPlan = SubscriptionPlanKey(1, "trial")
    val practiceBasicPlan = SubscriptionPlanKey(2, "practice_basic_monthly")
    val practicePremiumPlan = SubscriptionPlanKey(3, "practice_premium_monthly")
    val clubBasicPlan = SubscriptionPlanKey(4, "club_basic_monthly")
    val clubPremium = SubscriptionPlanKey(5, "club_premium_monthly")

    val plans = listOf(
        trialPlan,
        practiceBasicPlan,
        practicePremiumPlan,
        clubBasicPlan,
        clubPremium
    )

    interface OnQueryProductsListener {
        fun onSuccess(products: List<ProductDetails>)
        fun onFailure(error: Error)
    }

    class Error(val responseCode: Int, val debugMessage: String)

    // Initialize the BillingClient.
    private val billingClient by lazy {
        BillingClient
            .newBuilder(PraaktisApp.getApplication())
            .enablePendingPurchases()
            .setListener(this)
            .build()
    }

    // New Subscription ProductDetails
    private val _clubProductWithProductDetails =
        MutableStateFlow<List<ProductDetails>>(emptyList())
    val clubProductWithProductDetails = _clubProductWithProductDetails.asStateFlow()

    private val _practiceProductWithProductDetails =
        MutableStateFlow<List<ProductDetails>>(emptyList())
    val practiceProductWithProductDetails = _practiceProductWithProductDetails.asStateFlow()

    // Current Purchases
    private val _userPurchases = MutableStateFlow<List<Purchase>>(listOf())
    val userPurchases = _userPurchases.asStateFlow()

    private val _allPurchases = MutableStateFlow<List<Purchase>>(listOf())
    val allPurchases = _allPurchases.asStateFlow()

    // Tracks new purchases acknowledgement state.
    // Set to true when a purchase is acknowledged and false when not.
    private val _isNewPurchaseAcknowledged = MutableStateFlow(value = false)
    val isNewPurchaseAcknowledged = _isNewPurchaseAcknowledged.asStateFlow()


    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        val userIdHash = SettingsStorage.instance.getProfile()!!.id.toString().md5()

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
            && !purchases.isNullOrEmpty()
        ) {
            val userPurchases = purchases.filter {
                it.accountIdentifiers?.obfuscatedProfileId == userIdHash
            }


            // Post new purchase List to _purchases
            _userPurchases.value = userPurchases
            _allPurchases.value = purchases

            // Then, handle the purchases
            for (purchase in userPurchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Timber.e("User has cancelled")
        } else {
            // Handle any other error codes.
        }
    }

    // Perform new subscription purchases' acknowledgement client side.
    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(
                    params
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        _isNewPurchaseAcknowledged.value = true
                    } else {
                        Timber.e(billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                block()
                queryPurchases()
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun queryAllProducts(listener: OnQueryProductsListener) {
        val skusList = listOf(
            practiceBasicPlan.iapKey,
            practicePremiumPlan.iapKey,
            clubBasicPlan.iapKey,
            clubPremium.iapKey
        )

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList.toMutableList()
                val practiceProducts = products.filter {
                    listOf(
                        practiceBasicPlan.iapKey,
                        practicePremiumPlan.iapKey
                    ).contains(it.productId)
                }

                val clubProducts = products.filter {
                    listOf(clubBasicPlan.iapKey, clubPremium.iapKey).contains(it.productId)
                }

                _practiceProductWithProductDetails.value = practiceProducts
                _clubProductWithProductDetails.value = clubProducts
                listener.onSuccess(products)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun queryPracticeProducts(listener: OnQueryProductsListener) {
        val skusList = listOf(
            practiceBasicPlan.iapKey,
            practicePremiumPlan.iapKey,
        )

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList.toMutableList()
                _practiceProductWithProductDetails.value = products
                listener.onSuccess(products)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun queryClubProducts(listener: OnQueryProductsListener) {
        val skusList = listOf(
            clubBasicPlan.iapKey,
            clubPremium.iapKey
        )

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList.toMutableList()
                _clubProductWithProductDetails.value = products
                listener.onSuccess(products)
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun purchase(activity: Activity, billingParams: BillingFlowParams) {
        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(activity, billingParams)
            }
        }
    }

    fun purchase(activity: Activity, product: ProductDetails, profileId: String) {

        val token = product.subscriptionOfferDetails?.first()?.offerToken

        token?.let {
            val productDetailsParamsList =
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .setOfferToken(it)
                        .build()
                )
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .setObfuscatedProfileId(profileId)
                    .setObfuscatedAccountId(profileId)
                    .build()

            onConnected {
                activity.runOnUiThread {
                    billingClient.launchBillingFlow(activity, billingFlowParams)
                }
            }
        }
    }

    // Query Google Play Billing for existing purchases.
    // New purchases will be provided to PurchasesUpdatedListener.onPurchasesUpdated().
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Timber.e("queryPurchases: BillingClient is not ready")
        }

        // Query for existing subscription products that have been purchased.
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchaseList.isNotEmpty()) {
                    val userIdHash = SettingsStorage.instance.getProfile()!!.id.toString().md5()
                    val userPurchases = purchaseList.filter {
                        userIdHash == it.accountIdentifiers?.obfuscatedProfileId
                    }

                    _userPurchases.value = userPurchases
                    _allPurchases.value = purchaseList
                } else {
                    _userPurchases.value = emptyList()
                    _allPurchases.value = emptyList()
                }

            } else {
                Timber.e(billingResult.debugMessage)
            }
        }
    }


    private fun queryProductsForType(
        skusList: List<String>,
        @BillingClient.ProductType type: String,
        listener: ProductDetailsResponseListener
    ) {
        onConnected {
            val productList = skusList.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(type)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

            billingClient.queryProductDetailsAsync(params.build(), listener)

//            billingClient.querySkuDetailsAsync(
//                SkuDetailsParams.newBuilder().setSkusList(skusList).setType(type).build(),
//                listener
//            )
        }
    }

}

data class SubscriptionPlanKey(
    val praaktisKey: Int,
    val iapKey: String,
)