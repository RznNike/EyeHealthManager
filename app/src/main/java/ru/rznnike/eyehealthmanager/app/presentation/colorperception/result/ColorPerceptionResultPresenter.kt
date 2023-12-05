package ru.rznnike.eyehealthmanager.app.presentation.colorperception.result

import moxy.InjectViewState
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter

private const val DICHROMAT_BORDER = 20
private const val TRICHROMAT_BORDER = 32

@InjectViewState
class ColorPerceptionResultPresenter(
    private val recognizedCount: Int,
    private val allCount: Int
) : BasePresenter<ColorPerceptionResultView>() {
    override fun onFirstViewAttach() {
        val messageResId = when (recognizedCount) {
            in 0 until DICHROMAT_BORDER -> R.string.dichromat_message
            in DICHROMAT_BORDER until TRICHROMAT_BORDER -> R.string.trichromat_message
            else -> R.string.tetrachromat_message
        }
        viewState.populateData(
            recognizedCount = recognizedCount,
            allCount = allCount,
            messageResId = messageResId
        )
    }
}
