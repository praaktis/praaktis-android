package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.toAnalysisEntityList
import com.mobile.gympraaktis.domain.entities.toDashboardEntity
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