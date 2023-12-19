package ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.answer

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.answer.NearFarAnswerPresenter
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.answer.NearFarAnswerView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.selectionIndex
import ru.rznnike.eyehealthmanager.databinding.FragmentNearFarAnswerBinding

class NearFarAnswerFragment : BaseFragment(R.layout.fragment_near_far_answer), NearFarAnswerView {
    @InjectPresenter
    lateinit var presenter: NearFarAnswerPresenter

    private val binding by viewBinding(FragmentNearFarAnswerBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonBackToTest.addSystemWindowInsetToMargin(bottom = true)
            buttonSaveAnswer.addSystemWindowInsetToMargin(bottom = true)
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
        buttonBackToTest.setOnClickListener {
            onBackPressed()
        }
        listOf(radioGroupLeftEye, radioGroupRightEye).forEach {
            it.setOnCheckedChangeListener { _, _ ->
                setButtonSaveState()
            }
        }
        buttonSaveAnswer.setOnClickListener {
            presenter.onSaveAnswer(
                answerLeftEye = radioGroupLeftEye.selectionIndex,
                answerRightEye = radioGroupRightEye.selectionIndex
            )
        }
    }

    private fun setButtonSaveState() = binding.apply {
        buttonSaveAnswer.isEnabled = (radioGroupLeftEye.checkedRadioButtonId >= 0)
                && (radioGroupRightEye.checkedRadioButtonId >= 0)
    }
}
