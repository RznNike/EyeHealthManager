package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

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
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.doctor.AcuityDoctorResultFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.instruction.AcuityInstructionFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.settings.testing.TestingSettingsFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class AcuityInfoPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AcuityInfoView

    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val mockGetAcuityTestingSettingsUseCase: GetAcuityTestingSettingsUseCase by inject()
    private val mockSetAcuityTestingSettingsUseCase: SetAcuityTestingSettingsUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { Clock.systemUTC() }
                single { mock<EventDispatcher>() }
                single { mock<GetTestingSettingsUseCase>() }
                single { mock<GetAcuityTestingSettingsUseCase>() }
                single { mock<SetAcuityTestingSettingsUseCase>() }
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
    fun init_subscribedToEvents() {
        val presenter = AcuityInfoPresenter()

        verify(mockEventDispatcher, only()).addEventListener(AppEvent.TestingSettingsChanged::class, presenter)
    }

    @Test
    fun onDestroy_unsubscribedFromEvents() {
        val presenter = AcuityInfoPresenter()
        clearInvocationsForAll()

        presenter.onDestroy()

        verify(mockEventDispatcher, only()).removeEventListener(presenter)
    }

    @Test
    fun onEvent_testingSettingsChanged_loadData() = runTest {
        val acuityTestingSettings = AcuityTestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(acuityTestingSettings))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.TestingSettingsChanged)
        testScheduler.advanceUntilIdle()

        verify(mockGetTestingSettingsUseCase)()
        verify(mockGetAcuityTestingSettingsUseCase)()
        verify(mockView).populateData(acuityTestingSettings)
        verifyNoMoreInteractions(
            mockView,
            mockGetTestingSettingsUseCase,
            mockGetAcuityTestingSettingsUseCase,
            mockSetAcuityTestingSettingsUseCase
        )
    }

    @Test
    fun onEvent_wrongEvent_nothing() = runTest {
        val acuityTestingSettings = AcuityTestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(acuityTestingSettings))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalChanged)
        testScheduler.advanceUntilIdle()

        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_loadData() = runTest {
        val acuityTestingSettings = AcuityTestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(acuityTestingSettings))
        val presenter = AcuityInfoPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetTestingSettingsUseCase)()
        verify(mockGetAcuityTestingSettingsUseCase)()
        verify(mockView).populateData(acuityTestingSettings)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onSymbolsTypeSelected_success() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onSymbolsTypeSelected(AcuityTestSymbolsType.LETTERS_EN)
        testScheduler.advanceUntilIdle()

        verify(mockView).populateData(
            argThat { acuityTestingSettings ->
                acuityTestingSettings.symbolsType == AcuityTestSymbolsType.LETTERS_EN
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEyesTypeSelected_success() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEyesTypeSelected(TestEyesType.RIGHT)
        testScheduler.advanceUntilIdle()

        verify(mockView).populateData(
            argThat { acuityTestingSettings ->
                acuityTestingSettings.eyesType == TestEyesType.RIGHT
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onScaleSettings_openTestingSettingsScreen() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onScaleSettings()

        verify(mockView).routerNavigateTo(screenMatcher(TestingSettingsFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDayPartAutoSelectionSettings_openTestingSettingsScreen() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDayPartAutoSelectionSettings()

        verify(mockView).routerNavigateTo(screenMatcher(TestingSettingsFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startTest_saveSettingsAndOpenNextScreen() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val dayPart = DayPart.END
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.startTest(dayPart)
        testScheduler.advanceUntilIdle()

        verify(mockSetAcuityTestingSettingsUseCase)(any())
        verify(mockView).routerNavigateTo(
            screenMatcher(AcuityInstructionFragment::class) { arguments ->
                arguments[AcuityInstructionFragment.DAY_PART] == dayPart
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_beforeBeginning() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:00:05Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.END)
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_beginning() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:00:10Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.BEGINNING)
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_beforeMiddle() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:00:20Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.BEGINNING)
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_middle() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:01:00Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.MIDDLE)
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_beforeEnd() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:01:10Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.MIDDLE)
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_end() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-10T00:02:00Z"), ZoneOffset.UTC
            )
        }
        onPrepareToStartTest_autoDayPart_testBody(DayPart.END)
    }

    private fun onPrepareToStartTest_autoDayPart_testBody(part: DayPart) = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(
            UseCaseResult(
                TestingSettings(
                    enableAutoDayPart = true,
                    timeToDayBeginning = 10_000,
                    timeToDayMiddle = 60_000,
                    timeToDayEnd = 120_000
                )
            )
        )
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onPrepareToStartTest()
        testScheduler.advanceUntilIdle()

        verify(mockSetAcuityTestingSettingsUseCase)(any())
        verify(mockView).routerNavigateTo(
            screenMatcher(AcuityInstructionFragment::class) { arguments ->
                arguments[AcuityInstructionFragment.DAY_PART] == part
            }
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onPrepareToStartTest_manualDayPart_showSelection() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(
            UseCaseResult(
                TestingSettings(
                    enableAutoDayPart = false,
                    replaceBeginningWithMorning = true
                )
            )
        )
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onPrepareToStartTest()
        testScheduler.advanceUntilIdle()

        verify(mockView).showDayPartSelectionDialog(true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onAddDoctorResult_openAcuityDoctorResultScreen() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val presenter = AcuityInfoPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onAddDoctorResult()

        verify(mockView).routerNavigateTo(screenMatcher(AcuityDoctorResultFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockEventDispatcher,
            mockGetTestingSettingsUseCase,
            mockGetAcuityTestingSettingsUseCase,
            mockSetAcuityTestingSettingsUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}