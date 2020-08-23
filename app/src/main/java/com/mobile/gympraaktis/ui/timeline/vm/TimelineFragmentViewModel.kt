package com.mobile.gympraaktis.ui.timeline.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.toTimelineEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TimelineFragmentViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    init {
//        fetchTimelineData()
    }

    fun observeTimeline() =
        PraaktisDatabase.getInstance(getApplication()).getTimelineDao().getAllTimeline()
            .asLiveData()

    fun fetchTimelineData() {
        userService.getTimelineData()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                GlobalScope.launch(Dispatchers.IO) {
                    PraaktisDatabase.getInstance(getApplication()).getTimelineDao()
                        .removeAndInsertTimeline(it.toTimelineEntities())
                }
            }, ::onError)
    }

}