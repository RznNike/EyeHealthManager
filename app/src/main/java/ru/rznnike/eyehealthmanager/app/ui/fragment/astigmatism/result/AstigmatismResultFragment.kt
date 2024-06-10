package ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.astigmatism.result.AstigmatismResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.astigmatism.result.AstigmatismResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.FragmentAstigmatismResultBinding
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismAnswerType

class AstigmatismResultFragment : BaseFragment(R.layout.fragment_astigmatism_result),
    AstigmatismResultView {
    @InjectPresenter
    lateinit var presenter: AstigmatismResultPresenter

    @ProvidePresenter
    fun providePresenter() = AstigmatismResultPresenter(
        answerLeftEye = getParcelableArg(ANSWER_LEFT_EYE)!!,
        answerRightEye = getParcelableArg(ANSWER_RIGHT_EYE)!!
    )

    private val binding by viewBinding(FragmentAstigmatismResultBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonClose.addSystemWindowInsetToMargin(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.test_astigmatism)
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

    @SuppressLint("SetTextI18n")
    override fun populateData(
        answerLeftEye: AstigmatismAnswerType,
        answerRightEye: AstigmatismAnswerType
    ) {
        binding.apply {
            fun getStatusString(answer: AstigmatismAnswerType) = when (answer) {
                AstigmatismAnswerType.OK -> getString(R.string.normal_condition)
                AstigmatismAnswerType.ANOMALY -> "<font color=\"#FE4C3F\">%s</font>".format(
                    getString(R.string.possible_astigmatism)
                )
            }

            textViewResult.text = "%s - %s<br>%s - %s".format(
                getString(R.string.left_eye),
                getStatusString(answerLeftEye),
                getString(R.string.right_eye),
                getStatusString(answerRightEye)
            ).toHtmlSpanned()

            textViewMessage.setVisible(
                (answerLeftEye != AstigmatismAnswerType.OK) || (answerRightEye != AstigmatismAnswerType.OK)
            )
        }
    }

    companion object {
        const val ANSWER_LEFT_EYE = "ANSWER_LEFT_EYE"
        const val ANSWER_RIGHT_EYE = "ANSWER_RIGHT_EYE"
    }
}
