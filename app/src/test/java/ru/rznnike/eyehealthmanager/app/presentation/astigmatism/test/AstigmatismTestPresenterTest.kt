package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test

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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.answer.AstigmatismAnswerFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings

@ExtendWith(MockitoExtension::class)
class AstigmatismTestPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: AstigmatismTestView

    private val mockGetTestingSettingsUseCase: GetTestingSettingsUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mock<GetTestingSettingsUseCase>() }
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
    fun onFirstViewAttach_setScale() = runTest {
        val settings = TestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(settings))
        val presenter = AstigmatismTestPresenter()

        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()

        verify(mockGetTestingSettingsUseCase)()
        verify(mockView).setScale(settings)
        verifyNoMoreInteractions(mockView, mockGetTestingSettingsUseCase)
    }

    @Test
    fun openAnswer_openAstigmatismAnswerScreen() = runTest {
        val settings = TestingSettings()
        whenever(mockGetTestingSettingsUseCase()).doReturn(UseCaseResult(settings))
        val presenter = AstigmatismTestPresenter()
        presenter.attachView(mockView)
        testScheduler.advanceUntilIdle()
        clearInvocations(mockView, mockGetTestingSettingsUseCase)

        presenter.openAnswer()

        verify(mockView, only()).routerNavigateTo(screenMatcher(AstigmatismAnswerFragment::class))
    }
}