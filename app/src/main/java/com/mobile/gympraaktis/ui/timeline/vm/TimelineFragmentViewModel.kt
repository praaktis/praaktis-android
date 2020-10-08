package com.mobile.gympraaktis.ui.timeline.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.amitshekhar.utils.NetworkUtils
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.Result
import com.mobile.gympraaktis.data.datasource.AttemptHistoryBoundaryCallback
import com.mobile.gympraaktis.data.db.PraaktisDatabase
import com.mobile.gympraaktis.data.entities.AttemptEntity
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.entities.AttemptDTO
import com.mobile.gympraaktis.domain.entities.toEntity
import com.mobile.gympraaktis.domain.entities.toTimelineEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

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

    private val _pagingStateLiveData = LiveEvent<Result<Nothing>>()
    val pagingStateLiveData: LiveData<Result<Nothing>> get() = _pagingStateLiveData

    fun getPagedAttemptHistory(): LiveData<PagedList<AttemptEntity>> {
        return LivePagedListBuilder(
            PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                .getAttempts(),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()
        )
            .setBoundaryCallback(
                AttemptHistoryBoundaryCallback(
                    Dispatchers.IO,
                    handleResponse = { page, response ->
                        PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                            .insertAttempts(response.map { it.toEntity(page) })
                    },
                    onZeroLoad = {
                        fetchData(it)
                    },
                    onLoadMore = {
                        fetchData(it)
                    }
                )
            )
            .build()
    }

    private suspend fun fetchData(
        page: Int
    ): List<AttemptDTO> {
        _pagingStateLiveData.postValue(Result.loading())
        return runCatching {
            userService.getAttemptHistory(page)
        }.onSuccess {
            if (page == 1 && it.results.isEmpty()) {
                _pagingStateLiveData.postValue(Result.empty())
            } else {
                _pagingStateLiveData.postValue(Result.success())
            }
            Timber.d(it.toString())
        }.onFailure {
            if (it is HttpException && it.code() == 404) {
                val endOfPage = HttpException(
                    Response.error<Nothing>(
                        404,
                        "{\"detail\":\"End of page.\"}".toResponseBody()
                    )
                )
                _pagingStateLiveData.postValue(Result.error(throwable = endOfPage))
                onError(endOfPage)
            } else {
                onError(it)
                _pagingStateLiveData.postValue(Result.error(throwable = it))
            }
            Timber.d(it.toString())
        }.map {
            it.results
        }.getOrElse {
            emptyList()
        }
    }

    fun refreshAttemptHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            PraaktisDatabase.getInstance(getApplication()).getAttemptHistoryDao()
                .removeAttemptHistory()
        }
    }


}