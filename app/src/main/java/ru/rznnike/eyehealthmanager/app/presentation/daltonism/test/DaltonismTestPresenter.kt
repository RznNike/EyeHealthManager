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
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestQuestionsMap
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

private const val NORMAL_BORDER = 2

@InjectViewState
class DaltonismTestPresenter : BasePresenter<DaltonismTestView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var currentStep: Int = -1
    private val answersOrder: MutableList<Int> = mutableListOf(0, 1, 2, 3)
    private val userAnswers: MutableList<Int> = mutableListOf()
    private val answerDeltas: MutableMap<DaltonismAnomalyType, Int> = mutableMapOf()

    init {
        goToNextStep()
    }

    fun onAnswer(selection: Int) {
        userAnswers.add(answersOrder[selection])
        goToNextStep()
    }

    private fun goToNextStep() {
        currentStep++
        if (currentStep >= DaltonismTestQuestionsMap.questions.size) {
            finishTest()
        } else {
            answersOrder.shuffle()
            showTestQuestion()
        }
    }

    private fun showTestQuestion() {
        viewState.populateData(
            DaltonismTestQuestionsMap.questions.getValue(currentStep).testImageResId,
            listOf(
                DaltonismTestQuestionsMap.questions.getValue(currentStep).answerResIds[answersOrder[0]],
                DaltonismTestQuestionsMap.questions.getValue(currentStep).answerResIds[answersOrder[1]],
                DaltonismTestQuestionsMap.questions.getValue(currentStep).answerResIds[answersOrder[2]],
                DaltonismTestQuestionsMap.questions.getValue(currentStep).answerResIds[answersOrder[3]]
            ),
            getCurrentProgress()
        )
    }

    private fun getCurrentProgress(): Int {
        return currentStep * 100 / DaltonismTestQuestionsMap.questions.size
    }

    private fun finishTest() {
        presenterScope.launch {
            viewState.setProgress(true)
            for (i in DaltonismTestQuestionsMap.questions.keys.first()..DaltonismTestQuestionsMap.questions.keys.last()) {
                if (userAnswers[i] > 0) {
                    answerDeltas[DaltonismAnomalyType.NONE] = (answerDeltas[DaltonismAnomalyType.NONE] ?: 0) + 1
                }
                val variantAnswers = DaltonismTestQuestionsMap.questions[i]?.answerVariantsMap ?: mapOf()
                val booleanAnswers = DaltonismTestQuestionsMap.questions[i]?.answerBooleanMap ?: mapOf()
                for (answersMapKey in variantAnswers.keys) {
                    if (variantAnswers[answersMapKey]?.contains(userAnswers[i]) != true) {
                        answerDeltas[answersMapKey] = (answerDeltas[answersMapKey] ?: 0) + 1
                    }
                }
                for (answersMapKey in booleanAnswers.keys) {
                    if (booleanAnswers[answersMapKey] != (userAnswers[i] == 0)) {
                        answerDeltas[answersMapKey] = (answerDeltas[answersMapKey] ?: 0) + 1
                    }
                }
            }

            val resultValue = answerDeltas[DaltonismAnomalyType.NONE] ?: 0
            val resultType = if ((answerDeltas[DaltonismAnomalyType.NONE] ?: 0) <= NORMAL_BORDER) {
                DaltonismAnomalyType.NONE
            } else {
                val sortedResults = answerDeltas.toList().sortedBy { (_, value) -> value }
                if (sortedResults[0].first == DaltonismAnomalyType.NONE) {
                    sortedResults[1].first
                } else {
                    sortedResults[0].first
                }
            }

            val testResult = DaltonismTestResult(
                timestamp = System.currentTimeMillis(),
                errorsCount = resultValue,
                anomalyType = resultType
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(Screens.Screen.daltonismResult(resultValue, resultType.toString()))
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
