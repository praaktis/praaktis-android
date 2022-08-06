package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.BoolLV
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

    fun observeDashboard() = PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getDashboardData()

    fun observeAnalysisComplete() = PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getAllAnalysisData()
        .asLiveData()

    fun observePlayerAnalysis() =
        PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getPlayersAnalysis()
            .asLiveData()

    val showHideLoader = BoolLV()

    fun fetchDashboardData() {
        userService.getDashboardData()
            .doOnSubscribe { showHideLoader.postValue(true) }
            .doAfterTerminate { showHideLoader.postValue(false) }
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
                                analysis.fourth,
                                analysis.fifth,
                            )
//                            insertRoutines(it.routines.map { it.toRoutineEntity() })
                        }
                    }
                }
            }, ::onError)
    }

}