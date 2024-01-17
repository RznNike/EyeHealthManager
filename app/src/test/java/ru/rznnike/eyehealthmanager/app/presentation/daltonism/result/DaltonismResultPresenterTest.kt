package ru.rznnike.eyehealthmanager.app.presentation.daltonism.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

@ExtendWith(MockitoExtension::class)
class DaltonismResultPresenterTest {
    @Mock
    private lateinit var mockView: DaltonismResultView

    @Test
    fun onFirstViewAttach_populateData() {
        val errorsCount = 1
        val resultType = DaltonismAnomalyType.DEITERANOMALY_B
        val presenter = DaltonismResultPresenter(
            errorsCount = errorsCount,
            resultType = resultType
        )

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            errorsCount = errorsCount,
            resultType = resultType
        )
    }
}