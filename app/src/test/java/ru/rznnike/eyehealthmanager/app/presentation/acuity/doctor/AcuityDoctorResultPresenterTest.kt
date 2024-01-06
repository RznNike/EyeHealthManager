package ru.rznnike.eyehealthmanager.app.presentation.acuity.doctor

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
import org.mockito.Mock
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.never
import org.mockito.Mockito.only
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

@ExtendWith(MockitoExtension::class)
class AcuityDoctorResultPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AcuityDoctorResultView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockAddTestResultUseCase: AddTestResultUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<AddTestResultUseCase>() }
            }
        )
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
    fun onFirstViewAttach_populateDefaultData() {
        val presenter = AcuityDoctorResultPresenter()

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            date = null,
            leftEye = "",
            rightEye = ""
        )
    }

    @Test
    fun onLeftEyeValueChanged_populateChangedData() {
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        reset(mockView)

        presenter.onLeftEyeValueChanged("123")

        verify(mockView, only()).populateData(
            date = null,
            leftEye = "123",
            rightEye = ""
        )
    }

    @Test
    fun onRightEyeValueChanged_populateChangedData() {
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        reset(mockView)

        presenter.onRightEyeValueChanged("123")

        verify(mockView, only()).populateData(
            date = null,
            leftEye = "",
            rightEye = "123"
        )
    }

    @Test
    fun onDateTimeSelected_populateChangedData() {
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        reset(mockView)

        presenter.onDateTimeSelected(42)

        verify(mockView, only()).populateData(
            date = 42,
            leftEye = "",
            rightEye = ""
        )
    }

    @Test
    fun onAddResult_dateNotSet_message() = runTest {
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        reset(mockView)

        presenter.onAddResult()
        testScheduler.advanceUntilIdle()

        verify(mockNotifier).sendMessage(R.string.choose_date_and_time)
        verify(mockView, never()).setProgress(anyBoolean(), anyBoolean())
        verify(mockView, never()).routerFinishFlow()
        verify(mockAddTestResultUseCase, never()).invoke(any())
        verify(mockErrorHandler, never()).proceed(any(), any())
        verify(mockEventDispatcher, never()).sendEvent(any())
    }

    @Test
    fun onAddResult_bothEyesNotSet_message() = runTest {
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        reset(mockView)

        presenter.onDateTimeSelected(42)
        presenter.onAddResult()
        testScheduler.advanceUntilIdle()

        verify(mockNotifier).sendMessage(R.string.error_enter_at_least_one_eye)
        verify(mockView, never()).setProgress(anyBoolean(), anyBoolean())
        verify(mockView, never()).routerFinishFlow()
        verify(mockAddTestResultUseCase, never()).invoke(any())
        verify(mockErrorHandler, never()).proceed(any(), any())
        verify(mockEventDispatcher, never()).sendEvent(any())
    }

    @Test
    fun onAddResult_oneEyeSet_success() = runTest {
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(1L))
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        presenter.onLeftEyeValueChanged("0.55")
        presenter.onDateTimeSelected(42)
        reset(mockView)

        presenter.onAddResult()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase).invoke(
            argThat<AcuityTestResult> {
                (symbolsType == AcuityTestSymbolsType.LETTERS_RU)
                        && (testEyesType == TestEyesType.LEFT)
                        && (dayPart == DayPart.MIDDLE)
                        && (resultLeftEye == 55)
                        && (resultRightEye == null)
                        && measuredByDoctor
            }
        )
        verify(mockNotifier).sendMessage(R.string.data_added)
        verify(mockView).routerFinishFlow()
        verify(mockErrorHandler, never()).proceed(any(), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractions(mockView, mockNotifier, mockAddTestResultUseCase, mockErrorHandler, mockEventDispatcher)
    }

    @Test
    fun onAddResult_bothEyesSet_success() = runTest {
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(1L))
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        presenter.onLeftEyeValueChanged("0.55")
        presenter.onRightEyeValueChanged("0.66")
        presenter.onDateTimeSelected(42)
        reset(mockView)

        presenter.onAddResult()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase).invoke(
            argThat<AcuityTestResult> {
                (symbolsType == AcuityTestSymbolsType.LETTERS_RU)
                        && (testEyesType == TestEyesType.BOTH)
                        && (dayPart == DayPart.MIDDLE)
                        && (resultLeftEye == 55)
                        && (resultRightEye == 66)
                        && measuredByDoctor
            }
        )
        verify(mockNotifier).sendMessage(R.string.data_added)
        verify(mockView).routerFinishFlow()
        verify(mockErrorHandler, never()).proceed(any(), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractions(mockView, mockNotifier, mockAddTestResultUseCase, mockErrorHandler, mockEventDispatcher)
    }

    @Test
    fun onAddResult_useCaseError_message() = runTest {
        val error = Exception()
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(error = error))
        val presenter = AcuityDoctorResultPresenter()
        presenter.attachView(mockView)
        presenter.onLeftEyeValueChanged("0.55")
        presenter.onRightEyeValueChanged("0.66")
        presenter.onDateTimeSelected(42)
        reset(mockView)

        presenter.onAddResult()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase).invoke(
            argThat<AcuityTestResult> {
                (symbolsType == AcuityTestSymbolsType.LETTERS_RU)
                        && (testEyesType == TestEyesType.BOTH)
                        && (dayPart == DayPart.MIDDLE)
                        && (resultLeftEye == 55)
                        && (resultRightEye == 66)
                        && measuredByDoctor
            }
        )
        verify(mockNotifier, never()).sendMessage(anyInt(), any(), any())
        verify(mockView, never()).routerFinishFlow()
        verify(mockErrorHandler).proceed(eq(error), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractions(mockView, mockNotifier, mockAddTestResultUseCase, mockErrorHandler, mockEventDispatcher)
    }
}