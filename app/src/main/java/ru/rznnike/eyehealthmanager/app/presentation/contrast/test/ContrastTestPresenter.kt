package ru.rznnike.eyehealthmanager.app.presentation.contrast.test

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
import ru.rznnike.eyehealthmanager.domain.model.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.Direction
import kotlin.random.Random

private const val START_VALUE = 100
private const val END_VALUE = 1
private const val FIRST_STAGE_STEP = 10
private const val FIRST_STAGE_BORDER = 10
private const val SECOND_STAGE_STEP = 1
private const val MIN_CORRECT_ANSWERS = 2
private const val MAX_ANSWERS = 3

@InjectViewState
class ContrastTestPresenter : BasePresenter<ContrastTestView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var currentDirection: Direction = Direction.UP
    private var currentDelta: Int = START_VALUE
    private var recognizedDelta: Int = currentDelta
    private var answersCount: Int = 0
    private var correctAnswersCount: Int = 0
    private var random: Random = Random.Default
    private var maxBaseSteps = 0
    private var currentBaseStep = 0

    init {
        initData()
        goToNextStep()
    }

    private fun initData() {
        maxBaseSteps = 1
        var testDelta = START_VALUE
        while (testDelta > END_VALUE) {
            maxBaseSteps++
            testDelta -= if (testDelta > FIRST_STAGE_BORDER) FIRST_STAGE_STEP else SECOND_STAGE_STEP
        }
    }

    fun onAnswer(direction: Direction) {
        answersCount++
        if (direction == currentDirection) {
            correctAnswersCount++
        }
        goToNextStep()
    }

    private fun goToNextStep() {
        when {
            correctAnswersCount >= MIN_CORRECT_ANSWERS -> {
                recognizedDelta = currentDelta
                if (recognizedDelta <= END_VALUE) {
                    finishTest()
                } else {
                    currentBaseStep++
                    answersCount = 0
                    correctAnswersCount = 0
                    currentDelta -= if (currentDelta > FIRST_STAGE_BORDER) FIRST_STAGE_STEP else SECOND_STAGE_STEP
                    currentDirection = Direction.entries[random.nextInt(Direction.entries.size)]
                    showNextImage()
                }
            }
            (answersCount - correctAnswersCount) > (MAX_ANSWERS - MIN_CORRECT_ANSWERS) -> {
                finishTest()
            }
            else -> {
                currentDirection = Direction.entries.random(random)
                showNextImage()
            }
        }
    }

    private fun showNextImage() {
        val foregroundDelta = currentDelta / 100f
        val backgroundAlpha = 0.5f - foregroundDelta / 2
        viewState.populateData(
            currentDirection,
            backgroundAlpha,
            foregroundDelta,
            getCurrentProgress()
        )
    }

    private fun getCurrentProgress() =
        (currentBaseStep * 100 / maxBaseSteps) + (answersCount * 100 / maxBaseSteps / MAX_ANSWERS)

    private fun finishTest() {
        presenterScope.launch {
            viewState.setProgress(true)
            val testResult = ContrastTestResult(
                timestamp = System.currentTimeMillis(),
                recognizedContrast = recognizedDelta
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(Screens.Screen.contrastResult(recognizedDelta))
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
