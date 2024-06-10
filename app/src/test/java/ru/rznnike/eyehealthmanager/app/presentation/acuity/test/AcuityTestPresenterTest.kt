package ru.rznnike.eyehealthmanager.app.presentation.acuity.test

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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.app.model.test.acuity.EmptyAcuitySymbol
import ru.rznnike.eyehealthmanager.app.model.test.acuity.IAcuitySymbol
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import java.time.Clock

@ExtendWith(MockitoExtension::class)
class AcuityTestPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AcuityTestView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val mockGetAcuityTestingSettingsUseCase: GetAcuityTestingSettingsUseCase by inject()
    private val mockAddTestResultUseCase: AddTestResultUseCase by inject()

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
                single { mock<GetTestingSettingsUseCase>() }
                single { mock<GetAcuityTestingSettingsUseCase>() }
                single { mock<AddTestResultUseCase>() }
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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun onFirstViewAttach_leftEye_leftEyeSelected() {
        onFirstViewAttach_testBody(TestEyesType.LEFT)
    }

    @Test
    fun onFirstViewAttach_rightEye_rightEyeSelected() {
        onFirstViewAttach_testBody(TestEyesType.RIGHT)
    }

    @Test
    fun onFirstViewAttach_bothEyes_leftEyeSelected() {
        onFirstViewAttach_testBody(TestEyesType.BOTH)
    }

    private fun onFirstViewAttach_testBody(eyesType: TestEyesType) = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(
            UseCaseResult(
                AcuityTestingSettings(
                    eyesType = eyesType
                )
            )
        )
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGetTestingSettingsUseCase)()
        verify(mockGetAcuityTestingSettingsUseCase)()
        verify(mockView).showTestProgress(0)
        verify(mockView).showInfo(if (eyesType == TestEyesType.RIGHT) TestEyesType.RIGHT else TestEyesType.LEFT)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onNextStep_info_test() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onNextStep()

        verify(mockView).showTestProgress(0)
        verify(mockView).showTestStep(any(), any(), any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onNextStep_test_answer() = runTest {
        val symbolsType = AcuityTestSymbolsType.LETTERS_EN
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(
            UseCaseResult(
                AcuityTestingSettings(
                    symbolsType = symbolsType
                )
            )
        )
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        presenter.onNextStep()
        clearInvocationsForAll()

        presenter.onNextStep()

        verify(mockView).showTestProgress(0)
        verify(mockView).showAnswerVariants(
            symbolsType = symbolsType,
            selectedSymbol = null
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onNextStep_answer_test() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        presenter.onNextStep()
        presenter.onNextStep()
        presenter.onSymbolSelected(null)
        clearInvocationsForAll()

        presenter.onNextStep()

        verify(mockView).showTestProgress(9)
        verify(mockView).showTestStep(any(), any(), any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onNextStep_twoWrongAnswers_finishTest() = runTest {
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(UseCaseResult(AcuityTestingSettings()))
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        presenter.onNextStep()
        presenter.onNextStep()
        presenter.onSymbolSelected(null)
        clearInvocationsForAll()

        presenter.onNextStep()

        verify(mockView).showTestProgress(9)
        verify(mockView).showTestStep(any(), any(), any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onSymbolSelected_null_populateData() {
        onSymbolSelected_testBody(null)
    }

    @Test
    fun onSymbolSelected_notNull_populateData() {
        onSymbolSelected_testBody(AcuitySymbolLetterEn.C)
    }

    private fun onSymbolSelected_testBody(symbol: IAcuitySymbol?) = runTest {
        val symbolsType = AcuityTestSymbolsType.LETTERS_EN
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(TestingSettings()))
        whenever(mockGetAcuityTestingSettingsUseCase()).doReturn(
            UseCaseResult(
                AcuityTestingSettings(
                    symbolsType = symbolsType
                )
            )
        )
        val dayPart = DayPart.MIDDLE
        val presenter = AcuityTestPresenter(dayPart)
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        presenter.onNextStep()
        presenter.onNextStep()
        clearInvocationsForAll()

        presenter.onSymbolSelected(symbol)

        verify(mockView).showTestProgress(0)
        verify(mockView).showAnswerVariants(
            symbolsType = symbolsType,
            selectedSymbol = symbol ?: EmptyAcuitySymbol
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
            mockGetAcuityTestingSettingsUseCase,
            mockAddTestResultUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}