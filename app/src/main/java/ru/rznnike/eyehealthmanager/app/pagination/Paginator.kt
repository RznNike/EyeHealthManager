package ru.rznnike.eyehealthmanager.app.pagination

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction2

private const val DEFAULT_PAGE_LIMIT = 20

class Paginator<T : Any>(
    private val coroutineScope: CoroutineScope,
    private val pageRequest: KSuspendFunction2<Int, Int, List<T>>,
    private val viewController: ViewController<T>,
    private var limit: Int = DEFAULT_PAGE_LIMIT
) {
    private var state = State.INIT
    private val data: MutableList<T> = mutableListOf()
    private var loadingJob: Job? = null

    fun getData(): List<T> = data

    fun loadNextPage() {
        when (state) {
            State.INIT,
            State.LOADED_PAGE -> {
                state = State.LOADING
                load(limit = limit)
            }
            else -> Unit
        }
    }

    fun refresh(forceRefresh: Boolean = false) {
        if (forceRefresh || (state != State.LOADING)) {
            load(
                limit = if (data.isEmpty()) limit else data.size.coerceAtLeast(limit),
                isRefresh = true
            )
        }
    }

    private fun load(limit: Int, isRefresh: Boolean = false) {
        loadingJob?.cancel()
        loadingJob = coroutineScope.launch {
            showProgress(
                show = true,
                isRefresh = isRefresh
            )
            showError(false)
            try {
                val offset = if (isRefresh) 0 else data.size
                val newPage = pageRequest(offset, limit)
                if (isRefresh) data.clear()
                data.addAll(newPage)
                state = if (newPage.size < limit) State.LOADED_ALL else State.LOADED_PAGE
                showData()
            } catch (ignored: CancellationException) {
                /* empty */
            } catch (error: Exception) {
                state = State.ERROR
                showError(
                    show = true,
                    error = error
                )
            }
            showProgress(
                show = false,
                isRefresh = isRefresh
            )
        }
    }

    private fun showProgress(show: Boolean, isRefresh: Boolean) =
        viewController.showProgress(
            show = show,
            isRefresh = isRefresh,
            isDataEmpty = data.isEmpty()
        )

    private fun showError(show: Boolean, error: Throwable? = null) =
        viewController.showError(
            show = show,
            error = error,
            isDataEmpty = data.isEmpty()
        )

    private fun showData() = viewController.showData(data)

    interface ViewController<T> {
        fun showData(data: List<T>)
        fun showProgress(show: Boolean, isRefresh: Boolean, isDataEmpty: Boolean)
        fun showError(show: Boolean, error: Throwable?, isDataEmpty: Boolean)
    }

    private enum class State {
        INIT,
        LOADING,
        LOADED_PAGE,
        LOADED_ALL,
        ERROR
    }
}
