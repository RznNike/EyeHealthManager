package ru.rznnike.eyehealthmanager.app.presentation.main.settings

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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.AnalysisFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.backup.ExportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.journal.restore.ImportJournalFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.settings.testing.TestingSettingsFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.dev.GenerateDataUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAppThemeUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAppThemeUseCase
import ru.rznnike.eyehealthmanager.domain.model.common.AppTheme
import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType

@ExtendWith(MockitoExtension::class)
class SettingsPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: SettingsView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetAppThemeUseCase: GetAppThemeUseCase by inject()
    private val mockSetAppThemeUseCase: SetAppThemeUseCase by inject()
    private val mockGenerateDataUseCase: GenerateDataUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<GetAppThemeUseCase>() }
                single { mock<SetAppThemeUseCase>() }
                single { mock<GenerateDataUseCase>() }
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
    fun onFirstViewAttach_success_populateData() = runTest {
        val theme = AppTheme.SYSTEM
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(theme))
        val presenter = SettingsPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetAppThemeUseCase)()
        verify(mockView).populateData(
            language = any(),
            theme = eq(theme)
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFirstViewAttach_exception_errorHandler() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(error = Exception()))
        val presenter = SettingsPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetAppThemeUseCase)()
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockView).populateData(
            language = any(),
            theme = eq(AppTheme.SYSTEM)
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onResume_populateData() = runTest {
        val theme = AppTheme.SYSTEM
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(theme))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.onResume()

        verify(mockView).populateData(
            language = any(),
            theme = eq(theme)
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun changeTheme_success_populateData() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        whenever(mockSetAppThemeUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val theme = AppTheme.DARK
        clearInvocationsForAll()

        presenter.changeTheme(theme)
        testScheduler.advanceUntilIdle()

        verify(mockSetAppThemeUseCase)(theme)
        verify(mockView).populateData(
            language = any(),
            theme = eq(theme)
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun changeTheme_exception_errorHandler() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        whenever(mockSetAppThemeUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val theme = AppTheme.DARK
        clearInvocationsForAll()

        presenter.changeTheme(theme)
        testScheduler.advanceUntilIdle()

        verify(mockSetAppThemeUseCase)(theme)
        verify(mockErrorHandler).proceed(any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestingSettings_openTestingSettingsScreen() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openTestingSettings()

        verify(mockView).routerNavigateTo(screenMatcher(TestingSettingsFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openAnalysis_openAnalysisFlow() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.openAnalysis()

        verify(mockView).routerStartFlow(screenMatcher(AnalysisFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun exportData_openExportJournalScreen() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.exportData()

        verify(mockView).routerNavigateTo(screenMatcher(ExportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importData_openImportJournalScreen() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.importData()

        verify(mockView).routerNavigateTo(screenMatcher(ImportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun clearJournal_sendEvent() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.clearJournal()

        verify(mockEventDispatcher).sendEvent(AppEvent.JournalTotalDeletionRequested)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun deleteDuplicatesInJournal_sendEvent() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocationsForAll()

        presenter.deleteDuplicatesInJournal()

        verify(mockEventDispatcher).sendEvent(AppEvent.JournalDuplicatesDeletionRequested)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun generateData_success() = runTest {
        whenever(mockGetAppThemeUseCase()).doReturn(UseCaseResult(AppTheme.SYSTEM))
        whenever(mockGenerateDataUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        val generationType = DataGenerationType.GOOD_VISION
        clearInvocationsForAll()

        presenter.generateData(generationType)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockGenerateDataUseCase)(generationType)
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
            mockGenerateDataUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}