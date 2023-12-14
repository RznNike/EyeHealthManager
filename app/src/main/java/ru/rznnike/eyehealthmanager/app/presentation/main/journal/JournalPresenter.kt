package ru.rznnike.eyehealthmanager.app.presentation.main.journal

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.pagination.Paginator
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.GetTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.getTodayCalendar
import java.util.*

@InjectViewState
class JournalPresenter : BasePresenter<JournalView>(), EventDispatcher.EventListener {
    private val eventDispatcher: EventDispatcher by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val getTestResultsUseCase: GetTestResultsUseCase by inject()
    private val deleteTestResultUseCase: DeleteTestResultUseCase by inject()

    private lateinit var paginator: Paginator<TestResult>

    private var filterParams = TestResultFilterParams()

    init {
        subscribeToEvents()
        initPaginator()
        setDefaultFilter()
    }

    private fun initPaginator() {
        val viewController = object : Paginator.ViewController<TestResult> {
            override fun showData(data: List<TestResult>) {
                viewState.populateData(data, filterParams)
            }

            override fun showProgress(show: Boolean, isRefresh: Boolean, isDataEmpty: Boolean) =
                viewState.showProgress(show, isRefresh, isDataEmpty)

            override fun showError(show: Boolean, error: Throwable?, isDataEmpty: Boolean) {
                if (show) {
                    var errorMessage: String? = null
                    error?.let {
                        errorHandler.proceed(error) {
                            errorMessage = it
                        }
                    }
                    if (isDataEmpty) {
                        viewState.showErrorView(
                            show = true,
                            message = errorMessage
                        )
                    } else {
                        errorMessage?.let {
                            notifier.sendMessage(it)
                        } ?: run {
                            notifier.sendMessage(R.string.unknown_error)
                        }
                    }
                } else {
                    viewState.showErrorView(false)
                }
            }
        }
        paginator = Paginator(
            coroutineScope = presenterScope,
            pageRequest = this::loadNext,
            viewController = viewController
        )
    }

    override fun onDestroy() {
        eventDispatcher.removeEventListener(this)
        super.onDestroy()
    }

    private fun subscribeToEvents() {
        eventDispatcher.addEventListener(AppEvent.JournalChanged::class, this)
        eventDispatcher.addEventListener(AppEvent.JournalImported::class, this)
    }

    override fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.JournalChanged,
            is AppEvent.JournalImported -> refresh()
            else -> Unit
        }
    }

    override fun onFirstViewAttach() {
        refresh()
    }

    private suspend fun loadNext(offset: Int, limit: Int): List<TestResult> {
        val result = getTestResultsUseCase(
            TestResultPagingParams(
                limit = limit,
                offset = offset,
                filterParams = filterParams
            )
        )
        return result.data ?: throw result.error ?: Exception()
    }

    fun loadNext() = paginator.loadNextPage()

    private fun refresh() = paginator.refresh(forceRefresh = true)

    fun openTestInfo(testResult: TestResult) {
        when (testResult) {
            is AcuityTestResult -> viewState.routerStartFlow(Screens.Flow.acuity())
            is AstigmatismTestResult -> viewState.routerStartFlow(Screens.Flow.astigmatism())
            is NearFarTestResult -> viewState.routerStartFlow(Screens.Flow.nearFar())
            is ColorPerceptionTestResult -> viewState.routerStartFlow(Screens.Flow.colorPerception())
            is DaltonismTestResult -> viewState.routerStartFlow(Screens.Flow.daltonism())
            is ContrastTestResult -> viewState.routerStartFlow(Screens.Flow.contrast())
            else -> notifier.sendMessage(R.string.unknown_error)
        }
    }

    fun onDeleteTestResult(testResult: TestResult) {
        presenterScope.launch {
            viewState.showProgress(show = true, isRefresh = true, isDataEmpty = false)
            deleteTestResultUseCase(testResult.id).process(
                {
                    refresh()
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                    viewState.showProgress(show = false, isRefresh = true, isDataEmpty = false)
                }
            )
        }
    }

    fun onFilterChanged(newFilterParams: TestResultFilterParams) {
        filterParams = newFilterParams
        refresh()
    }

    fun clearFilter() {
        setDefaultFilter()
        refresh()
    }

    private fun setDefaultFilter() {
        filterParams = TestResultFilterParams(
            dateFrom = getTodayCalendar().apply {
                add(Calendar.MONTH, -1)
            }.timeInMillis,
            dateTo = Calendar.getInstance().atEndOfDay().timeInMillis
        )
    }

    fun exportData() = viewState.routerNavigateTo(Screens.Screen.exportJournal())

    fun importData() = viewState.routerNavigateTo(Screens.Screen.importJournal())

    fun analyseData() = viewState.routerStartFlow(Screens.Flow.analysis())
}
