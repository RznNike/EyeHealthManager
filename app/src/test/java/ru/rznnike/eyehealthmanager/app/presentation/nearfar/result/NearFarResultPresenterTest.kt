package ru.rznnike.eyehealthmanager.app.presentation.nearfar.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

@ExtendWith(MockitoExtension::class)
class NearFarResultPresenterTest {
    @Mock
    private lateinit var mockView: NearFarResultView

    @Test
    fun onFirstViewAttach_populateData() {
        val answerLeftEye = NearFarAnswerType.EQUAL
        val answerRightEye = NearFarAnswerType.GREEN_BETTER
        val presenter = NearFarResultPresenter(
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