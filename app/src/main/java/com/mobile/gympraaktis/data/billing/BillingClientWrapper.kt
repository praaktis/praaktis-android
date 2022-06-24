package com.mobile.gympraaktis.data.billing

import android.app.Activity
import com.android.billingclient.api.*
import com.mobile.gympraaktis.PraaktisApp

object BillingClientWrapper : PurchasesUpdatedListener {

    interface OnQueryProductsListener {
        fun onSuccess(products: List<ProductDetails>)
        fun onFailure(error: Error)
    }

    class Error(val responseCode: Int, val debugMessage: String)

    private val billingClient by lazy {
        BillingClient
            .newBuilder(PraaktisApp.getApplication())
            .enablePendingPurchases()
            .setListener(this)
            .build()
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {

    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                block()
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun queryPracticeProducts(listener: OnQueryProductsListener) {
        val skusList = listOf(
            "practice_basic_monthly",
            "practice_premium_monthly",
        )

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList.toMutableList()
                queryProductsForType(
                    skusList,
                    BillingClient.ProductType.INAPP
                ) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        products.addAll(productDetailsList)
                        listener.onSuccess(products)
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun queryClubProducts(listener: OnQueryProductsListener) {
        val skusList = listOf(
            "club_basic_monthly",
            "club_premium_monthly"
        )

        queryProductsForType(
            skusList,
            BillingClient.ProductType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList.toMutableList()
                queryProductsForType(
                    skusList,
                    BillingClient.ProductType.INAPP
                ) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        products.addAll(productDetailsList)
                        listener.onSuccess(products)
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun purchase(activity: Activity, product: ProductDetails) {

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
                    .build()

            onConnected {
                activity.runOnUiThread {
                    billingClient.launchBillingFlow(activity, billingFlowParams)
                }
            }
        }
//        onConnected {
//            activity.runOnUiThread {
//                billingClient.launchBillingFlow(
//                    activity,
//                    BillingFlowParams.newBuilder().setSkuDetails(product).build()
//                )
//            }
//        }
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

