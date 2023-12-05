package ru.rznnike.eyehealthmanager.app.presentation.acuity.test

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
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.*
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

private const val VISION_START = 10
private const val VISION_END = 200
private const val VISION_STEP = 10
private const val MIN_CORRECT_ANSWERS = 2
private const val MAX_ANSWERS = 3

@InjectViewState
class AcuityTestPresenter(
    private var dayPart: DayPart
) : BasePresenter<AcuityTestView>() {
    private val preferences: PreferencesWrapper by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var testCount = 0
    private var requiredTestCount = 0
    private val testResult = AcuityTestResult()
    private var currentEye: TestEyesType = TestEyesType.LEFT
    private var stepType: StepType = StepType.INFO
    private var symbolsType: AcuityTestSymbolsType = AcuityTestSymbolsType.LETTERS_RU
    private var dpmm: Float = 0f
    private var distance: Int = 0
    private var currentSymbol: IAcuitySymbol = EmptyAcuitySymbol
    private var selectedSymbol: IAcuitySymbol? = EmptyAcuitySymbol
    private var vision = VISION_START
    private var answersCount: Int = 0
    private var correctAnswersCount: Int = 0
    private val random = Random(System.currentTimeMillis())

    override fun onFirstViewAttach() {
        presenterScope.launch {
            val testType = TestEyesType[preferences.testEyesType.get()]
            when (testType) {
                TestEyesType.BOTH -> {
                    currentEye = TestEyesType.LEFT
                    requiredTestCount = 2
                }
                TestEyesType.LEFT -> {
                    currentEye = TestEyesType.LEFT
                    requiredTestCount = 1
                }
                TestEyesType.RIGHT -> {
                    currentEye = TestEyesType.RIGHT
                    requiredTestCount = 1
                }
            }
            symbolsType = AcuityTestSymbolsType[preferences.acuitySymbolsType.get()]
            dpmm = preferences.dotsPerMillimeter.get()
            distance = preferences.armsLength.get()

            testResult.testEyesType = testType
            testResult.symbolsType = symbolsType
            showCurrentStep()
        }
    }

    fun onSymbolSelected(symbol: IAcuitySymbol) {
        selectedSymbol = symbol
    }

    fun onSymbolUnrecognized() {
        selectedSymbol = EmptyAcuitySymbol
    }

    fun onNextStep() {
        when (stepType) {
            StepType.INFO -> {
                vision = VISION_START
                initTestForNewVisionLevel()
                showCurrentStep()
            }
            StepType.TEST -> {
                stepType = StepType.ANSWER
                showCurrentStep()
            }
            StepType.ANSWER -> {
                selectedSymbol?.let {
                    processAnswer()
                }
            }
        }
    }

    private fun initTestForNewVisionLevel() {
        stepType = StepType.TEST
        answersCount = 0
        correctAnswersCount = 0
        selectRandomSymbol()
    }

    private fun selectRandomSymbol() {
        currentSymbol = when (symbolsType) {
            AcuityTestSymbolsType.LETTERS_RU -> AcuitySymbolLetterRu.entries
            AcuityTestSymbolsType.LETTERS_EN -> AcuitySymbolLetterEn.entries
            AcuityTestSymbolsType.SQUARE -> AcuitySymbolSquare.entries
            AcuityTestSymbolsType.TRIANGLE -> AcuitySymbolTriangle.entries
        }
            .filter { it != selectedSymbol }
            .random(random)
        selectedSymbol = null
    }

    private fun processAnswer() {
        answersCount++
        if (selectedSymbol == currentSymbol) {
            correctAnswersCount++
        }
        when {
            correctAnswersCount >= MIN_CORRECT_ANSWERS -> {
                if (vision >= VISION_END) {
                    finishTest()
                } else {
                    vision += VISION_STEP
                    initTestForNewVisionLevel()
                    showCurrentStep()
                }
            }
            ((answersCount - correctAnswersCount) > (MAX_ANSWERS - MIN_CORRECT_ANSWERS)) -> {
                vision -= VISION_STEP
                finishTest()
            }
            else -> {
                stepType = StepType.TEST
                selectRandomSymbol()
                showCurrentStep()
            }
        }
    }

    private fun finishTest() {
        presenterScope.launch {
            if (currentEye == TestEyesType.LEFT) {
                testResult.resultLeftEye = vision
            } else {
                testResult.resultRightEye = vision
            }
            testCount++
            if (testCount < requiredTestCount) {
                currentEye = if (currentEye == TestEyesType.LEFT) TestEyesType.RIGHT else TestEyesType.LEFT
                stepType = StepType.INFO
                answersCount = 0
                vision = VISION_START
                showCurrentStep()
            } else {
                viewState.setProgress(true)
                val testResult = AcuityTestResult(
                    timestamp = System.currentTimeMillis(),
                    symbolsType = symbolsType,
                    testEyesType = TestEyesType[preferences.testEyesType.get()],
                    dayPart = dayPart,
                    resultLeftEye = testResult.resultLeftEye,
                    resultRightEye = testResult.resultRightEye
                )
                addTestResultUseCase(testResult).process(
                    { id ->
                        testResult.id = id
                        viewState.routerNewRootScreen(Screens.Screen.acuityResult(testResult))
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

    private fun showCurrentStep() {
        when (stepType) {
            StepType.INFO -> {
                viewState.showInfo(
                    currentEye,
                    getCurrentProgress()
                )
            }
            StepType.TEST -> {
                viewState.showTestStep(
                    currentSymbol.getDrawableRes(),
                    vision,
                    dpmm,
                    distance,
                    getCurrentProgress()
                )
            }
            StepType.ANSWER -> {
                viewState.showAnswerVariants(
                    symbolsType,
                    getCurrentProgress()
                )
            }
        }
    }

    private fun getCurrentProgress(): Int {
        fun circularTransform(x: Float) = sqrt((10000 - (x - 100).pow(2)))

        // step
        var result = (vision - VISION_START + answersCount.toFloat() * VISION_STEP / MAX_ANSWERS) /
                (VISION_END - VISION_START) * 100
        // substep
        result += VISION_STEP / (VISION_END - VISION_START)
        // transform with non-linear function
        result = circularTransform(result)
        // multi-eye case
        if (requiredTestCount == 2) {
            result = result / 2 + 50 * testCount
        }

        return result.toInt()
    }

    private enum class StepType {
        INFO,
        TEST,
        ANSWER
    }
}
