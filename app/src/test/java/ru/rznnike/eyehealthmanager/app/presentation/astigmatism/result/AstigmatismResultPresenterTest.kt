package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType

@ExtendWith(MockitoExtension::class)
class AstigmatismResultPresenterTest {
    @Mock
    private lateinit var mockView: AstigmatismResultView

    @Test
    fun onFirstViewAttach() {
        val answerLeftEye = AstigmatismAnswerType.OK
        val answerRightEye = AstigmatismAnswerType.ANOMALY
        val presenter = AstigmatismResultPresenter(
            answerLeftEye = answerLeftEye,
            answerRightEye = answerRightEye
        )

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            answerLeftEye = answerLeftEye,
            answerRightEye = answerRightEye
        )
    }
}