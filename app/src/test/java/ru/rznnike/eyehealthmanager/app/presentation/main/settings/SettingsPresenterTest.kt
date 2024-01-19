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
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetUserLanguageUseCase
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.model.enums.Language

@ExtendWith(MockitoExtension::class)
class SettingsPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: SettingsView

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockGetUserLanguageUseCase: GetUserLanguageUseCase by inject()
    private val mockSetUserLanguageUseCase: SetUserLanguageUseCase by inject()
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
                single { mock<GetUserLanguageUseCase>() }
                single { mock<SetUserLanguageUseCase>() }
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
    fun onResume_success_populateLanguage() = runTest {
        val language = Language.RU
        whenever(mockGetUserLanguageUseCase()).doReturn(UseCaseResult(language))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.onResume()
        testScheduler.advanceUntilIdle()

        verify(mockGetUserLanguageUseCase)()
        verify(mockView).populateData(language)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onResume_exception_errorHAndler() = runTest {
        whenever(mockGetUserLanguageUseCase()).doReturn(UseCaseResult(error = Exception()))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.onResume()
        testScheduler.advanceUntilIdle()

        verify(mockGetUserLanguageUseCase)()
        verify(mockErrorHandler).proceed(any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun changeLanguage_success_updateUI() = runTest {
        val language = Language.RU
        whenever(mockSetUserLanguageUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.changeLanguage(Language.RU)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockSetUserLanguageUseCase)(language)
        verify(mockView).updateUiLanguage()
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun changeLanguage_exception_errorHandler() = runTest {
        val language = Language.RU
        whenever(mockSetUserLanguageUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.changeLanguage(Language.RU)
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockSetUserLanguageUseCase)(language)
        verify(mockErrorHandler).proceed(any(), any())
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openTestingSettings_openTestingSettingsScreen() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.openTestingSettings()

        verify(mockView).routerNavigateTo(screenMatcher(TestingSettingsFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun openAnalysis_openAnalysisFlow() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.openAnalysis()

        verify(mockView).routerStartFlow(screenMatcher(AnalysisFlowFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun exportData_openExportJournalScreen() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.exportData()

        verify(mockView).routerNavigateTo(screenMatcher(ExportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun importData_openImportJournalScreen() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.importData()

        verify(mockView).routerNavigateTo(screenMatcher(ImportJournalFragment::class))
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun clearJournal_sendEvent() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.clearJournal()

        verify(mockEventDispatcher).sendEvent(AppEvent.JournalTotalDeletionRequested)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun deleteDuplicatesInJournal_sendEvent() {
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)

        presenter.deleteDuplicatesInJournal()

        verify(mockEventDispatcher).sendEvent(AppEvent.JournalDuplicatesDeletionRequested)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun generateData() = runTest {
        whenever(mockGenerateDataUseCase(any())).doReturn(UseCaseResult(Unit))
        val presenter = SettingsPresenter()
        presenter.attachView(mockView)
        val generationType = DataGenerationType.GOOD_VISION

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
            mockGetUserLanguageUseCase,
            mockSetUserLanguageUseCase,
            mockGenerateDataUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}