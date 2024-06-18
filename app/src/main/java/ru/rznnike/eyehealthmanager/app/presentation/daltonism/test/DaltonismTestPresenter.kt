package ru.rznnike.eyehealthmanager.app.presentation.daltonism.test

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
import ru.rznnike.eyehealthmanager.app.model.test.daltonism.DaltonismTestData
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType
import java.time.Clock

private const val NORMAL_BORDER = 2

@InjectViewState
class DaltonismTestPresenter : BasePresenter<DaltonismTestView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var currentStep: Int = -1
    private val answersOrder: MutableList<Int> = mutableListOf(0, 1, 2, 3)
    private val userAnswers: MutableList<Int> = mutableListOf()
    private val answerDeltas: MutableMap<DaltonismAnomalyType, Int> = mutableMapOf()

    override fun onFirstViewAttach() {
        nextStep()
    }

    fun answer(selection: Int) {
        userAnswers.add(answersOrder[selection])
        nextStep()
    }

    private fun nextStep() {
        currentStep++
        if (currentStep > DaltonismTestData.questions.lastIndex) {
            finishTest()
        } else {
            answersOrder.shuffle()
            showTestQuestion()
        }
    }

    private fun showTestQuestion() {
        val currentQuestion = DaltonismTestData.questions[currentStep]
        viewState.populateData(
            imageResId = currentQuestion.testImageResId,
            variants = answersOrder.map { currentQuestion.answerResIds[it] },
            progress = getCurrentProgress()
        )
    }

    private fun getCurrentProgress() = currentStep * 100 / DaltonismTestData.questions.size

    private fun finishTest() {
        presenterScope.launch {
            viewState.setProgress(true)
            DaltonismTestData.questions.forEachIndexed { index, question ->
                if (userAnswers[index] > 0) {
                    answerDeltas[DaltonismAnomalyType.NONE] = answerDeltas.getOrDefault(
                        DaltonismAnomalyType.NONE, 0) + 1
                }
                question.answerVariantsMap.forEach { (type, variants) ->
                    if (!variants.contains(userAnswers[index])) {
                        answerDeltas[type] = answerDeltas.getOrDefault(type, 0) + 1
                    }
                }
                question.answerBooleanMap.forEach { (type, value) ->
                    if (value != (userAnswers[index] == 0)) {
                        answerDeltas[type] = answerDeltas.getOrDefault(type, 0) + 1
                    }
                }
            }

            val errorsCount = answerDeltas.getOrDefault(DaltonismAnomalyType.NONE, 0)
            val anomalyType = if (errorsCount <= NORMAL_BORDER) {
                DaltonismAnomalyType.NONE
            } else {
                answerDeltas.toList()
                    .filter { (type, _) -> type != DaltonismAnomalyType.NONE }
                    .minBy { (_, value) -> value }
                    .first
            }

            val testResult = DaltonismTestResult(
                timestamp = clock.millis(),
                errorsCount = errorsCount,
                anomalyType = anomalyType
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(
                        Screens.Screen.daltonismResult(
                            errorsCount = errorsCount,
                            resultType = anomalyType
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
