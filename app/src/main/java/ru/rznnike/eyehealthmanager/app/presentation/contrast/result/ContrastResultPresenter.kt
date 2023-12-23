package ru.rznnike.eyehealthmanager.app.presentation.contrast.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

private const val PERFECT_VALUE = 1
private const val GOOD_BORDER = 10

@InjectViewState
class ContrastResultPresenter(
    private val recognizedDelta: Int
) : BasePresenter<ContrastResultView>() {
    override fun onFirstViewAttach() {
        val messageResId = when {
            recognizedDelta <= PERFECT_VALUE -> R.string.contrast_perfect_result
            recognizedDelta < GOOD_BORDER -> R.string.contrast_good_result
            else -> R.string.doctor_warning
        }
        viewState.populateData(
            recognizedDelta = recognizedDelta,
            messageResId = messageResId
        )
    }
}
