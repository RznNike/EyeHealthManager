package ru.rznnike.eyehealthmanager.app.presentation.main

import android.net.Uri
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
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.utils.createTestCoroutineProvider
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteAllTestResultsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.test.DeleteDuplicatesUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetWelcomeDialogShowedUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetDisplayedChangelogVersionUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetWelcomeDialogShowedUseCase

@ExtendWith(MockitoExtension::class)
class MainPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: MainView

    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockNotifier: Notifier by inject()
    private val mockErrorHandler: ErrorHandler by inject()
    private val mockGetWelcomeDialogShowedUseCase: GetWelcomeDialogShowedUseCase by inject()
    private val mockSetWelcomeDialogShowedUseCase: SetWelcomeDialogShowedUseCase by inject()
    private val mockGetDisplayedChangelogVersionUseCase: GetDisplayedChangelogVersionUseCase by inject()
    private val mockSetDisplayedChangelogVersionUseCase: SetDisplayedChangelogVersionUseCase by inject()
    private val mockDeleteDuplicatesUseCase: DeleteDuplicatesUseCase by inject()
    private val mockDeleteAllTestResultsUseCase: DeleteAllTestResultsUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<EventDispatcher>() }
                single { mock<Notifier>() }
                single { mock<ErrorHandler>() }
                single { mock<GetWelcomeDialogShowedUseCase>() }
                single { mock<SetWelcomeDialogShowedUseCase>() }
                single { mock<GetDisplayedChangelogVersionUseCase>() }
                single { mock<SetDisplayedChangelogVersionUseCase>() }
                single { mock<DeleteDuplicatesUseCase>() }
                single { mock<DeleteAllTestResultsUseCase>() }
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
    fun init_subscribedToEvents() {
        val presenter = MainPresenter()

        verify(mockEventDispatcher).addEventListener(AppEvent.JournalExported::class, presenter)
        verify(mockEventDispatcher).addEventListener(AppEvent.JournalImported::class, presenter)
        verify(mockEventDispatcher).addEventListener(AppEvent.JournalDuplicatesDeletionRequested::class, presenter)
        verify(mockEventDispatcher).addEventListener(AppEvent.JournalTotalDeletionRequested::class, presenter)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onDestroy_unsubscribedFromEvents() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onDestroy()

        verify(mockEventDispatcher).removeEventListener(presenter)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalExported_showDialog() = runTest {
        val mockUri = mock<Uri>()
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalExported(mockUri))

        verify(mockView).showSuccessfulExportDialog(mockUri)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalImported_showDialog() = runTest {
        val mockUri = mock<Uri>()
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalImported(mockUri))

        verify(mockView).showSuccessfulImportDialog()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalDuplicatesDeletionRequestedAndSuccess_alert() = runTest {
        declare {
            createTestCoroutineProvider()
        }
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        whenever(mockDeleteDuplicatesUseCase()).doReturn(UseCaseResult(Unit))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalDuplicatesDeletionRequested)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteDuplicatesUseCase)()
        verify(mockNotifier).sendAlert(R.string.duplicates_successfully_deleted)
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalDuplicatesDeletionRequestedAndException_errorHandler() = runTest {
        declare {
            createTestCoroutineProvider()
        }
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        whenever(mockDeleteDuplicatesUseCase()).doReturn(UseCaseResult(error = Exception()))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalDuplicatesDeletionRequested)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteDuplicatesUseCase)()
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalTotalDeletionRequestedAndSuccess_alert() = runTest {
        declare {
            createTestCoroutineProvider()
        }
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        whenever(mockDeleteAllTestResultsUseCase()).doReturn(UseCaseResult(Unit))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalTotalDeletionRequested)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteAllTestResultsUseCase)()
        verify(mockNotifier).sendAlert(R.string.clear_journal_success)
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_journalTotalDeletionRequestedAndException_errorHandler() = runTest {
        declare {
            createTestCoroutineProvider()
        }
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        whenever(mockDeleteAllTestResultsUseCase()).doReturn(UseCaseResult(error = Exception()))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.JournalTotalDeletionRequested)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockDeleteAllTestResultsUseCase)()
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalChanged)
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onEvent_wrongEvent_nothing() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        val presenter = MainPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onEvent(AppEvent.TestingSettingsChanged)

        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_welcomeNotShowed_showWelcome() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(false))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        whenever(mockSetWelcomeDialogShowedUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = MainPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetWelcomeDialogShowedUseCase)()
        verify(mockGetDisplayedChangelogVersionUseCase)()
        verify(mockView).showWelcomeDialog()
        verify(mockSetWelcomeDialogShowedUseCase)(true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_changelogNotShowedFirst_skipChangelog() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(0))
        val presenter = MainPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetWelcomeDialogShowedUseCase)()
        verify(mockGetDisplayedChangelogVersionUseCase)()
        verify(mockSetDisplayedChangelogVersionUseCase)(any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_changelogNotShowedSecond_showChangelog() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(1))
        val presenter = MainPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetWelcomeDialogShowedUseCase)()
        verify(mockGetDisplayedChangelogVersionUseCase)()
        verify(mockSetDisplayedChangelogVersionUseCase)(any())
        verify(mockView).showChangelogDialog()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_allShowed_nothing() = runTest {
        whenever(mockGetWelcomeDialogShowedUseCase()).doReturn(UseCaseResult(true))
        whenever(mockGetDisplayedChangelogVersionUseCase()).doReturn(UseCaseResult(Int.MAX_VALUE))
        val presenter = MainPresenter()
        clearInvocationsForAll()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetWelcomeDialogShowedUseCase)()
        verify(mockGetDisplayedChangelogVersionUseCase)()
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockEventDispatcher,
            mockNotifier,
            mockErrorHandler,
            mockGetWelcomeDialogShowedUseCase,
            mockSetWelcomeDialogShowedUseCase,
            mockGetDisplayedChangelogVersionUseCase,
            mockSetDisplayedChangelogVersionUseCase,
            mockDeleteDuplicatesUseCase,
            mockDeleteAllTestResultsUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}