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
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetAcuityTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.interactor.user.GetTestingSettingsUseCase
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.*
import java.time.Clock
import kotlin.math.pow
import kotlin.math.sqrt

private const val VISION_START = 10
private const val VISION_END = 200
private const val VISION_STEP = 10
private const val MIN_CORRECT_ANSWERS = 2
private const val MAX_ANSWERS = 3

@InjectViewState
class AcuityTestPresenter(
    private var dayPart: DayPart
) : BasePresenter<AcuityTestView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val getTestingSettingsUseCase: GetTestingSettingsUseCase by inject()
    private val getAcuityTestingSettingsUseCase: GetAcuityTestingSettingsUseCase by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var generalSettings = TestingSettings()
    private var acuitySettings = AcuityTestingSettings()
    private var eyesCount = 0
    private var eyesTested = 0
    private var currentEye: TestEyesType = TestEyesType.LEFT
    private var resultLeftEye: Int? = null
    private var resultRightEye: Int? = null
    private var stepType: StepType = StepType.INFO
    private var currentSymbol: IAcuitySymbol = EmptyAcuitySymbol
    private var selectedSymbol: IAcuitySymbol? = null
    private var visionLevel = VISION_START
    private var answersCount: Int = 0
    private var correctAnswersCount: Int = 0

    override fun onFirstViewAttach() {
        presenterScope.launch {
            viewState.setProgress(true)
            generalSettings = getTestingSettingsUseCase().data ?: TestingSettings()
            acuitySettings = getAcuityTestingSettingsUseCase().data ?: AcuityTestingSettings()
            eyesCount = if (acuitySettings.eyesType == TestEyesType.BOTH) 2 else 1
            currentEye = if (acuitySettings.eyesType == TestEyesType.RIGHT) TestEyesType.RIGHT else TestEyesType.LEFT
            populateData()
            viewState.setProgress(false)
        }
    }

    fun onSymbolSelected(symbol: IAcuitySymbol?) {
        selectedSymbol = symbol ?: EmptyAcuitySymbol
        populateData()
    }

    fun onNextStep() = when (stepType) {
        StepType.INFO -> {
            visionLevel = VISION_START
            initTestForNewVisionLevel()
            populateData()
        }
        StepType.TEST -> {
            stepType = StepType.ANSWER
            populateData()
        }
        StepType.ANSWER -> {
            selectedSymbol?.let {
                processAnswer()
            }
        }
    }

    private fun initTestForNewVisionLevel() {
        stepType = StepType.TEST
        answersCount = 0
        correctAnswersCount = 0
        randomizeCurrentSymbol()
    }

    private fun randomizeCurrentSymbol() {
        currentSymbol = when (acuitySettings.symbolsType) {
            AcuityTestSymbolsType.LETTERS_RU -> AcuitySymbolLetterRu.entries
            AcuityTestSymbolsType.LETTERS_EN -> AcuitySymbolLetterEn.entries
            AcuityTestSymbolsType.SQUARE -> AcuitySymbolSquare.entries
            AcuityTestSymbolsType.TRIANGLE -> AcuitySymbolTriangle.entries
        }
            .filter { it != selectedSymbol }
            .random()
        selectedSymbol = null
    }

    private fun processAnswer() {
        answersCount++
        if (selectedSymbol == currentSymbol) {
            correctAnswersCount++
        }
        when {
            correctAnswersCount >= MIN_CORRECT_ANSWERS ->
                if (visionLevel >= VISION_END) {
                    finishTest()
                } else {
                    visionLevel += VISION_STEP
                    initTestForNewVisionLevel()
                    populateData()
                }
            ((answersCount - correctAnswersCount) > (MAX_ANSWERS - MIN_CORRECT_ANSWERS)) -> {
                visionLevel -= VISION_STEP
                finishTest()
            }
            else -> {
                stepType = StepType.TEST
                randomizeCurrentSymbol()
                populateData()
            }
        }
    }

    private fun finishTest() {
        presenterScope.launch {
            if (currentEye == TestEyesType.LEFT) {
                resultLeftEye = visionLevel
            } else {
                resultRightEye = visionLevel
            }
            eyesTested++
            if (eyesTested < eyesCount) {
                currentEye = if (currentEye == TestEyesType.LEFT) TestEyesType.RIGHT else TestEyesType.LEFT
                stepType = StepType.INFO
                answersCount = 0
                visionLevel = VISION_START
                populateData()
            } else {
                viewState.setProgress(true)
                val testResult = AcuityTestResult(
                    timestamp = clock.millis(),
                    symbolsType = acuitySettings.symbolsType,
                    testEyesType = acuitySettings.eyesType,
                    dayPart = dayPart,
                    resultLeftEye = resultLeftEye,
                    resultRightEye = resultRightEye
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

    private fun populateData() {
        viewState.showTestProgress(getCurrentProgress())
        when (stepType) {
            StepType.INFO -> viewState.showInfo(currentEye)
            StepType.TEST -> viewState.showTestStep(
                imageResId = currentSymbol.getDrawableRes(),
                vision = visionLevel,
                dpmm = generalSettings.dpmm,
                distance = generalSettings.armsLength
            )
            StepType.ANSWER -> viewState.showAnswerVariants(
                symbolsType = acuitySettings.symbolsType,
                selectedSymbol = selectedSymbol
            )
        }
    }

    private fun getCurrentProgress(): Int {
        fun circularTransform(x: Double) = sqrt((1 - (x - 1).pow(2)))

        val currentValue = visionLevel - VISION_START + answersCount.toDouble() / MAX_ANSWERS * VISION_STEP
        val maxValue = VISION_END - VISION_START
        var progress = circularTransform(currentValue / maxValue)
        if (acuitySettings.eyesType == TestEyesType.BOTH) {
            progress = (progress + eyesTested) / 2
        }

        return (progress * 100).toInt()
    }

    private enum class StepType {
        INFO,
        TEST,
        ANSWER
    }
}
