package ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.answer

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.answer.NearFarAnswerPresenter
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.answer.NearFarAnswerView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
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
        buttonBackToTest.setOnClickListener {
            onBackPressed()
        }
        radioGroupLeftEye.setOnCheckedChangeListener { _, _ ->
            setButtonSaveState()
        }
        radioGroupRightEye.setOnCheckedChangeListener { _, _ ->
            setButtonSaveState()
        }
        buttonSaveAnswer.setOnClickListener {
            val radioButtonLeftEye: AppCompatRadioButton = requireView().findViewById(radioGroupLeftEye.checkedRadioButtonId)
            val answerLeftEye = radioGroupLeftEye.indexOfChild(radioButtonLeftEye)
            val radioButtonRightEye: AppCompatRadioButton = requireView().findViewById(radioGroupRightEye.checkedRadioButtonId)
            val answerRightEye = radioGroupRightEye.indexOfChild(radioButtonRightEye)
            presenter.onSaveAnswer(answerLeftEye, answerRightEye)
        }
    }

    private fun setButtonSaveState() = binding.apply {
        buttonSaveAnswer.isEnabled = (radioGroupLeftEye.checkedRadioButtonId >= 0)
                && (radioGroupRightEye.checkedRadioButtonId >= 0)
    }
}
