package ru.rznnike.eyehealthmanager.app.presentation.main.tests

import androidx.fragment.app.Fragment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.AcuityFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.AstigmatismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.ColorPerceptionFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.ContrastFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.DaltonismFlowFragment
import ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.NearFarFlowFragment
import ru.rznnike.eyehealthmanager.app.utils.screenMatcher
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import kotlin.reflect.KClass

@ExtendWith(MockitoExtension::class)
class TestsPresenterTest {
    @Mock
    private lateinit var mockView: TestsView

    @Test
    fun onSelectTest_acuity_openAcuityFlow() = onSelectTest_testBody(
        testType = TestType.ACUITY,
        fragmentClass = AcuityFlowFragment::class
    )

    @Test
    fun onSelectTest_astigmatism_openAstigmatismFlow() = onSelectTest_testBody(
        testType = TestType.ASTIGMATISM,
        fragmentClass = AstigmatismFlowFragment::class
    )

    @Test
    fun onSelectTest_nearFar_openNearFarFlow() = onSelectTest_testBody(
        testType = TestType.NEAR_FAR,
        fragmentClass = NearFarFlowFragment::class
    )

    @Test
    fun onSelectTest_colorPerception_openColorPerceptionFlow() = onSelectTest_testBody(
        testType = TestType.COLOR_PERCEPTION,
        fragmentClass = ColorPerceptionFlowFragment::class
    )

    @Test
    fun onSelectTest_daltonism_openDaltonismFlow() = onSelectTest_testBody(
        testType = TestType.DALTONISM,
        fragmentClass = DaltonismFlowFragment::class
    )

    @Test
    fun onSelectTest_contrast_openContrastFlow() = onSelectTest_testBody(
        testType = TestType.CONTRAST,
        fragmentClass = ContrastFlowFragment::class
    )

    private fun onSelectTest_testBody(testType: TestType, fragmentClass: KClass<out Fragment>) {
        val presenter = TestsPresenter()
        presenter.attachView(mockView)

        presenter.onSelectTest(testType)

        verify(mockView, only()).routerStartFlow(screenMatcher(fragmentClass))
    }
}