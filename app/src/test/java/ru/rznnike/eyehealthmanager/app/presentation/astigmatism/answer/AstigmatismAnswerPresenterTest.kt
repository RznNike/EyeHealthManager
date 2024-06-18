package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.answer

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
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.result.AstigmatismResultFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismAnswerType
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class AstigmatismAnswerPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AstigmatismAnswerView

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
                single { Clock.systemUTC() }
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun onSaveAnswer_success_openAstigmatismResultScreen() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-14T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(42))
        val presenter = AstigmatismAnswerPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.onSaveAnswer(answerLeftEye = 0, answerRightEye = 1)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase)(
            argThat {
                (this is AstigmatismTestResult)
                        && (timestamp == 1705208400000L)
                        && (resultLeftEye == AstigmatismAnswerType.OK)
                        && (resultRightEye == AstigmatismAnswerType.ANOMALY)
            }
        )
        verify(mockView).routerNewRootScreen(
            screenMatcher(AstigmatismResultFragment::class) { arguments ->
                (arguments[AstigmatismResultFragment.ANSWER_LEFT_EYE] == AstigmatismAnswerType.OK)
                        && (arguments[AstigmatismResultFragment.ANSWER_RIGHT_EYE] == AstigmatismAnswerType.ANOMALY)
            }
        )
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onSaveAnswer_exception_errorHandler() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-14T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = AstigmatismAnswerPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.onSaveAnswer(answerLeftEye = 0, answerRightEye = 1)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase)(
            argThat {
                (this is AstigmatismTestResult)
                        && (timestamp == 1705208400000L)
                        && (resultLeftEye == AstigmatismAnswerType.OK)
                        && (resultRightEye == AstigmatismAnswerType.ANOMALY)
            }
        )
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
            mockAddTestResultUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}