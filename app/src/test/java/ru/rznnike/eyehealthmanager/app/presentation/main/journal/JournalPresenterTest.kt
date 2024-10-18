package ru.rznnike.eyehealthmanager.app.presentation.main.journal

import android.net.Uri
import android.os.Parcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.AcuityFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.AnalysisFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.AstigmatismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.ColorPerceptionFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.ContrastFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.DaltonismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.backup.ExportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.restore.ImportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.NearFarFlowFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.GetTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType
import java.time.Clock
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class JournalPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: JournalView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetTestResultsUseCase: GetTestResultsUseCase by inject()
    private val mockDeleteTestResultUseCase: DeleteTestResultUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { Clock.systemUTC() }
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<GetTestResultsUseCase>() }
                single { mock<DeleteTestResultUseCase>() }
            }
        )
    }

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun beforeEach() {
        Dispatchers.setMain(testDispatcher)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_subscribeToEvents() {
        val presenter = JournalPresenter()

        verify(mockEventDispatcher).addEventListener(AppEvent.JournalChanged::class, presenter)
        verify(mockEventDispatcher).addEventListener(AppEvent.JournalImported::class, presenter)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_refresh() = runTest {
        val tests = listOf(AstigmatismTestResult())
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = true
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = eq(tests),
            filter = any()
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDestroy_unsubscribeFromEvents() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(listOf()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDestroy()

        verify(mockEventDispatcher).removeEventListener(presenter)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalChanged_refresh() = onEvent_rightEvent_testBody(AppEvent.JournalChanged)

    @Test
    fun onEvent_journalImported_refresh() = onEvent_rightEvent_testBody(AppEvent.JournalImported(mock<Uri>()))

    private fun onEvent_rightEvent_testBody(event: AppEvent) = runTest {
        val tests = listOf(AstigmatismTestResult())
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(event)
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = false
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = eq(tests),
            filter = any()
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_wrongEvent_nothing() = runTest {
        val tests = listOf(AstigmatismTestResult())
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.TestingSettingsChanged)
        testScheduler.advanceUntilIdle()

        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun loadNext_loadNextPage() = runTest {
        val tests = List(20) { AstigmatismTestResult() }
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.loadNext()
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = false,
            isDataEmpty = false
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = eq(tests + tests),
            filter = any()
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = false,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_acuity_openAcuityFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(AcuityTestResult())

        verify(mockView).routerStartFlow(screenMatcher(AcuityFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_astigmatism_openAstigmatismFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(AstigmatismTestResult())

        verify(mockView).routerStartFlow(screenMatcher(AstigmatismFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_nearFar_openNearFarFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(
            NearFarTestResult(
                resultLeftEye = NearFarAnswerType.EQUAL,
                resultRightEye = NearFarAnswerType.EQUAL
            )
        )

        verify(mockView).routerStartFlow(screenMatcher(NearFarFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_colorPerception_openColorPerceptionFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(
            ColorPerceptionTestResult(
                recognizedColorsCount = 1,
                allColorsCount = 1
            )
        )

        verify(mockView).routerStartFlow(screenMatcher(ColorPerceptionFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_daltonism_openDaltonismFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(
            DaltonismTestResult(
                errorsCount = 0,
                anomalyType = DaltonismAnomalyType.NONE
            )
        )

        verify(mockView).routerStartFlow(screenMatcher(DaltonismFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_contrast_openContrastFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestInfo(
            ContrastTestResult(
                recognizedContrast = 1
            )
        )

        verify(mockView).routerStartFlow(screenMatcher(ContrastFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestInfo_unknown_errorMessage() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        val customResult = object : TestResult() {
            override fun contentEquals(other: TestResult?) = true
            override fun exportToString() = ""
            override fun describeContents() = 0
            override fun writeToParcel(dest: Parcel, flags: Int) = Unit
        }
        presenter.openTestInfo(customResult)

        verify(mockNotifier).sendMessage(R.string.unknown_error)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDeleteTestResult_success_refresh() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        whenever(mockDeleteTestResultUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val testResult = AcuityTestResult(
            id = 42
        )
        clearInvocationsForAll()

        presenter.onDeleteTestResult(testResult)
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = false
        )
        verify(mockDeleteTestResultUseCase)(testResult.id)
        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = true
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = eq(emptyList()),
            filter = any()
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = true
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDeleteTestResult_exception_errorHandler() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(emptyList()))
        whenever(mockDeleteTestResultUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val testResult = AcuityTestResult(
            id = 42
        )
        clearInvocationsForAll()

        presenter.onDeleteTestResult(testResult)
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = false
        )
        verify(mockDeleteTestResultUseCase)(testResult.id)
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterChanged_refresh() = runTest {
        val tests = listOf(AstigmatismTestResult())
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val filter = TestResultFilter()
        clearInvocationsForAll()

        presenter.onFilterChanged(filter)
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = false
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = tests,
            filter = filter
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun clearFilter_refresh() = runTest {
        val tests = listOf(AstigmatismTestResult())
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(tests))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.clearFilter()
        testScheduler.advanceUntilIdle()

        verify(mockView).showProgress(
            show = true,
            isRefresh = true,
            isDataEmpty = false
        )
        verify(mockView).showErrorView(
            show = false,
            message = null
        )
        verify(mockView).populateData(
            data = eq(tests),
            filter = any()
        )
        verify(mockView).showProgress(
            show = false,
            isRefresh = true,
            isDataEmpty = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun exportData_openExportJournalScreen() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(listOf()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.exportData()

        verify(mockView).routerNavigateTo(screenMatcher(ExportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importData_openImportJournalScreen() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(listOf()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.importData()

        verify(mockView).routerNavigateTo(screenMatcher(ImportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun analyseData_openAnalysisFlow() = runTest {
        whenever(mockGetTestResultsUseCase(any())).doReturn(UseCaseResult(listOf()))
        val presenter = JournalPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.analyseData()

        verify(mockView).routerStartFlow(screenMatcher(AnalysisFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockErrorHandler,
            mockNotifier,
            mockEventDispatcher,
            mockGetTestResultsUseCase,
            mockDeleteTestResultUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}