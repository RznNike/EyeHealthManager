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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetTestingSettingsUseCase

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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun onFirstViewAttach_loadDataAndSuccess_populateData() = runTest {
        // TODO
    }

    @Test
    fun onFirstViewAttach_loadDataAndException_closeScreen() = runTest {
        // TODO
    }

    @Test
    fun onPause_saveChanges() = runTest {
        // TODO
    }

    @Test
    fun onCheckBoxReplaceBeginningClicked_populateData() {
        // TODO
    }

    @Test
    fun onCheckBoxAutoDayPartSelectionClicked_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayBeginningValueChanged_correctOrder_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayBeginningValueChanged_messedOrder_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayMiddleValueChanged_correctOrder_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayMiddleValueChanged_messedOrder_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayEndValueChanged_correctOrder_populateData() {
        // TODO
    }

    @Test
    fun onTimeToDayEndValueChanged_messedOrder_populateData() {
        // TODO
    }

    @Test
    fun onArmsLengthValueChanged_populateData() {
        // TODO
    }

    @Test
    fun onBodyHeightValueChanged_populateData() {
        // TODO
    }

    @Test
    fun onScalingLineLengthValueChanged_populateData() {
        // TODO
    }

    @Test
    fun resetScale_populateData() {
        // TODO
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