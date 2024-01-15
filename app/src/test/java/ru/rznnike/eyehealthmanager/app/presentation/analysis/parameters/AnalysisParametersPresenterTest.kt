package ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters

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
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.result.AnalysisResultFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.analysis.GetAnalysisResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetApplyDynamicCorrectionsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.SingleEyeAnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.model.exception.NotEnoughDataException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class AnalysisParametersPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AnalysisParametersView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockGetApplyDynamicCorrectionsUseCase: GetApplyDynamicCorrectionsUseCase by inject()
    private val mockSetApplyDynamicCorrectionsUseCase: SetApplyDynamicCorrectionsUseCase by inject()
    private val mockGetAnalysisResultUseCase: GetAnalysisResultUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { Clock.systemUTC() }
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<GetApplyDynamicCorrectionsUseCase>() }
                single { mock<SetApplyDynamicCorrectionsUseCase>() }
                single { mock<GetAnalysisResultUseCase>() }
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
    fun onFirstViewAttach_initData() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateFromValueChanged_normal_success() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateFromValueChanged(1697687200000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697673600000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateFromValueChanged_tooFar_fixed() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateFromValueChanged(1696587200000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1696550400000L)
                        && (dateTo == 1704326399999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateFromValueChanged_tooClose_fixed() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateFromValueChanged(1705103100000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1705017600000L)
                        && (dateTo == 1705535999999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateToValueChanged_normal_success() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateToValueChanged(1705104000000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705190399999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateToValueChanged_tooFar_fixed() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateToValueChanged(1705708800000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1698019200000L)
                        && (dateTo == 1705795199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDateToValueChanged_tooClose_fixed() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDateToValueChanged(1697760000000L)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697328000000L)
                        && (dateTo == 1697846399999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onAnalysisTypeValueChanged_success() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val analysisType = AnalysisType.ACUITY_ONLY
        clearInvocationsForAll()

        presenter.onAnalysisTypeValueChanged(analysisType)

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (this.analysisType == analysisType)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onApplyDynamicCorrectionsChanged_success() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        whenever(mockSetApplyDynamicCorrectionsUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onApplyDynamicCorrectionsChanged(!applyDynamicCorrections)
        testScheduler.advanceUntilIdle()

        verify(mockView).populateData(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == !applyDynamicCorrections)
            }
        )
        verify(mockSetApplyDynamicCorrectionsUseCase)(!applyDynamicCorrections)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startAnalysis_success_openAnalysisResultScreen() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
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
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.startAnalysis()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetAnalysisResultUseCase)(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verify(mockView).routerNewRootScreen(
            screenMatcher(AnalysisResultFragment::class) { arguments ->
                arguments[AnalysisResultFragment.RESULT] == analysisResult
            }
        )
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startAnalysis_notEnoughDataException_finishFlow() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = NotEnoughDataException()))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.startAnalysis()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetAnalysisResultUseCase)(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verify(mockView).showNotEnoughDataMessage()
        verify(mockView).routerFinishFlow()
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startAnalysis_otherException_finishFlow() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-15T05:00:00Z"), ZoneOffset.UTC
            )
        }
        val applyDynamicCorrections = true
        whenever(mockGetApplyDynamicCorrectionsUseCase()).doReturn(UseCaseResult(applyDynamicCorrections))
        whenever(mockGetAnalysisResultUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = AnalysisParametersPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.startAnalysis()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetAnalysisResultUseCase)(
            argThat {
                (dateFrom == 1697587200000L)
                        && (dateTo == 1705363199999L)
                        && (analysisType == AnalysisType.CONSOLIDATED_REPORT)
                        && (this.applyDynamicCorrections == applyDynamicCorrections)
            }
        )
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).routerFinishFlow()
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockErrorHandler,
            mockNotifier,
            mockGetApplyDynamicCorrectionsUseCase,
            mockSetApplyDynamicCorrectionsUseCase,
            mockGetAnalysisResultUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}