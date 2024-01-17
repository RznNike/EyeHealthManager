package ru.rznnike.eyehealthmanager.app.presentation.contrast.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.R

@ExtendWith(MockitoExtension::class)
class ContrastResultPresenterTest {
    @Mock
    private lateinit var mockView: ContrastResultView

    @Test
    fun onFirstViewAttach_perfectResult_populateData() {
        val recognizedDelta = 1
        val presenter = ContrastResultPresenter(recognizedDelta)

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedDelta = recognizedDelta,
            messageResId = R.string.contrast_perfect_result
        )
    }

    @Test
    fun onFirstViewAttach_goodResult_populateData() {
        val recognizedDelta = 9
        val presenter = ContrastResultPresenter(recognizedDelta)

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedDelta = recognizedDelta,
            messageResId = R.string.contrast_good_result
        )
    }

    @Test
    fun onFirstViewAttach_badResult_populateData() {
        val recognizedDelta = 10
        val presenter = ContrastResultPresenter(recognizedDelta)

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedDelta = recognizedDelta,
            messageResId = R.string.doctor_warning
        )
    }
}