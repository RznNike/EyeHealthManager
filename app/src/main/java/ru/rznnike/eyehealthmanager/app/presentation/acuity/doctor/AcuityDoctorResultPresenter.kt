package ru.rznnike.eyehealthmanager.app.presentation.acuity.doctor

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.BasePresenter
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.domain.interactor.test.AddTestResultUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import ru.rznnike.eyehealthmanager.domain.utils.toFloatOrNullSmart
import java.time.Clock

@InjectViewState
class AcuityDoctorResultPresenter : BasePresenter<AcuityDoctorResultView>() {
    private val clock: Clock by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val eventDispatcher: EventDispatcher by inject()
    private val addTestResultUseCase: AddTestResultUseCase by inject()

    private var date: Long? = null
    private var leftEye: String = ""
    private var rightEye: String = ""

    override fun onFirstViewAttach() {
        populateData()
    }

    fun onLeftEyeValueChanged(newValue: String) {
        leftEye = newValue
        populateData()
    }

    fun onRightEyeValueChanged(newValue: String) {
        rightEye = newValue
        populateData()
    }

    fun onDateTimeSelected(newValue: Long) {
        date = newValue
        populateData()
    }

    fun onAddResult() {
        presenterScope.launch {
            val leftEyeFloat = leftEye.toFloatOrNullSmart()
            val rightEyeFloat = rightEye.toFloatOrNullSmart()
            when {
                date == null -> {
                    notifier.sendMessage(R.string.choose_date_and_time)
                }
                (leftEyeFloat == null) && (rightEyeFloat == null) -> {
                    notifier.sendMessage(R.string.error_enter_at_least_one_eye)
                }
                else -> {
                    viewState.setProgress(true)
                    val testResult = AcuityTestResult(
                        timestamp = date ?: clock.millis(),
                        symbolsType = AcuityTestSymbolsType.LETTERS_RU,
                        testEyesType = when {
                            leftEyeFloat == null -> TestEyesType.RIGHT
                            rightEyeFloat == null -> TestEyesType.LEFT
                            else -> TestEyesType.BOTH
                        },
                        dayPart = DayPart.MIDDLE,
                        resultLeftEye = leftEyeFloat?.let { (it * 100).toInt() },
                        resultRightEye = rightEyeFloat?.let { (it * 100).toInt() },
                        measuredByDoctor = true
                    )
                    addTestResultUseCase(testResult).process(
                        {
                            notifier.sendMessage(R.string.data_added)
                            viewState.routerFinishFlow()
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
    }

    private fun populateData() =
        viewState.populateData(
            date = date,
            leftEye = leftEye,
            rightEye = rightEye
        )
}
