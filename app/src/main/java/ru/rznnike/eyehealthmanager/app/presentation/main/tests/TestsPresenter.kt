package ru.rznnike.eyehealthmanager.app.presentation.main.tests

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

@InjectViewState
class TestsPresenter : BasePresenter<TestsView>() {
    fun onSelectTest(testType: TestType) {
        viewState.routerStartFlow(
            when (testType) {
                TestType.ACUITY -> Screens.Flow.acuity()
                TestType.ASTIGMATISM -> Screens.Flow.astigmatism()
                TestType.NEAR_FAR -> Screens.Flow.nearFar()
                TestType.COLOR_PERCEPTION -> Screens.Flow.colorPerception()
                TestType.DALTONISM -> Screens.Flow.daltonism()
                TestType.CONTRAST -> Screens.Flow.contrast()
            }
        )
    }
}
