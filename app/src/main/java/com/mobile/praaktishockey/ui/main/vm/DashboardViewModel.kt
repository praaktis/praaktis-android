package com.mobile.praaktishockey.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.db.PraaktisDatabase
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.entities.toAnalysisEntityList
import com.mobile.praaktishockey.domain.entities.toDashboardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    init {
//        fetchDashboardData()
    }

    fun observeDashboard() =
        PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getDashboardData()
            .asLiveData()

    fun fetchDashboardData() {
        userService.getDashboardData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                if (it != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        PraaktisDatabase.getInstance(getApplication()).getDashboardDao().apply {
                            val analysis = it.toAnalysisEntityList()
                            setDashboardData(
                                it.toDashboardEntity(),
                                analysis.first,
                                analysis.second,
                                analysis.third,
                                analysis.fourth
                            )
                        }
                    }
                    settingsStorage.setDashboard(it)
                }
            }, ::onError)
    }

}