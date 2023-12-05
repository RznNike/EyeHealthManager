package ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.result

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.acuity.result.AcuityResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.acuity.result.AcuityResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.FragmentAcuityResultBinding
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AnalysisResult
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

class AcuityResultFragment : BaseFragment(R.layout.fragment_acuity_result), AcuityResultView {
    @InjectPresenter
    lateinit var presenter: AcuityResultPresenter

    @ProvidePresenter
    fun providePresenter() = AcuityResultPresenter(
        result = getParcelableArg(TEST_RESULT)!!
    )

    private val binding by viewBinding(FragmentAcuityResultBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.test_acuity)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
        buttonRedoTest.setOnClickListener {
            presenter.onRedoTest()
        }
        checkBoxApplyDynamicCorrections.setOnClickListener {
            presenter.onCheckBoxApplyDynamicCorrectionsClicked(checkBoxApplyDynamicCorrections.isChecked)
        }
    }

    override fun populateData(
        testResult: AcuityTestResult,
        analysisResult: AnalysisResult?,
        applyDynamicCorrections: Boolean
    ) {
        binding.apply {
            var resultLeftEye = (testResult.resultLeftEye ?: 0) / 100.0
            var resultRightEye = (testResult.resultRightEye ?: 0) / 100.0
            if (applyDynamicCorrections) {
                val correctionLeft: Double
                val correctionRight: Double
                when (testResult.dayPart) {
                    DayPart.BEGINNING -> {
                        correctionLeft = analysisResult?.leftEyeAnalysisResult?.dynamicCorrections?.beginning ?: 0.0
                        correctionRight = analysisResult?.rightEyeAnalysisResult?.dynamicCorrections?.beginning ?: 0.0
                    }
                    DayPart.MIDDLE -> {
                        correctionLeft = analysisResult?.leftEyeAnalysisResult?.dynamicCorrections?.middle ?: 0.0
                        correctionRight = analysisResult?.rightEyeAnalysisResult?.dynamicCorrections?.middle ?: 0.0
                    }
                    DayPart.END -> {
                        correctionLeft = analysisResult?.leftEyeAnalysisResult?.dynamicCorrections?.end ?: 0.0
                        correctionRight = analysisResult?.rightEyeAnalysisResult?.dynamicCorrections?.end ?: 0.0
                    }
                }
                resultLeftEye *= 1 + correctionLeft
                resultRightEye *= 1 + correctionRight
            }
            val resultText = when (testResult.testEyesType) {
                TestEyesType.BOTH -> {
                    "%s - %.1f<br>%s - %.1f".format(
                        getString(R.string.left_eye),
                        resultLeftEye,
                        getString(R.string.right_eye),
                        resultRightEye
                    )
                }
                TestEyesType.LEFT -> {
                    "%s - %.1f".format(
                        getString(R.string.left_eye),
                        resultLeftEye
                    )
                }
                TestEyesType.RIGHT -> {
                    "%s - %.1f".format(
                        getString(R.string.right_eye),
                        resultRightEye
                    )
                }
            }.toHtmlSpanned()
            textViewResult.text = resultText

            if (analysisResult?.lastResultRecognizedAsNoise == true) {
                layoutNoiseProcessing.setVisible()
            } else {
                layoutNoiseProcessing.setGone()
            }

            checkBoxApplyDynamicCorrections.isChecked = applyDynamicCorrections

            textViewMessage.setGone()
        }
    }

    companion object {
        const val TEST_RESULT = "TEST_RESULT"
    }
}
