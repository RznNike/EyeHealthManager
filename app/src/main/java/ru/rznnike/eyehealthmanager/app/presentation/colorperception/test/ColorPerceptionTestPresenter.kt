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
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants

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

    override fun onFirstViewAttach() {
        initData()
        nextStep()
    }

    private fun initData() {
        // stub
        currentLevelPairs.add(Pair(0, 0))
        // start value
        currentLevelPairs.add(Pair(0, GlobalConstants.COLOR_PERCEPTION_TEST_COLORS.lastIndex))
        // data for progress counting
        pairDismissStepValues[2] = 1
        for (size in 3..GlobalConstants.COLOR_PERCEPTION_TEST_COLORS.size) {
            val firstPairSize = size / 2 + 1
            val secondPairSize = size - firstPairSize + 1
            pairDismissStepValues[size] = (pairDismissStepValues[firstPairSize] ?: 0) + (pairDismissStepValues[secondPairSize] ?: 0) + 1
        }
    }

    fun answer(canRecognize: Boolean) {
        if (canRecognize) {
            currentStep++
            currentLevelRecognizedPairs.add(currentLevelPairs.first())
            generateChildPairs()
            nextStep()
        } else {
            val pairSize = currentLevelPairs.first().second - currentLevelPairs.first().first
            currentStep += pairDismissStepValues[pairSize] ?: 1
            nextStep()
        }
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

    private fun nextStep() {
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

    private fun showNextColorsPair() =
        viewState.populateData(
            GlobalConstants.COLOR_PERCEPTION_TEST_COLORS[currentLevelPairs[0].first],
            GlobalConstants.COLOR_PERCEPTION_TEST_COLORS[currentLevelPairs[0].second],
            getCurrentProgress()
        )

    private fun getCurrentProgress(): Int =
        currentStep * 100 / (pairDismissStepValues[GlobalConstants.COLOR_PERCEPTION_TEST_COLORS.size] ?: 0)

    private fun finishTest() {
        presenterScope.launch {
            viewState.setProgress(true)
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

            val testResult = ColorPerceptionTestResult(
                timestamp = System.currentTimeMillis(),
                recognizedColorsCount = recognizedColorsCount,
                allColorsCount = GlobalConstants.COLOR_PERCEPTION_TEST_COLORS.size
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(
                        Screens.Screen.colorPerceptionResult(
                            recognizedCount = recognizedColorsCount,
                            allCount = GlobalConstants.COLOR_PERCEPTION_TEST_COLORS.size
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
