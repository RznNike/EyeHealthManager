package ru.rznnike.eyehealthmanager.app.presentation.contrast.info

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.test.ContrastTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class ContrastInfoPresenterTest {
    @Mock
    private lateinit var mockView: ContrastInfoView

    @Test
    fun startTest_openContrastTestScreen() {
        val presenter = ContrastInfoPresenter()
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(screenMatcher(ContrastTestFragment::class))
    }
}