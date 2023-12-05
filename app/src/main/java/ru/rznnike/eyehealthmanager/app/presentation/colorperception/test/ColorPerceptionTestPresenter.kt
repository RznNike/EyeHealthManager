package ru.rznnike.eyehealthmanager.app.presentation.colorperception.test

import android.graphics.Color
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
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult

@InjectViewState
class ColorPerceptionTestPresenter : BasePresenter<ColorPerceptionTestView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var currentLevelPairs: MutableList<Pair<Int, Int>> = mutableListOf()
    private var nextLevelPairs: MutableList<Pair<Int, Int>> = mutableListOf()
    private var currentLevelRecognizedPairs: MutableList<Pair<Int, Int>> = mutableListOf()
    private val allRecognizedPairs: MutableList<MutableList<Pair<Int, Int>>> = mutableListOf()
    private var pairDismissStepValues = hashMapOf<Int, Int>()
    private var currentStep = 0

    private val testColors: Array<Int> = arrayOf(
        Color.rgb(155, 92, 203),
        Color.rgb(165, 95, 217),
        Color.rgb(172, 99, 230),
        Color.rgb(202, 113, 246),
        Color.rgb(247, 131, 254),
        Color.rgb(150, 113, 246),
        Color.rgb(118, 113, 246),
        Color.rgb(113, 118, 246),
        Color.rgb(113, 125, 246),
        Color.rgb(113, 133, 246),
        Color.rgb(113, 158, 246),
        Color.rgb(113, 173, 246),
        Color.rgb(113, 184, 246),
        Color.rgb(113, 195, 246),
        Color.rgb(113, 210, 246),
        Color.rgb(113, 221, 246),
        Color.rgb(113, 239, 246),
        Color.rgb(113, 246, 243),
        Color.rgb(113, 246, 213),
        Color.rgb(113, 246, 188),
        Color.rgb(113, 246, 152),
        Color.rgb(113, 246, 135),
        Color.rgb(113, 246, 113),
        Color.rgb(146, 246, 113),
        Color.rgb(184, 246, 113),
        Color.rgb(208, 246, 113),
        Color.rgb(225, 255, 99),
        Color.rgb(255, 255, 85),
        Color.rgb(255, 250, 72),
        Color.rgb(246, 229, 112),
        Color.rgb(246, 219, 112),
        Color.rgb(246, 207, 112),
        Color.rgb(246, 182, 112),
        Color.rgb(246, 157, 112),
        Color.rgb(246, 141, 112),
        Color.rgb(246, 119, 112),
        Color.rgb(246, 112, 112),
        Color.rgb(215, 91, 91),
        Color.rgb(150, 73, 73)
    )

    override fun onFirstViewAttach() {
        initData()
        goToNextStep()
    }

    private fun initData() {
        // stub
        currentLevelPairs.add(Pair(0, 0))
        // start value
        currentLevelPairs.add(Pair(0, testColors.size - 1))
        // data for progress counting
        pairDismissStepValues[2] = 1
        for (size in 3..testColors.size) {
            val firstPairSize = size / 2 + 1
            val secondPairSize = size - firstPairSize + 1
            pairDismissStepValues[size] = (pairDismissStepValues[firstPairSize] ?: 0) + (pairDismissStepValues[secondPairSize] ?: 0) + 1
        }
    }

    fun onYes() {
        currentStep++
        currentLevelRecognizedPairs.add(currentLevelPairs.first())
        generateChildPairs()
        goToNextStep()
    }

    fun onNo() {
        val pairSize = currentLevelPairs.first().second - currentLevelPairs.first().first
        currentStep += pairDismissStepValues[pairSize] ?: 1
        goToNextStep()
    }

    private fun generateChildPairs() {
        val currentPair = currentLevelPairs.first()
        val delta = currentPair.second - currentPair.first
        if (delta > 1) {
            val middle = currentPair.first + delta / 2
            nextLevelPairs.add(Pair(currentPair.first, middle))
            nextLevelPairs.add(Pair(middle, currentPair.second))
        }
    }

    private fun goToNextStep() {
        currentLevelPairs.removeAt(0)
        if (currentLevelPairs.size > 0) {
            showNextColorsPair()
        } else {
            if (currentLevelRecognizedPairs.size > 0) {
                allRecognizedPairs.add(currentLevelRecognizedPairs)
                currentLevelRecognizedPairs = mutableListOf()
                if (nextLevelPairs.size > 0) {
                    currentLevelPairs = nextLevelPairs
                    nextLevelPairs = mutableListOf()

                    showNextColorsPair()
                } else {
                    finishTest()
                }
            } else {
                finishTest()
            }
        }
    }

    private fun showNextColorsPair() {
        viewState.populateData(
            testColors[currentLevelPairs[0].first],
            testColors[currentLevelPairs[0].second],
            getCurrentProgress()
        )
    }

    private fun getCurrentProgress(): Int {
        return currentStep * 100 / (pairDismissStepValues[testColors.size] ?: 0)
    }

    private fun finishTest() {
        presenterScope.launch {
            for (level in 1 until allRecognizedPairs.size) {
                // remove parent intervals
                for (step in 0..(allRecognizedPairs[level].size - 2)) {
                    val first = allRecognizedPairs[level][step].first
                    val second = allRecognizedPairs[level][step + 1].second
                    allRecognizedPairs[level - 1].removeAll { (it.first == first) && (it.second == second) }
                }
                // remove outer intervals
                for (step in 0 until allRecognizedPairs[level].size) {
                    val pair = allRecognizedPairs[level][step]
                    allRecognizedPairs[level - 1].removeAll { (it.first == pair.first) || (it.second == pair.second) }
                }
            }

            val recognizedColorsCount = 1 + allRecognizedPairs.sumOf { it.size }

            viewState.setProgress(true)
            val testResult = ColorPerceptionTestResult(
                timestamp = System.currentTimeMillis(),
                recognizedColorsCount = recognizedColorsCount,
                allColorsCount = testColors.size
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(Screens.Screen.colorPerceptionResult(recognizedColorsCount, testColors.size))
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
