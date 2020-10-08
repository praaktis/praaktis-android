package com.mobile.gympraaktis.data.datasource

import androidx.paging.PagedList
import com.mobile.gympraaktis.data.entities.AttemptEntity
import com.mobile.gympraaktis.domain.entities.AttemptDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AttemptHistoryBoundaryCallback(
    private val coroutineContext: CoroutineContext,
    val handleResponse: suspend (Int, List<AttemptDTO>) -> Unit,
    val onZeroLoad: suspend (Int) -> List<AttemptDTO>,
    val onLoadMore: suspend (Int) -> List<AttemptDTO>
) : PagedList.BoundaryCallback<AttemptEntity>() {

    private var page: Int = 1

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        CoroutineScope(coroutineContext).launch {
            val attempts = onZeroLoad(page)
            handleResponse(page, attempts)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: AttemptEntity) {
        super.onItemAtEndLoaded(itemAtEnd)
        CoroutineScope(coroutineContext).launch {
            val nextPage = itemAtEnd.page.inc()
            val attempts = onLoadMore(nextPage)
            handleResponse(nextPage, attempts)
        }
    }

}