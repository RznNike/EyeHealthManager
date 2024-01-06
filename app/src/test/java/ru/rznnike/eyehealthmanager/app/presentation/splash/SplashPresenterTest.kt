package ru.rznnike.eyehealthmanager.app.presentation.splash

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import ru.rznnike.eyehealthmanager.app.Screens
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
        reset(mockView)

        presenter.onAnimationEnd()

        verify(mockView, only()).routerNewRootFlow(screenMatcher(MainFlowFragment::class))
    }
}