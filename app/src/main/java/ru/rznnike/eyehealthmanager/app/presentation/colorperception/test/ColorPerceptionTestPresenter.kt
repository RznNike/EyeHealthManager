package ru.rznnike.eyehealthmanager.app.presentation.colorperception.test

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.app.Screens
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestData
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult
import java.time.Clock

@InjectViewState
class ColorPerceptionTestPresenter : BasePresenter<ColorPerceptionTestView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var currentPair = Pair(0, 1)
    private var recognizedColorsCount = 1

    override fun onFirstViewAttach() {
        populateData()
    }

    private fun populateData() =
        viewState.populateData(
            color1 = ColorPerceptionTestData.colors[currentPair.first],
            color2 = ColorPerceptionTestData.colors[currentPair.second],
            progress = getCurrentProgress()
        )

    private fun getCurrentProgress() =
        (currentPair.second - 1) * 100 / ColorPerceptionTestData.colors.lastIndex

    fun answer(canRecognize: Boolean) {
        currentPair = if (canRecognize) {
            recognizedColorsCount++
            Pair(currentPair.second, currentPair.second + 1)
        } else {
            Pair(currentPair.first, currentPair.second + 1)
        }

        if (currentPair.second > ColorPerceptionTestData.colors.lastIndex) {
            finishTest()
        } else {
            populateData()
        }
    }

    private fun finishTest() {
        presenterScope.launch {
            viewState.setProgress(true)
            val testResult = ColorPerceptionTestResult(
                timestamp = clock.millis(),
                recognizedColorsCount = recognizedColorsCount,
                allColorsCount = ColorPerceptionTestData.colors.size
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(
                        Screens.Screen.colorPerceptionResult(
                            recognizedCount = recognizedColorsCount,
                            allCount = ColorPerceptionTestData.colors.size
                        )
                    )
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )
            eventDispatcher.sendEvent(AppEvent.JournalChanged)
            viewState.setProgress(false)
        }
    }
}
