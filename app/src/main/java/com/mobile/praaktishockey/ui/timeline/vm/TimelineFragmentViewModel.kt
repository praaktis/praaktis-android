package com.mobile.praaktishockey.ui.timeline.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.praaktishockey.base.BaseViewModel
import com.mobile.praaktishockey.data.db.PraaktisDatabase
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.entities.toTimelineEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TimelineFragmentViewModel(app: Application) : BaseViewModel(app) {

    val userService by lazy { UserServiceRepository.UserServiceRepositoryImpl.getInstance() }

    init {
        fetchTimelineData()
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