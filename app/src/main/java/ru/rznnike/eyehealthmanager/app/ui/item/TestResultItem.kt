package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.app.utils.extensions.getString
import ru.rznnike.eyehealthmanager.app.utils.extensions.resources
import ru.rznnike.eyehealthmanager.app.utils.extensions.setScaleOnTouch
import ru.rznnike.eyehealthmanager.app.utils.extensions.toHtmlSpanned
import ru.rznnike.eyehealthmanager.databinding.ItemTestResultBinding
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate

class TestResultItem(
    val testResult: TestResult,
    val scalable: Boolean = true
) : BaseBindingItem<ItemTestResultBinding>() {
    override var identifier = testResult.id

    override val type: Int = R.id.testResultItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemTestResultBinding.inflate(inflater, parent, false)

    override fun ItemTestResultBinding.bindView() {
        val testType: TestType
        val details: String
        when (testResult) {
            is AcuityTestResult -> {
                testType = TestType.ACUITY
                details = getAcuityTestDetails(testResult)
            }
            is AstigmatismTestResult -> {
                testType = TestType.ASTIGMATISM
                details = getAstigmatismTestDetails(testResult)
            }
            is NearFarTestResult -> {
                testType = TestType.NEAR_FAR
                details = getNearFarTestDetails(testResult)
            }
            is ColorPerceptionTestResult -> {
                testType = TestType.COLOR_PERCEPTION
                details = getColorPerceptionTestDetails(testResult)
            }
            is DaltonismTestResult -> {
                testType = TestType.DALTONISM
                details = getDaltonismTestDetails(testResult)
            }
            is ContrastTestResult -> {
                testType = TestType.CONTRAST
                details = getContrastTestDetails(testResult)
            }
            else -> {
                testType = TestType.ACUITY
                details = getString(R.string.error)
            }
        }

        textViewName.setText(testType.nameResId)
        textViewDetails.text = details.replace("\n", "<br>").toHtmlSpanned()
        imageViewIcon.setImageResource(testType.iconResId)

        textViewDate.text = testResult.timestamp.toDate(GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME)

        if (scalable) {
            root.setScaleOnTouch()
        }
    }

    private fun ItemTestResultBinding.getAcuityTestDetails(testResult: AcuityTestResult): String {
        val subheader = if (testResult.measuredByDoctor) {
            "<b>${getString(R.string.data_from_doctor)}</b>"
        } else {
            "%s: %s".format(
                getString(R.string.symbols_set),
                getString(testResult.symbolsType.nameResId)
            )
        }
        val eyesPart = when (testResult.testEyesType) {
            TestEyesType.BOTH -> "%s: %.1f\n%s: %.1f".format(
                getString(R.string.left_eye),
                (testResult.resultLeftEye ?: 0) / 100f,
                getString(R.string.right_eye),
                (testResult.resultRightEye ?: 0) / 100f
            )
            TestEyesType.LEFT -> "%s: %.1f".format(
                getString(R.string.left_eye),
                (testResult.resultLeftEye ?: 0) / 100f
            )
            TestEyesType.RIGHT -> "%s: %.1f".format(
                getString(R.string.right_eye),
                (testResult.resultRightEye ?: 0) / 100f
            )
        }
        return "%s\n%s".format(
            subheader,
            eyesPart
        )
    }

    private fun ItemTestResultBinding.getAstigmatismTestDetails(testResult: AstigmatismTestResult): String {
        val leftEyeStatus = testResult.resultLeftEye?.nameResId?.let { getString(it) } ?: "-"
        val rightEyeStatus = testResult.resultRightEye?.nameResId?.let { getString(it) } ?: "-"
        return "%s: %s\n%s: %s".format(
            getString(R.string.left_eye),
            leftEyeStatus,
            getString(R.string.right_eye),
            rightEyeStatus
        )
    }

    private fun ItemTestResultBinding.getNearFarTestDetails(testResult: NearFarTestResult): String {
        val leftEyeStatus = getString(testResult.resultLeftEye.nameResId)
        val rightEyeStatus = getString(testResult.resultRightEye.nameResId)
        return "%s: %s\n%s: %s".format(
            getString(R.string.left_eye),
            leftEyeStatus,
            getString(R.string.right_eye),
            rightEyeStatus
        )
    }

    private fun ItemTestResultBinding.getColorPerceptionTestDetails(testResult: ColorPerceptionTestResult) =
        "%s: %d/%d".format(
            getString(R.string.colors_recognized),
            testResult.recognizedColorsCount,
            testResult.allColorsCount
        )

    private fun ItemTestResultBinding.getDaltonismTestDetails(testResult: DaltonismTestResult) =
        "%s %s\n%s".format(
            testResult.errorsCount,
            resources.getQuantityString(R.plurals.errors, testResult.errorsCount),
            getString(testResult.anomalyType.nameResId)
        )

    private fun ItemTestResultBinding.getContrastTestDetails(testResult: ContrastTestResult) =
        "%s: %d%%".format(
            getString(R.string.contrast_result_header),
            testResult.recognizedContrast
        )
}