package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.info

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.test.AstigmatismTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class AstigmatismInfoPresenterTest {
    @Mock
    private lateinit var mockView: AstigmatismInfoView

    @Test
    fun startTest_openAstigmatismTestScreen() {
        val presenter = AstigmatismInfoPresenter()
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(screenMatcher(AstigmatismTestFragment::class))
    }
}