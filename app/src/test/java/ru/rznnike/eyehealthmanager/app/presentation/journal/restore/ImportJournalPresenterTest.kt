package ru.rznnike.eyehealthmanager.app.presentation.journal.restore

import android.content.Context
import android.net.Uri
import com.github.terrakok.cicerone.androidx.ActivityScreen
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
import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.utils.JournalBackupManagerAndroid
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.ImportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

@ExtendWith(MockitoExtension::class)
class ImportJournalPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: ImportJournalView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockJournalBackupManagerAndroid: JournalBackupManagerAndroid by inject()
    private val mockImportJournalUseCase: ImportJournalUseCase by inject()
    private val mockContext: Context by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<JournalBackupManagerAndroid>() }
                single { mock<ImportJournalUseCase>() }
                single { mock<Context>() }
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
    fun onFirstViewAttach_populateData() {
        val presenter = ImportJournalPresenter()

        presenter.attachView(mockView)

        verify(mockView).populateData(
            availableImportTypes = emptyList(),
            folderPath = null
        )
    }

    @Test
    fun onFolderSelected_success_checkAvailableData() = runTest {
        val availableTypes = listOf(TestType.ACUITY)
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(availableTypes))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        clearInvocationsForAll()

        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(true)
        verify(mockGetAvailableImportTypesUseCase)(mockUri)
        verify(mockView).populateData(
            availableImportTypes = availableTypes,
            folderPath = url
        )
        verify(mockView).setProgress(false)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFolderSelected_startImportAutomatically_importFiles() = runTest {
        val availableTypes = listOf(TestType.ACUITY)
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(availableTypes))
        whenever(mockImportJournalUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        presenter.importFiles()
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()

        verify(mockView, times(2)).setProgress(true)
        verify(mockGetAvailableImportTypesUseCase)(mockUri)
        verify(mockView).populateData(
            availableImportTypes = availableTypes,
            folderPath = url
        )
        verify(mockView, times(2)).setProgress(false)
        // import part
        verify(mockImportJournalUseCase)(mockUri)
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalImported(mockUri))
        verify(mockView).routerExit()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFolderSelected_exception_errorHandler() = runTest {
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val mockUri = mock<Uri>()
        clearInvocationsForAll()

        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(true)
        verify(mockGetAvailableImportTypesUseCase)(mockUri)
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).setProgress(false)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importFiles_folderNotSelected_requestSelection() = runTest {
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.importFiles()
        testScheduler.advanceUntilIdle()

        verify(mockView).selectImportFolder()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importFiles_noData_showMessage() = runTest {
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.importFiles()
        testScheduler.advanceUntilIdle()

        verify(mockNotifier).sendMessage(R.string.error_no_backup_in_folder)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importFiles_withDataAndSuccess_closeScreen() = runTest {
        val availableTypes = listOf(TestType.ACUITY)
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(availableTypes))
        whenever(mockImportJournalUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.importFiles()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(true)
        verify(mockImportJournalUseCase)(mockUri)
        verify(mockEventDispatcher).sendEvent(AppEvent.JournalImported(mockUri))
        verify(mockView).routerExit()
        verify(mockView).setProgress(false)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importFiles_withDataAndException_errorHandler() = runTest {
        val availableTypes = listOf(TestType.ACUITY)
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(availableTypes))
        whenever(mockImportJournalUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.importFiles()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(true)
        verify(mockImportJournalUseCase)(mockUri)
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).setProgress(false)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openImportFolder_folderNotSelected_nothing() = runTest {
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.openImportFolder()

        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openImportFolder_folderSelected_openFolderFlow() = runTest {
        whenever(mockGetAvailableImportTypesUseCase(any())).doReturn(UseCaseResult(emptyList()))
        val presenter = ImportJournalPresenter()
        presenter.attachView(mockView)
        val url = "test"
        val mockUri = mock<Uri> {
            on { lastPathSegment } doReturn url
        }
        presenter.onFolderSelected(mockUri)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openImportFolder()

        verify(mockView).routerStartFlow(
            argThat {
                (this is ActivityScreen)
                        && (screenKey == "actionOpenFolder")
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
            mockJournalBackupManagerAndroid,
            mockImportJournalUseCase,
            mockContext
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}