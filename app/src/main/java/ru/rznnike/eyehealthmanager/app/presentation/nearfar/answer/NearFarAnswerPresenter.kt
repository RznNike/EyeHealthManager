package ru.rznnike.eyehealthmanager.app.presentation.nearfar.answer

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
import ru.rznnike.eyehealthmanager.domain.model.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

@InjectViewState
class NearFarAnswerPresenter : BasePresenter<NearFarAnswerView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    fun onSaveAnswer(answerLeftEye: Int, answerRightEye: Int) {
        presenterScope.launch {
            viewState.setProgress(true)
            val typeLeftEye = NearFarAnswerType.entries[answerLeftEye]
            val typeRightEye = NearFarAnswerType.entries[answerRightEye]

            val testResult = NearFarTestResult(
                timestamp = System.currentTimeMillis(),
                resultRightEye = typeRightEye,
                resultLeftEye = typeLeftEye
            )
            addTestResultUseCase(testResult).process(
                {
                    viewState.routerNewRootScreen(
                        Screens.Screen.nearFarResult(typeLeftEye, typeRightEye)
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
