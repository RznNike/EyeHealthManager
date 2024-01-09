package ru.rznnike.eyehealthmanager.app.presentation.acuity.info

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.SetAcuityTestingSettingsUseCase

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
                single { mock<ErrorHandler>() }
                single { mock<GetTestingSettingsUseCase>() }
                single { mock<GetAcuityTestingSettingsUseCase>() }
                single { mock<SetAcuityTestingSettingsUseCase>() }
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
    fun onEvent_testingSettingsChanged_loadData() {
        // TODO
    }

    @Test
    fun onEvent_wrongEvent_nothing() {
        // TODO
    }

    @Test
    fun onFirstViewAttach_loadData() {
        // TODO
    }

    @Test
    fun onSymbolsTypeSelected_success() {
        // TODO
    }

    @Test
    fun onEyesTypeSelected_success() {
        // TODO
    }

    @Test
    fun onScaleSettings_openTestingSettingsScreen() {
        // TODO
    }

    @Test
    fun onDayPartAutoSelectionSettings_openTestingSettingsScreen() {
        // TODO
    }

    @Test
    fun startTest_saveSettingsAndOpenNextScreen() {
        // TODO
    }

    @Test
    fun onPrepareToStartTest_autoDayPart_startTest() {
        // TODO
    }

    @Test
    fun onPrepareToStartTest_manualDayPart_showSelection() {
        // TODO
    }

    @Test
    fun onAddDoctorResult_openAcuityDoctorResultScreen() {
        // TODO
    }
}