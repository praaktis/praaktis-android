package com.mobile.gympraaktis.data.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.mobile.gympraaktis.data.Result
import com.mobile.gympraaktis.data.repository.UserServiceRepository
import com.mobile.gympraaktis.domain.entities.AttemptDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

class AttemptHistoryDataSource(
    private val repository: UserServiceRepository,
    private val scope: CoroutineScope,
    private val pagingStateLiveData: MutableLiveData<Result<Nothing>>
) : PageKeyedDataSource<Int, AttemptDTO>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, AttemptDTO>
    ) {
        fetchData(1) { list, totalCount ->
            callback.onResult(list, 1, totalCount, null, 2)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, AttemptDTO>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, AttemptDTO>) {
        fetchData(params.key) { list, _ ->
            callback.onResult(list, params.key + 1)
        }
    }

    private fun fetchData(page: Int, callback: (List<AttemptDTO>, Int) -> Unit) {
        scope.launch(Dispatchers.IO) {
            pagingStateLiveData.postValue(Result.loading())
            runCatching {
                repository.getAttemptHistory(page)
            }.onSuccess {
                pagingStateLiveData.postValue(Result.success())
                callback.invoke(it.results, it.count)
                Timber.d(it.toString())
            }.onFailure {
                if (it is HttpException && it.code() == 404) {
                    pagingStateLiveData.postValue(
                        Result.error(
                            throwable = HttpException(
                                Response.error<Nothing>(
                                    404,
                                    "{\"detail\":\"End of page.\"}".toResponseBody()
                                )
                            )
                        )
                    )
                } else {
                    pagingStateLiveData.postValue(Result.error(throwable = it))
                }
                Timber.d(it.toString())
                callback.invoke(emptyList(), 0)
            }
        }
    }

    class Factory(
        private val repository: UserServiceRepository,
        private val scope: CoroutineScope,
        private val pagingStateLiveData: MutableLiveData<Result<Nothing>>
    ) :
        DataSource.Factory<Int, AttemptDTO>() {
        override fun create(): DataSource<Int, AttemptDTO> {
            return AttemptHistoryDataSource(repository, scope, pagingStateLiveData)
        }

        companion object {
            fun config() = PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()
        }
    }
}