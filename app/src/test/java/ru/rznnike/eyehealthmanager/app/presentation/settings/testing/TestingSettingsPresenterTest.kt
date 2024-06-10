package ru.rznnike.eyehealthmanager.app.presentation.settings.testing

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
import ru.rznnike.eyehealthmanager.app.utils.createTestCoroutineProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import java.util.TimeZone
import kotlin.math.abs

@ExtendWith(MockitoExtension::class)
class TestingSettingsPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: TestingSettingsView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val mockSetTestingSettingsUseCase: SetTestingSettingsUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<GetTestingSettingsUseCase>() }
                single { mock<SetTestingSettingsUseCase>() }
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
    fun onFirstViewAttach_loadDataAndSuccess_populateData() = runTest {
        val testingSettings = TestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings))
        val presenter = TestingSettingsPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetTestingSettingsUseCase)()
        verify(mockView).populateData(testingSettings)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_loadDataAndException_closeScreen() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(error = Exception()))
        val presenter = TestingSettingsPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetTestingSettingsUseCase)()
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).routerExit()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onPause_saveChanges() = runTest {
        declare {
            createTestCoroutineProvider()
        }
        val testingSettings = TestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings))
        whenever(mockSetTestingSettingsUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onPause()
        testScheduler.advanceUntilIdle()

        verify(mockSetTestingSettingsUseCase)(testingSettings)
        verify(mockEventDispatcher).sendEvent(AppEvent.TestingSettingsChanged)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onCheckBoxReplaceBeginningClicked_populateData() = runTest {
        val testingSettings = TestingSettings(
            replaceBeginningWithMorning = false
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onCheckBoxReplaceBeginningClicked(true)

        verify(mockView).populateData(
            argThat {
                replaceBeginningWithMorning
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onCheckBoxAutoDayPartSelectionClicked_populateData() = runTest {
        val testingSettings = TestingSettings(
            enableAutoDayPart = false
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onCheckBoxAutoDayPartSelectionClicked(true)

        verify(mockView).populateData(
            argThat {
                enableAutoDayPart
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayBeginningValueChanged_correctOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 60_000L
        clearInvocationsForAll()

        presenter.onTimeToDayBeginningValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == newValue)
                        && (timeToDayMiddle == testingSettings.timeToDayMiddle)
                        && (timeToDayEnd == testingSettings.timeToDayEnd)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayBeginningValueChanged_messedOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 64_800_000L
        clearInvocationsForAll()

        presenter.onTimeToDayBeginningValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == newValue)
                        && (timeToDayMiddle == newValue + GlobalConstants.MINUTE_MS)
                        && (timeToDayEnd == newValue + 2 * GlobalConstants.MINUTE_MS)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayMiddleValueChanged_correctOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 45_200_000L
        clearInvocationsForAll()

        presenter.onTimeToDayMiddleValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == testingSettings.timeToDayBeginning)
                        && (timeToDayMiddle == newValue)
                        && (timeToDayEnd == testingSettings.timeToDayEnd)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayMiddleValueChanged_messedOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 21_600_000L
        clearInvocationsForAll()

        presenter.onTimeToDayMiddleValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == newValue + 2 * GlobalConstants.MINUTE_MS)
                        && (timeToDayMiddle == newValue)
                        && (timeToDayEnd == newValue + GlobalConstants.MINUTE_MS)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayEndValueChanged_correctOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 66_800_000L
        clearInvocationsForAll()

        presenter.onTimeToDayEndValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == testingSettings.timeToDayBeginning)
                        && (timeToDayMiddle == testingSettings.timeToDayMiddle)
                        && (timeToDayEnd == newValue)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onTimeToDayEndValueChanged_messedOrder_populateData() = runTest {
        val testingSettings = TestingSettings(
            timeToDayBeginning = 21_600_000L,
            timeToDayMiddle = 43_200_000L,
            timeToDayEnd = 64_800_000L
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val newValue = 43_200_000L
        clearInvocationsForAll()

        presenter.onTimeToDayEndValueChanged(newValue)

        verify(mockView).populateData(
            argThat {
                (timeToDayBeginning == newValue + GlobalConstants.MINUTE_MS)
                        && (timeToDayMiddle == newValue + 2 * GlobalConstants.MINUTE_MS)
                        && (timeToDayEnd == newValue)
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onArmsLengthValueChanged_populateData() = runTest {
        val testingSettings = TestingSettings(
            armsLength = 420
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onArmsLengthValueChanged("24")

        verify(mockView).populateData(
            argThat {
                armsLength == 240
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onBodyHeightValueChanged_populateData() = runTest {
        val testingSettings = TestingSettings(
            armsLength = 420
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onBodyHeightValueChanged("200")

        verify(mockView).populateData(
            argThat {
                armsLength == 900
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onScalingLineLengthValueChanged_populateData() = runTest {
        val testingSettings = TestingSettings(
            dpmm = 42f
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onScalingLineLengthValueChanged(
            value = "10",
            lineWidthPx = 300
        )

        verify(mockView).populateData(
            argThat {
                abs(dpmm - 3f) < 1e-4
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun resetScale_populateData() = runTest {
        val testingSettings = TestingSettings(
            dpmm = 42f
        )
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(testingSettings.copy()))
        val presenter = TestingSettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.resetScale()

        verify(mockView).populateData(
            argThat {
                abs(dpmm + 1f) < 1e-4
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockErrorHandler,
            mockNotifier,
            mockEventDispatcher,
            mockGetTestingSettingsUseCase,
            mockSetTestingSettingsUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}