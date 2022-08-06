package com.mobile.gympraaktis.ui.details.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.toAnalysisEntityList
import com.mobile.gympraaktis.domain.entities.toDashboardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailsViewModel(app: Application) : BaseViewModel(app) {

    private val userRepository by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    fun changeTitle(title: String) {
        _title.value = title
    }

    fun fetchDashboardData() {
        userRepository.getDashboardData()
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