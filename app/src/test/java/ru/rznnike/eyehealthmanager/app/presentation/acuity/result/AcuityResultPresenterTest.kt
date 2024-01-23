package ru.rznnike.eyehealthmanager.app.presentation.acuity.result

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
import org.koin.test.mock.declare
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.test.AcuityTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.SingleEyeAnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class AcuityResultPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AcuityResultView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetApplyDynamicCorrectionsUseCase: GetApplyDynamicCorrectionsUseCase by inject()
    private val mockGetAnalysisResultUseCase: GetAnalysisResultUseCase by inject()
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
                single { mock<GetApplyDynamicCorrectionsUseCase>() }
                single { mock<GetAnalysisResultUseCase>() }
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
    fun onFirstViewAttach_withAnalysis_populateData() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-14T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        val analysisResult = AnalysisResult(
            testResults = emptyList(),
            leftEyeAnalysisResult = SingleEyeAnalysisResult(
                chartData = emptyList(),
                extrapolatedResult = null,
                statistics = null,
                dynamicCorrections = null
            ),
            rightEyeAnalysisResult = SingleEyeAnalysisResult(
                chartData = emptyList(),
                extrapolatedResult = null,
                statistics = null,
                dynamicCorrections = null
            ),
            showWarningAboutVision = true,
            lastResultRecognizedAsNoise = false
        )
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(analysisResult))
        val testResult = AcuityTestResult()
        val presenter = AcuityResultPresenter(testResult)

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetApplyDynamicCorrectionsUseCase)()
        verify(mockGetAnalysisResultUseCase)(
            argThat { parameters ->
                (parameters.dateFrom == 1702512000000L)
                        && (parameters.dateTo == 1705276799999L)
                        && (parameters.applyDynamicCorrections == applyDynamicCorrections)
                        && (parameters.analysisType == AnalysisType.ACUITY_ONLY)
            }
        )
        verify(mockView).populateData(
            testResult = testResult,
            analysisResult = analysisResult,
            applyDynamicCorrections = applyDynamicCorrections
        )
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_exception_handled() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-14T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = NotEnoughDataException()))
        val testResult = AcuityTestResult()
        val presenter = AcuityResultPresenter(testResult)

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetApplyDynamicCorrectionsUseCase)()
        verify(mockGetAnalysisResultUseCase)(
            argThat { parameters ->
                (parameters.dateFrom == 1702512000000L)
                        && (parameters.dateTo == 1705276799999L)
                        && (parameters.applyDynamicCorrections == applyDynamicCorrections)
                        && (parameters.analysisType == AnalysisType.ACUITY_ONLY)
            }
        )
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).populateData(
            testResult = testResult,
            analysisResult = null,
            applyDynamicCorrections = applyDynamicCorrections
        )
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onApplyDynamicCorrectionsChanged_populateData() = runTest {
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = NotEnoughDataException()))
        val testResult = AcuityTestResult()
        val presenter = AcuityResultPresenter(testResult)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onApplyDynamicCorrectionsChanged(false)

        verify(mockView).populateData(
            testResult = testResult,
            analysisResult = null,
            applyDynamicCorrections = false
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun redoTest_deleted_openAcuityTestScreen() = runTest {
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = NotEnoughDataException()))
        whenever(mockDeleteTestResultUseCase(any())).doReturn(UseCaseResult(Unit))
        val testResult = AcuityTestResult(
            id = 42,
            dayPart = DayPart.END
        )
        val presenter = AcuityResultPresenter(testResult)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.redoTest()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteTestResultUseCase)(testResult.id)
        verify(mockView).routerNewRootScreen(
            screenMatcher(AcuityTestFragment::class) { arguments ->
                arguments[AcuityTestFragment.DAY_PART] == testResult.dayPart
            }
        )
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun redoTest_exception_message() = runTest {
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = NotEnoughDataException()))
        whenever(mockDeleteTestResultUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val testResult = AcuityTestResult(
            id = 42,
            dayPart = DayPart.END
        )
        val presenter = AcuityResultPresenter(testResult)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.redoTest()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteTestResultUseCase)(testResult.id)
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockErrorHandler,
            mockNotifier,
            mockEventDispatcher,
            mockGetApplyDynamicCorrectionsUseCase,
            mockGetAnalysisResultUseCase,
            mockDeleteTestResultUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}