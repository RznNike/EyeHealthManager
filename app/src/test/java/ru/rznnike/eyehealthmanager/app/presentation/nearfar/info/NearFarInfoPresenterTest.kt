package ru.rznnike.eyehealthmanager.app.presentation.nearfar.info

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.test.NearFarTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher

@ExtendWith(MockitoExtension::class)
class NearFarInfoPresenterTest {
    @Mock
    private lateinit var mockView: NearFarInfoView

    @Test
    fun startTest_openNearFarTestScreen() {
        val presenter = NearFarInfoPresenter()
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(screenMatcher(NearFarTestFragment::class))
    }
}