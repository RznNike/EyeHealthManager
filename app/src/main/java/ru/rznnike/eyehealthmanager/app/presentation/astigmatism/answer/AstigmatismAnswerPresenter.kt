package ru.rznnike.eyehealthmanager.app.presentation.astigmatism.answer

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
import ru.rznnike.eyehealthmanager.domain.model.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType
import java.time.Clock

@InjectViewState
class AstigmatismAnswerPresenter : BasePresenter<AstigmatismAnswerView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    fun onSaveAnswer(answerLeftEye: Int, answerRightEye: Int) {
        presenterScope.launch {
            viewState.setProgress(true)
            val typeLeftEye = if (answerLeftEye == 0) AstigmatismAnswerType.OK else AstigmatismAnswerType.ANOMALY
            val typeRightEye = if (answerRightEye == 0) AstigmatismAnswerType.OK else AstigmatismAnswerType.ANOMALY

            val testResult = AstigmatismTestResult(
                timestamp = clock.millis(),
                resultLeftEye = typeLeftEye,
                resultRightEye = typeRightEye
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(Screens.Screen.astigmatismResult(typeLeftEye, typeRightEye))
                }, { error ->
                    errorHandler.proceed(error) {
                        notifier.sendMessage(it)
                    }
                }
            )
            eventDispatcher.sendEvent(AppEvent.JournalChanged)
        }
    }
}
