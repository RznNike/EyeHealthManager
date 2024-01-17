package ru.rznnike.eyehealthmanager.app.presentation.daltonism.info

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.test.DaltonismTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class DaltonismInfoPresenterTest {
    @Mock
    private lateinit var mockView: DaltonismInfoView

    @Test
    fun startTest_openDaltonismTestScreen() {
        val presenter = DaltonismInfoPresenter()
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(screenMatcher(DaltonismTestFragment::class))
    }
}