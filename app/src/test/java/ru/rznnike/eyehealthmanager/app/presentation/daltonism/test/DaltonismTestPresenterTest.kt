package ru.rznnike.eyehealthmanager.app.presentation.daltonism.test

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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.result.DaltonismResultFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.app.model.test.daltonism.DaltonismTestData
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class DaltonismTestPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: DaltonismTestView

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
    fun onFirstViewAttach_populateData() {
        var variants: List<Int> = emptyList()
        whenever(mockView.populateData(any(), any(), any())).doAnswer {
            variants = it.arguments.filterIsInstance<List<Int>>().first()
        }
        val presenter = DaltonismTestPresenter()

        presenter.attachView(mockView)

        verify(mockView).populateData(
            imageResId = DaltonismTestData.questions[0].testImageResId,
            variants = variants,
            progress = 0
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onAnswer_oneAnswer_nextStep() {
        var variants: List<Int> = emptyList()
        whenever(mockView.populateData(any(), any(), any())).doAnswer {
            variants = it.arguments.filterIsInstance<List<Int>>().first()
        }
        val presenter = DaltonismTestPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.answer(0)

        verify(mockView).populateData(
            imageResId = DaltonismTestData.questions[1].testImageResId,
            variants = variants,
            progress = 3
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onAnswer_lastAnswerAndSuccess_finishTest() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-17T05:00:00Z"), ZoneOffset.UTC
            )
        }
        var variants: List<Int> = emptyList()
        whenever(mockView.populateData(any(), any(), any())).doAnswer {
            variants = it.arguments.filterIsInstance<List<Int>>().first()
        }
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(42L))
        val presenter = DaltonismTestPresenter()
        presenter.attachView(mockView)
        DaltonismTestData.questions.subList(0, DaltonismTestData.questions.lastIndex).forEach { question ->
            val correctAnswerIndex = variants.indexOf(question.answerResIds.first())
            presenter.answer(correctAnswerIndex)
        }
        clearInvocationsForAll()

        val correctAnswerIndex = variants.indexOf(
            DaltonismTestData.questions.last().answerResIds.first()
        )
        presenter.answer(correctAnswerIndex)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase)(
            argThat {
                (this is DaltonismTestResult)
                        && (timestamp == 1705467600000L)
                        && (errorsCount == 0)
                        && (anomalyType == DaltonismAnomalyType.NONE)
            }
        )
        verify(mockView).routerNewRootScreen(
            screenMatcher(DaltonismResultFragment::class) { arguments ->
                (arguments[DaltonismResultFragment.ERRORS_COUNT] == 0)
                        && (arguments[DaltonismResultFragment.RESULT_TYPE] == DaltonismAnomalyType.NONE)
            }
        )
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onAnswer_lastAnswerAndException_errorHandler() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-17T05:00:00Z"), ZoneOffset.UTC
            )
        }
        var variants: List<Int> = emptyList()
        whenever(mockView.populateData(any(), any(), any())).doAnswer {
            variants = it.arguments.filterIsInstance<List<Int>>().first()
        }
        whenever(mockAddTestResultUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = DaltonismTestPresenter()
        presenter.attachView(mockView)
        DaltonismTestData.questions.subList(0, DaltonismTestData.questions.lastIndex).forEach { question ->
            val correctAnswerIndex = variants.indexOf(question.answerResIds.first())
            presenter.answer(correctAnswerIndex)
        }
        clearInvocationsForAll()

        val correctAnswerIndex = variants.indexOf(
            DaltonismTestData.questions.last().answerResIds.first()
        )
        presenter.answer(correctAnswerIndex)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockAddTestResultUseCase)(
            argThat {
                (this is DaltonismTestResult)
                        && (timestamp == 1705467600000L)
                        && (errorsCount == 0)
                        && (anomalyType == DaltonismAnomalyType.NONE)
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