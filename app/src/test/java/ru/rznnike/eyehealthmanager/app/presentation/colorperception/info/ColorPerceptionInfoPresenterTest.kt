package ru.rznnike.eyehealthmanager.app.presentation.colorperception.info

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.test.ColorPerceptionTestFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.DaltonismFlowFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class ColorPerceptionInfoPresenterTest {
    @Mock
    private lateinit var mockView: ColorPerceptionInfoView

    @Test
    fun startTest_openColorPerceptionTestScreen() {
        val presenter = ColorPerceptionInfoPresenter()
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(screenMatcher(ColorPerceptionTestFragment::class))
    }

    @Test
    fun openDaltonismTest_openDaltonismFlow() {
        val presenter = ColorPerceptionInfoPresenter()
        presenter.attachView(mockView)

        presenter.openDaltonismTest()

        verify(mockView, only()).routerReplaceFlow(screenMatcher(DaltonismFlowFragment::class))
    }
}