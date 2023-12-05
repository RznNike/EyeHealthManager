package ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.result

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.result.NearFarResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.result.NearFarResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.FragmentNearFarResultBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

class NearFarResultFragment : BaseFragment(R.layout.fragment_near_far_result), NearFarResultView {
    @InjectPresenter
    lateinit var presenter: NearFarResultPresenter

    @ProvidePresenter
    fun providePresenter() = NearFarResultPresenter(
        answerLeftEye = getParcelableArg(ANSWER_LEFT_EYE)!!,
        answerRightEye = getParcelableArg(ANSWER_RIGHT_EYE)!!
    )

    private val binding by viewBinding(FragmentNearFarResultBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_nearsightedness_farsightedness)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
    }

    override fun populateData(answerLeftEye: NearFarAnswerType, answerRightEye: NearFarAnswerType) {
        binding.apply {
            val leftEyeStatus = when (answerLeftEye) {
                NearFarAnswerType.RED_BETTER -> {
                    "<font color=\"#FE4C3F\">%s</font>".format(
                        getString(R.string.possible_myopia)
                    )
                }
                NearFarAnswerType.GREEN_BETTER -> {
                    "<font color=\"#FE4C3F\">%s</font>".format(
                        getString(R.string.possible_farsightedness)
                    )
                }
                NearFarAnswerType.EQUAL -> {
                    getString(R.string.normal_condition)
                }
            }

            val rightEyeStatus = when (answerRightEye) {
                NearFarAnswerType.RED_BETTER -> {
                    "<font color=\"#FE4C3F\">%s</font>".format(
                        getString(R.string.possible_myopia)
                    )
                }
                NearFarAnswerType.GREEN_BETTER -> {
                    getString(R.string.possible_farsightedness)
                }
                NearFarAnswerType.EQUAL -> {
                    getString(R.string.normal_condition)
                }
            }

            val result = "%s - %s<br>%s - %s".format(
                getString(R.string.left_eye),
                leftEyeStatus,
                getString(R.string.right_eye),
                rightEyeStatus
            ).toHtmlSpanned()
            textViewResult.text = result

            textViewMessage.setVisible(
                (answerLeftEye != NearFarAnswerType.EQUAL) || (answerRightEye != NearFarAnswerType.EQUAL)
            )
        }
    }

    companion object {
        const val ANSWER_LEFT_EYE = "ANSWER_LEFT_EYE"
        const val ANSWER_RIGHT_EYE = "ANSWER_RIGHT_EYE"
    }
}
