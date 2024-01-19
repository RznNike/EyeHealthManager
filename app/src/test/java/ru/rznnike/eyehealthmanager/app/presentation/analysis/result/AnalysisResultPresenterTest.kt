package ru.rznnike.eyehealthmanager.app.presentation.analysis.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.SingleEyeAnalysisResult

@ExtendWith(MockitoExtension::class)
class AnalysisResultPresenterTest {
    @Mock
    private lateinit var mockView: AnalysisResultView

    @Test
    fun onFirstViewAttach() {
        val analysisResult = AnalysisResult(
            testResults = emptyList(),
            leftEyeAnalysisResult = SingleEyeAnalysisResult(
                chartData = emptyList(),
                extrapolatedResult = null,
                statistics = null,
                dynamicCorrections = null
            ),
            rightEyeAnalysisResult = SingleEyeAnalysisResult(
                chartData = emptyList(),
                extrapolatedResult = null,
                statistics = null,
                dynamicCorrections = null
            ),
            showWarningAboutVision = true,
            lastResultRecognizedAsNoise = false
        )
        val presenter = AnalysisResultPresenter(analysisResult)

        presenter.attachView(mockView)

        verify(mockView).populateData(analysisResult)
        verifyNoMoreInteractions(mockView)
    }
}