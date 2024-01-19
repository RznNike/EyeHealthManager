package ru.rznnike.eyehealthmanager.app.presentation.colorperception.result

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import ru.rznnike.eyehealthmanager.R

@ExtendWith(MockitoExtension::class)
class ColorPerceptionResultPresenterTest {
    @Mock
    private lateinit var mockView: ColorPerceptionResultView

    @Test
    fun onFirstViewAttach_badVision_populateData() {
        val recognizedCount = 0
        val allCount = 39
        val presenter = ColorPerceptionResultPresenter(
            recognizedCount = recognizedCount,
            allCount = allCount
        )

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedCount = recognizedCount,
            allCount = allCount,
            messageResId = R.string.dichromat_message
        )
    }

    @Test
    fun onFirstViewAttach_averageVision_populateData() {
        val recognizedCount = 20
        val allCount = 39
        val presenter = ColorPerceptionResultPresenter(
            recognizedCount = recognizedCount,
            allCount = allCount
        )

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedCount = recognizedCount,
            allCount = allCount,
            messageResId = R.string.trichromat_message
        )
    }

    @Test
    fun onFirstViewAttach_goodVision_populateData() {
        val recognizedCount = 32
        val allCount = 39
        val presenter = ColorPerceptionResultPresenter(
            recognizedCount = recognizedCount,
            allCount = allCount
        )

        presenter.attachView(mockView)

        verify(mockView, only()).populateData(
            recognizedCount = recognizedCount,
            allCount = allCount,
            messageResId = R.string.tetrachromat_message
        )
    }
}