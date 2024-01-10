package ru.rznnike.eyehealthmanager.app.presentation.splash

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import ru.rznnike.eyehealthmanager.app.ui.fragment.main.MainFlowFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class SplashPresenterTest {
    @Mock
    private lateinit var mockView: SplashView

    @Test
    fun onAnimationEnd_replaceWithMainFlow() {
        val presenter = SplashPresenter()
        presenter.attachView(mockView)
        clearInvocations(mockView)

        presenter.onAnimationEnd()

        verify(mockView, only()).routerNewRootFlow(screenMatcher(MainFlowFragment::class))
    }
}