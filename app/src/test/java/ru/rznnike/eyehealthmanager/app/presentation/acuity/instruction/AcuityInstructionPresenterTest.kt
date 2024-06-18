package ru.rznnike.eyehealthmanager.app.presentation.acuity.instruction

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.test.AcuityTestFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart

@ExtendWith(MockitoExtension::class)
class AcuityInstructionPresenterTest {
    @Mock
    private lateinit var mockView: AcuityInstructionView

    @Test
    fun startTest_openAcuityTestScreen() {
        val dayPart = DayPart.END
        val presenter = AcuityInstructionPresenter(dayPart)
        presenter.attachView(mockView)

        presenter.startTest()

        verify(mockView, only()).routerNewRootScreen(
            screenMatcher(AcuityTestFragment::class) { arguments ->
                arguments[AcuityTestFragment.DAY_PART] == dayPart
            }
        )
    }
}