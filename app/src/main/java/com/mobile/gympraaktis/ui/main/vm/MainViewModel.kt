package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.billing.BillingClientWrapper
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.entities.RoutineEntity
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.StoreResultModel
import com.mobile.gympraaktis.domain.entities.toAnalysisEntityList
import com.mobile.gympraaktis.domain.entities.toDashboardEntity
import com.mobile.gympraaktis.domain.entities.toRoutineEntity
import com.mobile.gympraaktis.ui.subscription_plans.view.SubscriptionPlan
import com.mobile.gympraaktis.ui.subscription_plans.vm.SubscriptionData
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val authRepository by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }
    private val praaktisDao by lazy {
        PraaktisDatabase.getInstance(getApplication()).getPraaktisDao()
    }
    private val dashboardDao by lazy {
        PraaktisDatabase.getInstance(getApplication()).getDashboardDao()
    }

    val challengesEvent: LiveData<List<RoutineEntity>> get() = dashboardDao.getRoutinesLiveData()
    private var challengesDisposable: Disposable? = null
    private var fcmDisposable: Disposable? = null

    fun getChallenges() {
        challengesDisposable = userRepository.getChallenges()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                viewModelScope.launch(Dispatchers.IO) {
                    settingsStorage.setChallenges(it)
                    dashboardDao.insertRoutines(it.map {
                        it.toRoutineEntity()
                    })
                }
            }, ::onError)
        addDisposable(challengesDisposable!!)
    }

    fun fetchDashboardData() {
        userRepository.getDashboardData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                if (it != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        dashboardDao.apply {
                            val analysis = it.toAnalysisEntityList()
                            setDashboardData(
                                it.toDashboardEntity(),
                                analysis.first,
                                analysis.second,
                                analysis.third,
                                analysis.fourth,
                                analysis.fifth,
                            )
//                            insertRoutines(it.routines.map { it.toRoutineEntity() })
                        }
                    }
                }
            }, ::onError)
    }

    fun checkFcmToken() {
        if (!settingsStorage.isSentFcmToken) {
            fcmDisposable = authRepository.registerDevice(settingsStorage.fcmToken)
                .doOnSubscribe { showHideEvent.postValue(true) }
                .doAfterTerminate { showHideEvent.postValue(false) }
                .subscribe({
                    settingsStorage.isSentFcmToken = true
                }, ::onError)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            praaktisDao.getOfflineExerciseResults().collectLatest {
                it.firstOrNull()?.let { result ->
                    userRepository.storeResult(result)
                        .doOnSubscribe { showHideEvent.postValue(true) }
                        .doAfterTerminate { showHideEvent.postValue(false) }
                        .subscribe({
                            viewModelScope.launch(Dispatchers.IO) {
                                praaktisDao.removeOfflineExerciseResult(result)
                            }
                            fetchDashboardData()
                            refreshAttemptHistory()
                        }, ::onError)
                }
            }
        }

    }

    private fun refreshAttemptHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                .removeAttemptHistory()
        }
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
                    "0.00",
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


}

class UploadResultWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val storeResultModel: StoreResultModel
) : CoroutineWorker(appContext, workerParams) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }
    private val praaktisDao by lazy { PraaktisDatabase.getInstance(appContext).getPraaktisDao() }

    override suspend fun doWork(): Result {


        val result = kotlin.runCatching {
            userRepository.storeResultCoroutines(storeResultModel)
        }.onSuccess {

        }.onFailure {

        }


        return if (result.isSuccess) {
            val data = result.getOrNull()
            Result.success()
        } else {
            Result.failure()
        }

    }

}